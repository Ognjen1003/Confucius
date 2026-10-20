"""Build res/raw/analects.json, commentary.json and texts_minor.json from tools/source/CFU.md.

Run from anywhere: python -I tools/extract.py [--refresh]
--refresh copies the snapshot again from the Obsidian vault first.

What the cleaning does, and why (docs/SOURCES.md §3):
- Part one of the file (up to the first '---') is Legge with the Chinese text interleaved page by
  page. Every line holding a CJK character or a 【】 mark is dropped; the Chinese runs between pages,
  so an English sentence often continues after a Chinese block and is simply joined back.
- A book runs from 'BOOK N.' to the next; a chapter from 'CHAPTER I.' / 'CHAP. N.' (also 'CHAP N.'
  without the dot, three times in the scan) to the next. Chapters are numbered by POSITION: the scan
  misprints five numerals (II.18, III.18 and III.23, IX.23, XIV.47), and every one of them is reported.
- Numbered sections ('1.', '2.' at the start of a line) become paragraphs; a numbering that does not
  run 1, 2, 3 stops the build.
- A line ending in a single hyphen after a letter is a word broken by the line ('music-' / 'master')
  and is joined without a space.
- '--' and ',-' become ' - ' (SAVJETI §10: the reader never sees an em dash, not even in a quotation).
- Part two (English only, 'translated by James Legge [1893]') has no chapter marks. It is compared
  word for word, book by book, with part one; the ratio and the differing stretches go to
  tools/extract_report.md. Nothing is taken from it.
- The Chinese of part one is kept internally, chapter by chapter where its 【第N章】 mark exists, only to
  confirm term tags (THEORY §3). It is never written to res/raw.
"""
import difflib
import hashlib
import json
import re
import shutil
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
VAULT = Path(r"C:\Users\ogmio\Lancelot\Obsidian\Obsidian Vault\sacred-texts\Confucianism\CFU\CFU.md")
SOURCE = ROOT / "tools" / "source" / "CFU.md"
RAW = ROOT / "app" / "src" / "main" / "res" / "raw"
COMMENTARY = ROOT / "tools" / "commentary"
REPORT = ROOT / "tools" / "extract_report.md"

EDITION = "The Chinese Classics, vol. I, 1893"
CREDIT = "Tr. James Legge, The Chinese Classics (1893)"
EXPECTED = [16, 24, 26, 26, 27, 28, 37, 21, 30, 18, 25, 24, 30, 47, 41, 14, 26, 11, 25, 3]

# Five misreadings of the scan in part one. The first three are confirmed by part two of the same file and
# by the sense of the sentence; III.19 doubles two words in BOTH parts and is put right by the sense alone;
# I.5 drops an opening quotation mark that part two keeps.
# Written out rather than guessed; each is reported. (ref, wrong, right)
CORRECTIONS = [
    ("II.14", "catholic and no partisan", "catholic and not partisan"),
    ("III.4", "deep sorrow than a minute attention", "deep sorrow than in minute attention"),
    ("IV.13", "'Is a prince is able", "'If a prince is able"),
    ("III.19", "according to according to", "according to"),
    ("I.5", "The Master said, To rule", "The Master said, 'To rule"),
    ("XII.4", "said Nui;", "said Niu;"),
    ("VII.13", "'I did not think'' he said", "'I did not think,' he said"),
    # The English names the wrong disciple twice; the Chinese of the same file reads 子游, Tsze-yu.
    ("XIX.14", "Tsze-hsia said, 'Mourning", "Tsze-yu said, 'Mourning"),
    ("XIX.15", "Tsze-hsia said, 'My friend Chang", "Tsze-yu said, 'My friend Chang"),
]

# XIII.20 is the one chapter whose section numbers the scan breaks: it omits '2.' and prints '3.' twice,
# so the four sections arrive as one paragraph. They are split again where the numbers stand, and renumbered.
SPLITS = {"XIII.20": [" 3. Tsze-kung pursued", " 3. Again the disciple", " 4. Tsze-kung finally"]}

CJK = re.compile(r"[\u3000-\u303f\u3400-\u9fff\uff00-\uffef\u2e80-\u2fdf【】]")
BOOK = re.compile(r"^BOOK ([IVXL]+)\.\s+(.*?)\.?\s*$")
CHAP = re.compile(r"^(?:CHAPTER|CHAP)\.?\s+([IVXLC]+)\.?\s+(.*)$")
SECTION = re.compile(r"^(\d{1,2})\.\s+(.*)$")
CN_MARK = re.compile(r"【第?([一二三四五六七八九十百]+)章】")


def roman(s):
    v = {"I": 1, "V": 5, "X": 10, "L": 50, "C": 100}
    total = 0
    for i, c in enumerate(s):
        x = v[c]
        total += -x if i + 1 < len(s) and v[s[i + 1]] > x else x
    return total


