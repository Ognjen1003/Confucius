# CONTENT - Confucius

Sve na engleskom. Nijedna tvrdnja bez razine (`TEXT` / `LEGGE` / `MODERN`). Nijedan U+2014.

## 1. `analects.json` (generira `tools/extract.py`)
```json
{ "version": 1, "sourceSha256": "…", "translator": "James Legge", "edition": "<s naslovnice filea>",
  "books": [ { "number": 2, "legge": "WEI CHANG", "chapters": [
     { "ref": "II.4", "paragraphs": ["1. The Master said, 'At fifteen, I had my mind bent on learning.", "2. …"],
       "terms": ["xue"], "people": [], "title": "From fifteen to seventy" } ] } ] }
```
- `paragraphs` je citat. `title` je **naš** (`MODERN`), kratak, i u čitaču se vidi kao naš.
- `terms` i `people` dolaze iz `terms.json`/`people.json`, potvrđeni ručno.

## 2. `commentary.json` (piše se, ulaz za extract.py)
Za **svako** poglavlje: `reflection` (2-4 rečenice), `question` (jedno pitanje), opcionalno `exercise`.
- **Originalno za svako poglavlje**, ne predložak s promijenjenom riječju.
- Kaže što poglavlje kaže i kako se može čitati; **ne pripisuje Konfuciju ono što tekst ne kaže**.
- Kod teških odlomaka: što stoji, u kojem društvu, mirno. Bez opravdavanja i bez osude.

## 3. `terms.json`, `people.json`, `course.json`
Po `THEORY.md §3-5`. `course.json`: 12 cjelina × 7 koraka, `kind` iz zatvorenog skupa
(`TERM`, `CHAPTER`, `CHAPTER_2`, `QUESTION`, `EXERCISE`, `TENSION`, `TRANSLATION`).

## 4. `articles.json`
| id | naslov | o čemu |
|---|---|---|
| `who_wrote_the_analects` | Who wrote the Analects | Učenici i njihovi učenici; „The Master said". **Najvažniji.** |
| `one_word_many_translations` | One word, many translations | 仁 i 君子 kod Leggea; zašto app pokazuje znak. |
| `legge` | James Legge | Misionar i sinolog, 25 godina na prijevodu; s poštovanjem. |
| `names_in_legge` | Names in Legge | Tsze-lu = Zilu; romanizacija 19. stoljeća. |
| `hard_passages` | Hard passages | Zašto teški odlomci ostaju u ciklusu, i kako ih app komentira. |
| `the_way_of_two_masters` | The Way of two masters | Usporedba s Taom; poveznica. |
| `examining_oneself` | Three questions a day | I.4; poveznica na Stoic. |
| `the_attributed_wings` | Confucius and the Changes | Predaja o Deset krila kao **predaja**, ne činjenica; poveznica na IChing. |
| `no_streaks_here` | No streaks here | Dvije rečenice. |

## 5. Što se nikad ne piše
- „Confucius wrote". „Ancient Chinese secret". Ocjena korisnika („you are a junzi").
- Savjet o konkretnim obiteljskim, bračnim ili poslovnim odlukama.
- Kineski znak koji nije provjeren u rječniku za taj pojam. Izmišljeni citat.
- Em dash.
