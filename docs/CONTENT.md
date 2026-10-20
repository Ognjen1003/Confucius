# CONTENT - Confucius

Sve na engleskom. Nijedna tvrdnja bez razine (`TEXT` / `LEGGE` / `MODERN`). Nijedan U+2014.

## 1. `analects.json` (generira `tools/extract.py`, ne uređuje se ručno)
```json
{ "version": 1, "sourceSha256": "d042…", "translator": "James Legge", "edition": "The Chinese Classics, vol. I, 1893",
  "credit": "Tr. James Legge, The Chinese Classics (1893)",
  "books": [ { "number": 2, "roman": "II", "legge": "WEI CHANG", "chapters": [
     { "ref": "II.4", "number": 4, "paragraphs": ["1. The Master said, 'At fifteen, I had my mind bent on learning.", "2. …"],
       "terms": ["xue"], "people": [], "title": "From fifteen to seventy" } ] } ] }
```
- `paragraphs` je citat. `title` je **naš** (`MODERN`), iz `tools/commentary/`, i čitač to kaže.
- `terms`/`people` dolaze iz `terms.json`/`people.json`.

## 2. Komentar: `tools/commentary/bookNN.json` -> `res/raw/commentary.json`
```json
{ "II.4": { "title": "From fifteen to seventy", "reflection": "…", "question": "…?", "exercise": "…" } }
```
Za **svako** poglavlje `title`, `reflection` (2-4 rečenice), `question`; `exercise` povremeno (21).
- Originalno za svako poglavlje; `CommentaryTest` ruši build ako se ponovi rečenica, pitanje ili naslov.
- Kaže što poglavlje kaže; ne pripisuje Konfuciju ono što tekst ne kaže; kad Legge i drugi čitaju
  različito, kaže to.
- Teški odlomci (XVII.25, XIII.18, VIII.9, VIII.20, XVII.21, XIII.4): što stoji i u kojem društvu, mirno.

## 3. `terms.json`, `people.json`, `course.json`
- `terms.json`: `id, character, pinyin, legge[], gloss (MODERN), chapters[], related[]` (+ `pattern`,
  `chinese` samo za alat). Svaka Leggeova riječ mora stajati u nekom od poglavlja pojma (test).
- `people.json`: `id, legge, pinyin, forms[], about (MODERN), chapters[]`.
- `course.json`: 12 cjelina × 7 koraka, `kind` iz zatvorenog skupa
  (`TERM`, `CHAPTER`, `CHAPTER_2`, `QUESTION`, `EXERCISE`, `TENSION`, `TRANSLATION`), `chapter` opcionalno.

## 4. `articles.json` (10)
| id | naslov |
|---|---|
| `who_wrote_the_analects` | Who wrote the Analects |
| `one_word_many_translations` | One word, many translations |
| `legge` | James Legge |
| `names_in_legge` | Names in Legge |
| `hard_passages` | Hard passages |
| `the_way_of_two_masters` | The Way of two masters (→ Tao) |
| `examining_oneself` | Three questions a day (→ Stoic) |
| `the_attributed_wings` | Confucius and the Changes (→ IChing; predaja kao predaja) |
| `the_four_books` | The Four Books (Great Learning i Mean u Loreu) |
| `no_streaks_here` | No streaks here |

## 5. Što se nikad ne piše
- „Confucius wrote" (osim kao predaja o Deset krila, izričito označena). „Ancient Chinese secret".
- Ocjena korisnika („you are a junzi"), savjet o obiteljskim, bračnim ili poslovnim odlukama.
- Kineski znak koji nije provjeren za taj pojam. Izmišljeni citat. Em dash.