def to_roman(n):
    out = ""
    for value, letters in [(50, "L"), (40, "XL"), (10, "X"), (9, "IX"), (5, "V"), (4, "IV"), (1, "I")]:
        while n >= value:
            out += letters
            n -= value
    return out


def chinese_number(s):
    digits = {"一": 1, "二": 2, "三": 3, "四": 4, "五": 5, "六": 6, "七": 7, "八": 8, "九": 9}
    if "十" not in s:
        # 四七 is the scan's shorthand for forty-seven
        return int("".join(str(digits[c]) for c in s)) if len(s) > 1 else digits[s]
    tens, _, units = s.partition("十")
    return (digits[tens] if tens else 1) * 10 + (digits[units] if units else 0)


def clean(text):
    text = text.replace("\u2014", " - ").replace("--", " - ").replace(",-", ", - ")
    text = re.sub(r"(?<=[;:!?.'\"])-(?=[A-Za-z])", " - ", text)
    text = re.sub(r"\s+", " ", text).strip()
    text = text.replace(" ,", ",").replace(" ;", ";")
    return text


def join(lines):
    out = ""
    for line in lines:
        line = line.strip()
        if not line:
            continue
        if not out:
            out = line
        elif re.search(r"[A-Za-z]-$", out) and not out.endswith("--"):
            out += line
        else:
            out += " " + line
    return clean(out)


def parse_part_one(lines, report):
    books, book, chapter = [], None, None
    chinese = {}
    cn_buffer = []

    def close_chapter():
        if chapter is None:
            return
        sections, current = [], []
        for raw in chapter["lines"]:
            m = SECTION.match(raw.strip())
            if m and (int(m.group(1)) == len(sections) + 2 or (not sections and not current and m.group(1) == "1")):
                if current:
                    sections.append(current)
                current = [m.group(0)]
            else:
                current.append(raw)
        if current:
            sections.append(current)
        paragraphs = [join(s) for s in sections]
        numbered = [p for p in paragraphs if SECTION.match(p)]
        if numbered:
            nums = [int(SECTION.match(p).group(1)) if SECTION.match(p) else 0 for p in paragraphs]
            if len(nums) > 1 and nums[0] == 0 and nums[1:] == list(range(2, len(nums) + 1)):
                # The scan drops the '1.' of a numbered chapter once (XVII.5); it is restored and reported.
                paragraphs[0] = "1. " + paragraphs[0]
                nums[0] = 1
                report.append(f"- {chapter['ref']}: the scan omits the section number '1.'; restored.")
            if nums != list(range(1, len(nums) + 1)):
                sys.exit(f"section numbering broken in {chapter['ref']}: {nums}")
        book["chapters"].append({"ref": chapter["ref"], "number": chapter["number"], "paragraphs": paragraphs})

    for idx, line in enumerate(lines, 1):
        if CJK.search(line):
            if book is not None:
                cn_buffer.append(line.strip())
            continue
        m = BOOK.match(line)
        if m:
            close_chapter()
            chapter = None
            if book is not None:
                chinese[book["number"]] = "".join(cn_buffer)
            cn_buffer = []
            n = roman(m.group(1))
            if n != len(books) + 1:
                sys.exit(f"book {n} out of order at line {idx}")
            book = {"number": n, "roman": m.group(1), "legge": m.group(2).strip(), "chapters": []}
            books.append(book)
            continue
        if book is None:
            continue
        m = CHAP.match(line)
        if m:
            close_chapter()
            number = len(book["chapters"]) + 1
            printed = roman(m.group(1))
            if printed != number:
                report.append(f"- {book['roman']}.{number}: the scan prints `{m.group(1)}` (line {idx}); numbered by position.")
            chapter = {"ref": f"{book['roman']}.{number}", "number": number, "lines": [m.group(2)]}
            continue
        if line.strip() == "---":
            break
        if chapter is not None:
            chapter["lines"].append(line)
    close_chapter()
    if book is not None:
        chinese[book["number"]] = "".join(cn_buffer)
    return books, chinese


def chinese_by_chapter(chinese):
    """{(book, chapter): Chinese text} for every chapter whose 【第N章】 mark the scan carries."""
    out = {}
    for b, text in chinese.items():
        parts = CN_MARK.split(text)
        for i in range(1, len(parts) - 1, 2):
            out[(b, chinese_number(parts[i]))] = re.sub(r"【[^】]*】", "", parts[i + 1])
    return out


def part_two_books(lines):
    start = next(i for i, l in enumerate(lines) if "translated by James Legge [1893]" in l)
    books, current = {}, None
    for line in lines[start + 1:]:
        s = line.strip()
        if s == "THE END":
            break
        if re.fullmatch(r"\d{1,2}", s):
            current = int(s)
            books[current] = []
            continue
        if current:
            books[current].append(s)
    return {b: join(v) for b, v in books.items()}


