# THEORY - Confucius: korpus, pojmovi, tečaj

Prvo brojanje 19. 10. 2026.; **ispravljeno i zaključano 20. 10. 2026.** nakon što je `tools/extract.py`
prošao cijeli file. Izvještaj alata: `tools/extract_report.md`.

## 1. Korpus

Izvor: `Confucianism/CFU/CFU.md` (Legge, *The Chinese Classics*, vol. I: Analects, Great Learning,
Doctrine of the Mean), snapshot u `tools/source/CFU.md`, SHA-256
`d04250a7f59f69c1c307fdf57a1da7ecfb013ccb6faeb9117ab6b67741c99bbe`.

| što | konačno | napomena |
|---|---|---|
| knjige | **20** | `BOOK I.` ... `BOOK XX.` |
| poglavlja | **499** | |
| po knjizi | I 16, II 24, III 26, IV 26, V 27, VI 28, **VII 37**, VIII 21, IX 30, **X 18**, XI 25, XII 24, XIII 30, XIV 47, XV 41, XVI 14, XVII 26, XVIII 11, XIX 25, **XX 3** | |
| kineski tekst nađen | 372 od 499 poglavlja | samo interno, za potvrdu oznaka pojmova; ne ide u app |

**Zašto je prvo brojanje (496) bilo krivo:** strogi regex nije hvatao `CHAP XVII` / `CHAP II.` /
`CHAP III.` bez točke iza `CHAP` (redci 1153, 1658, 4200). S tim oznakama VII ima 37, X 18, XX 3, što je
Leggeova podjela. Book XX ima tri poglavlja, kako je i očekivano.

**Poglavlja se broje po položaju**, ne po otisnutom rimskom broju: sken krivo otiskuje pet brojeva
(II.18 i III.18 kao `XVII`, III.23 kao `XXXII`, IX.23 kao `XXV`, XIV.47 kao `XLVI`). Alat ih javlja.

**Ispravci teksta** (svi ispisani u `extract.py` kao `CORRECTIONS`/`SPLITS` i u izvještaju; ništa se ne
pogađa):

| poglavlje | sken | ispravak | dokaz |
|---|---|---|---|
| II.14 | catholic and **no** partisan | **not** | drugi dio filea |
| III.4 | than **a** minute attention | than **in** minute | drugi dio |
| IV.13 | '**Is** a prince is able | '**If** a prince | drugi dio |
| III.19 | according to according to | according to | smisao (oba dijela griješe isto) |
| I.5 | said, To rule | said, 'To rule | drugi dio |
| XII.4 | said Nui | said Niu | drugi dio |
| VII.13 | 'I did not think'' he said | 'I did not think,' he said | drugi dio |
| XIX.14, XIX.15 | **Tsze-hsia** said | **Tsze-yu** said | kineski u istom fileu: 子游曰 |
| XVII.5 | nedostaje „1." | dodan | slijed 2., 3. |
| XIII.20 | nema „2.", „3." dvaput | razdvojeno u 4 sekcije | jedini takav slučaj (provjereno skriptom) |

Govornici su provjereni kroz kineski za sve poglavlja s kineskim tekstom (曾子, 有子, 子夏, 子游, 子張, 子貢):
nakon ispravka nema nijednog neslaganja.

Usporedba s drugim dijelom filea (1893, samo engleski): po knjigama 98.6 - 99.9 % podudaranja riječi;
ostale razlike su pravopis (practise/practice, labour/labor) ili su greške drugog dijela.

Tekst poglavlja: od oznake do iduće, bez redaka s CJK znakovima i `【…】`, sa zadržanim brojevima stavaka
(`1.`, `2.`) kao Leggeovim tekstom. `--` i `,-` postaju ` - `.

## 2. Dnevni ciklus

`floorMod(localEpochDay - epochDay(2026-01-01), 499) + 1`, kroz cijele Analekte redom (I.1 do XX.3).

| datum | poglavlje |
|---|---|
| 31. 12. 2025. | XX.3 |
| 1. 1. 2026. | I.1 |
| 2. 1. 2026. | I.2 |
| 20. 10. 2026. | XII.15 |
| 14. 5. 2027. | XX.3 |
| 15. 5. 2027. | I.1 |

Teški odlomci (XVII.25, XIII.18, VIII.9...) su u ciklusu (`CLAUDE.md`, zamka 3). `DailyTest` pinira sidra.

## 3. Dvanaest pojmova