def words(text):
    return re.findall(r"[a-z]+", text.lower())


def compare(books, second, report):
    report.append("\n## Part one against part two, word for word\n")
    report.append("| book | words (one) | words (two) | ratio | differing stretches |")
    report.append("|---|---|---|---|---|")
    detail = []
    for b in books:
        one = words(" ".join(p for c in b["chapters"] for p in c["paragraphs"]))
        two = words(second.get(b["number"], ""))
        sm = difflib.SequenceMatcher(None, one, two, autojunk=False)
        ops = [op for op in sm.get_opcodes() if op[0] != "equal"]
        report.append(f"| {b['roman']} | {len(one)} | {len(two)} | {sm.ratio():.4f} | {len(ops)} |")
        for tag, i1, i2, j1, j2 in ops[:40]:
            detail.append(f"- {b['roman']} {tag}: `{' '.join(one[i1:i2])}` / `{' '.join(two[j1:j2])}`")
    report.append("\nDifferences (first 40 per book; part one is what ships):\n")
    report.extend(detail)


def minor_text(lines, title):
    start = next(i for i, l in enumerate(lines) if l.strip() == title and "translated by James Legge" in lines[i + 2])
    body = []
    for line in lines[start + 3:]:
        if line.strip() == "THE END":
            break
        body.append(line.rstrip())
    paragraphs, current = [], []
    for line in body:
        s = line.strip()
        if not s:
            continue
        if s.isupper() and len(s) > 8:
            if current:
                paragraphs.append(join(current))
                current = []
            small = {"of", "the", "and", "in", "on"}
            paragraphs.append("## " + " ".join(w.lower() if i and w.lower() in small else w.capitalize()
                                                for i, w in enumerate(s.split())))
            continue
        current.append(s)
        if len(s) < 60 and re.search(r"[.?!:\"']$", s):
            paragraphs.append(join(current))
            current = []
    if current:
        paragraphs.append(join(current))
    return paragraphs


def load_commentary():
    out = {}
    for f in sorted(COMMENTARY.glob("book*.json")):
        for ref, entry in json.loads(f.read_text(encoding="utf-8")).items():
            if ref in out:
                sys.exit(f"{ref} written twice")
            out[ref] = entry
    return out


def tag(books, cn, report):
    """Term tags: the Legge word is a candidate, the Chinese confirms where it exists, terms.json decides."""
    terms = json.loads((RAW / "terms.json").read_text(encoding="utf-8"))["terms"]
    people = json.loads((RAW / "people.json").read_text(encoding="utf-8"))["people"]
    confirmed = {t["id"]: set(t["chapters"]) for t in terms}
    review = {}
    for b in books:
        for c in b["chapters"]:
            text = " ".join(c["paragraphs"])
            zh = cn.get((b["number"], c["number"]))
            for t in terms:
                if re.search(t["pattern"], text, re.I):
                    has = None if zh is None else any(ch in zh for ch in t["chinese"])
                    review.setdefault(t["id"], []).append({"ref": c["ref"], "chinese": has})
            c["terms"] = [t["id"] for t in terms if c["ref"] in confirmed[t["id"]]]
            c["people"] = [p["id"] for p in people if c["ref"] in p["chapters"]]
    (ROOT / "tools" / "term_candidates.json").write_text(json.dumps(review, ensure_ascii=False, indent=1), encoding="utf-8")
    refs = {c["ref"] for b in books for c in b["chapters"]}
    for t in terms:
        bad = confirmed[t["id"]] - refs
        if bad:
            sys.exit(f"term {t['id']} names unknown chapters {sorted(bad)}")
    for p in people:
        bad = set(p["chapters"]) - refs
        if bad:
            sys.exit(f"person {p['id']} names unknown chapters {sorted(bad)}")
        for ref in p["chapters"]:
            c = next(c for b in books for c in b["chapters"] if c["ref"] == ref)
            if not any(re.search(r"\b" + re.escape(f) + r"\b", " ".join(c["paragraphs"])) for f in p["forms"]):
                sys.exit(f"person {p['id']} not named in {ref}")
    report.append("\n## Term tags\n")
    for t in terms:
        cand = review.get(t["id"], [])
        report.append(f"- `{t['id']}`: {len(confirmed[t['id']])} confirmed, {len(cand)} candidates by Legge's word, "
                      f"{sum(1 for x in cand if x['chinese'] is False)} of them refused by the Chinese.")


def main():
    if "--refresh" in sys.argv:
        shutil.copyfile(VAULT, SOURCE)
    data = SOURCE.read_bytes()
    sha = hashlib.sha256(data).hexdigest()
    lines = data.decode("utf-8").split("\n")
    report = ["# extract.py report", "", f"Input `tools/source/CFU.md`, SHA-256 `{sha}`.", "", "## Numerals the scan misprints", ""]

    books, chinese = parse_part_one(lines, report)
    report.append("\n## Corrections\n")
    for ref, wrong, right in CORRECTIONS:
        c = next(c for b in books for c in b["chapters"] if c["ref"] == ref)
        hits = [i for i, p in enumerate(c["paragraphs"]) if wrong in p]
        if len(hits) != 1:
            sys.exit(f"correction for {ref} does not apply once")
        c["paragraphs"][hits[0]] = c["paragraphs"][hits[0]].replace(wrong, right)
        report.append(f"- {ref}: `{wrong}` -> `{right}`" + ({"III.19": " (both parts double the words).", "XIX.14": " (the Chinese reads 子游).",
                      "XIX.15": " (the Chinese reads 子游)."}.get(ref, " (as part two reads).")))
    for ref, marks in SPLITS.items():
        c = next(c for b in books for c in b["chapters"] if c["ref"] == ref)
        text = " ".join(c["paragraphs"])
        parts = []
        for mark in marks:
            head, sep, text = text.partition(mark)
            if not sep:
                sys.exit(f"split mark {mark!r} not found in {ref}")
            parts.append(head)
            text = mark.strip().split(" ", 1)[1] + text
        parts.append(text)
        c["paragraphs"] = [parts[0]] + [f"{i}. {p}" for i, p in enumerate(parts[1:], 2)]
        report.append(f"- {ref}: the scan omits '2.' and repeats '3.'; split into {len(parts)} sections and renumbered.")
    counts = [len(b["chapters"]) for b in books]
    if counts != EXPECTED:
        sys.exit(f"chapter counts {counts} differ from {EXPECTED}")
    report.append(f"\n{len(books)} books, {sum(counts)} chapters: {counts}.")

    cn = chinese_by_chapter(chinese)
    report.append(f"Chinese text located for {len(cn)} of {sum(counts)} chapters (used only to confirm term tags).")
    compare(books, part_two_books(lines), report)

    tag(books, cn, report)

    commentary = load_commentary()
    refs = [c["ref"] for b in books for c in b["chapters"]]
    missing = [r for r in refs if r not in commentary]
    extra = sorted(set(commentary) - set(refs))
    if extra:
        sys.exit(f"commentary for unknown chapters: {extra}")
    for ref, e in commentary.items():
        for k in ("title", "reflection", "question"):
            if not e.get(k, "").strip():
                sys.exit(f"{ref}: {k} missing")
    for b in books:
        for c in b["chapters"]:
            if c["ref"] in commentary:
                c["title"] = commentary[c["ref"]]["title"]
    report.append(f"\nCommentary written for {len(refs) - len(missing)} of {len(refs)} chapters.")
    if missing:
        report.append("Missing: " + ", ".join(missing))

    analects = {"version": 1, "sourceSha256": sha, "translator": "James Legge", "edition": EDITION,
                "credit": CREDIT, "books": books}
    RAW.mkdir(parents=True, exist_ok=True)
    (RAW / "analects.json").write_text(json.dumps(analects, ensure_ascii=False, indent=1), encoding="utf-8")
    comm = {"version": 1, "chapters": [dict(ref=r, **{k: commentary[r][k] for k in ("reflection", "question", "exercise")
                                                      if commentary[r].get(k)}) for r in refs if r in commentary]}
    (RAW / "commentary.json").write_text(json.dumps(comm, ensure_ascii=False, indent=1), encoding="utf-8")
    minor = {"version": 1, "sourceSha256": sha, "texts": [
        {"id": "great_learning", "title": "The Great Learning", "credit": "Tr. James Legge (1893)",
         "paragraphs": minor_text(lines, "THE GREAT LEARNING")},
        {"id": "doctrine_of_the_mean", "title": "The Doctrine of the Mean", "credit": "Tr. James Legge (1893)",
         "paragraphs": minor_text(lines, "THE DOCTRINE OF THE MEAN")},
    ]}
    (RAW / "texts_minor.json").write_text(json.dumps(minor, ensure_ascii=False, indent=1), encoding="utf-8")

    for f in ("analects.json", "commentary.json", "texts_minor.json"):
        if "\u2014" in (RAW / f).read_text(encoding="utf-8"):
            sys.exit(f"em dash in {f}")
    REPORT.write_text("\n".join(report) + "\n", encoding="utf-8")
    print(f"{sum(counts)} chapters, commentary {len(refs) - len(missing)}/{len(refs)}; report in {REPORT.name}")


if __name__ == "__main__":
    main()