Postupak (ručna potvrda, 20. 10. 2026.): alat označi kandidate po **Leggeovoj riječi**; gdje postoji
kineski tekst poglavlja, odlučuje znak; za poglavlja bez kineskog odluka je donesena čitanjem i
upisana u `tools/term_decisions.json` (npr. „virtue" u IV.25 je 德, u VI.21 仁, u XIX.22 nijedan).
`terms.json` je konačna lista. `related` su poglavlja koja kažu ideju bez riječi, i app to kaže.

| id | znak | Leggeove riječi (stvarno u tekstu) | poglavlja | related |
|---|---|---|---|---|
| `ren` | 仁 | perfect virtue, virtue, virtuous, perfectly virtuous, benevolent, benevolence, true virtue | 56 | |
| `li` | 禮 | propriety, rules of propriety, ceremonies, rites | 39 | |
| `yi` | 義 | righteousness, what is right, righteous | 17 | |
| `zhi` | 知 | knowledge, wise, wisdom | 23 | |
| `xin` | 信 | sincerity, sincere, faithfulness, truthfulness, truthful, good faith, confidence | 25 | |
| `xiao` | 孝 | filial piety, filial | 14 | |
| `junzi` | 君子 | superior man, **man of complete virtue** (I.1), **scholar** (I.8), **accomplished scholar** (II.12), **Chun-tsze** (III.7) | 73 | |
| `xue` | 學 | learning, learn, learned, study | 39 | |
| `shu` | 恕 | reciprocity (XV.23), the benevolent exercise of them to others (IV.15) | 2 | V.11, XII.2 |
| `zhong` | 忠 | faithfulness, faithful, loyal, loyalty, devotion of soul, true to the principles of our nature | 12 | |
| `de` | 德 | virtue, virtuous | 30 | |
| `zhengming` | 正名 | rectify names, rectification | 1 (XIII.3) | XII.11, VI.23 |

- Čitač prikazuje samo riječi koje stoje **u tom poglavlju** (`Term.wordsIn`): u IV.15 je zhong „true to the
  principles of our nature", ne cijela lista.
- Znak i pinyin su `MODERN` (rječnik). Leggeove riječi su `LEGGE`.

## 4. Tečaj „Twelve words"

`PracticeCourse(id = "twelve_words")`, 12 cjelina redom iz §3, svaka 7 koraka u fiksnom redu
`TERM, CHAPTER, CHAPTER_2, QUESTION, EXERCISE, TENSION, TRANSLATION` (84 koraka).

- `CHAPTER` je uvijek poglavlje u kojem riječ stoji (`CourseTest`); `CHAPTER_2` i `TENSION` mogu biti
  `related` poglavlje (zhengming: XII.11; shu: V.11) i tekst to kaže.
- Napredak po korisniku: „Word 3 of 12 · step 5 of 7". „Next step" zove `Practice.advance` +
  `markPracticed` (jednom po danu). Nakon zadnjeg koraka `finished` i „Begin again".
- xiao: tekst kaže što tekst kaže; vježba je opažanje primljene brige, bez upute o obitelji.

## 5. Ljudi u Analektima (`people.json`, 17)

Oblik imena mora stajati u svakom navedenom poglavlju (`TermsAndPeopleTest`). Homonimi isključeni:
Hui iz Liu-hsije (XV.13, XVIII.2, XVIII.8), Tsze-yu iz Tung-lija (XIV.9). „Yu" kao osobno ime Zilua nije
oblik (dvoznačno s „philosopher Yu").

Yan Hui (21), Zilu (33), Zigong (38), Zengzi (14), Youzi (3), Zixia (19), Zizhang (16), Ran You (9),
Zai Wo (5), Min Ziqian (4), Fan Chi (6), Ziyou (8), Zhonggong (5), Gongxi Chi (3), Ji Kangzi (7),
Duke Ai (4), Guan Zhong (4).

## 6. Testovi

JVM (35): `AnalectsDataTest` (20 knjiga, 499, XX ima 3, strogi redoslijed, nema CJK/U+2014/`--`, sekcije
1, 2, 3, ispravci u bundleu, SHA-256 = snapshot), `CommentaryTest` (svako poglavlje, bez duplikata, zabranjene
fraze), `DailyTest` (sidra, ponoć u Zagrebu, cijeli ciklus redom, teški odlomci u ciklusu), `CourseTest`,
`TermsAndPeopleTest`, `ArticlesTest` (članci iz CONTENT, poveznice imaju članak, nema U+2014 nigdje u
`res/` i `java/`), `SearchTest`.

Instrumentirani (4, `DeviceTest`): resursi se učitavaju, widget pokazuje današnje poglavlje bez ikakvog unosa,
podsjetnik objavljuje obavijest s naslovom „Today: Analects I.4", `arcadia://confucius?chapter=II.4`
otvara čitač.
