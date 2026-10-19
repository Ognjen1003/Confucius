# THEORY - Confucius: korpus, pojmovi, tečaj

Provjereno u vaultu **19. 10. 2026.**, prebrojano skriptom.

## 1. Korpus i sidra

Izvor: `Confucianism/CFU/CFU.md` (Legge, *The Chinese Classics*, vol. I: Analects, Great Learning,
Doctrine of the Mean).

| što | nalaz 19. 10. | napomena |
|---|---|---|
| knjige Analekata | **20** (`BOOK I.` ... `BOOK XX.`, redci 25-4112) | |
| oznake poglavlja u prvom dijelu | **496** (`CHAPTER I.` za prvo, `CHAP. N.` za ostala) | |
| po knjizi | I 16, II 24, III 26, IV 26, V 27, VI 28, VII 36, VIII 21, IX 30, X 17, XI 25, XII 24, XIII 30, XIV 47, XV 41, XVI 14, XVII 26, XVIII 11, XIX 25, XX 2 | |
| kineske oznake `【第…章】` | 193 (10 po knjizi, 3 u XX) | samo djelomično; ne ide u v1 |

**Pažnja: Book XX ima tri poglavlja**, a nađene su dvije oznake. Barem jedna oznaka u skenu nedostaje
ili je drugog oblika (možda i u VII i X). **Alat (`tools/extract.py`) mora uskladiti oznake s
drugim dijelom filea i s kineskim oznakama, ispisati svako odstupanje i stati ako ga ne može
razriješiti.** Konačni broj poglavlja `N` i sidra dnevnog ciklusa upisuju se ovdje tek nakon toga.

Tekst poglavlja: od oznake do iduće, **bez kineskih redaka** (redci s CJK znakovima i `【…】`),
s brojevima stavaka (`1.`, `2.`) zadržanima kao dio Leggeova teksta. Legge piše `--` i `,-`:
u izlazu ` - `.

## 2. Dnevni ciklus

`floorMod(localEpochDay - epochDay(2026-01-01), N) + 1`, kroz cijele Analekte redom (I.1 do XX.3).
Upiši sidra (1. 1. 2026. = I.1; dan N-1 nakon toga = XX.3; 31. 12. 2025. = XX.3).
Teški odlomci su u ciklusu (`CLAUDE.md`, zamka 3).

## 3. Dvanaest pojmova

| id | znak | pinyin | Leggeove riječi (provjeri u tekstu i zapiši koje stvarno koristi) |
|---|---|---|---|
| `ren` | 仁 | ren | perfect virtue, benevolence, virtue, virtuous |
| `li` | 禮 | li | propriety, the rules of propriety, ceremonies |
| `yi` | 義 | yi | righteousness, what is right |
| `zhi` | 知 | zhi | knowledge, wisdom, the wise |
| `xin` | 信 | xin | sincerity, faithfulness, truthful |
| `xiao` | 孝 | xiao | filial piety, filial |
| `junzi` | 君子 | junzi | the superior man |
| `xue` | 學 | xue | learning, to learn |
| `shu` | 恕 | shu | reciprocity |
| `zhong` | 忠 | zhong | faithfulness, loyalty, doing one's utmost |
| `de` | 德 | de | virtue |
| `zhengming` | 正名 | zhengming | rectify names (XIII.3) |

- **Pripadnost poglavlja pojmu** se ne pogađa: alat označi poglavlja u kojima se javlja **Leggeova
  riječ** za pojam, a ti ručno potvrdiš (popis u `res/raw/terms.json`). Riječ „virtue" javlja se i
  za 德 i za 仁; ondje odluku donosi kineski tekst gdje ga ima, inače poglavlje ostaje bez oznake.
- Znak i pinyin su `MODERN` (rječnik), jer kineski tekst u vaultu nije potpun.

## 4. Tečaj „Twelve words"

`PracticeCourse(id = "twelve_words")`, 12 cjelina (redom iz §3) po 7 koraka:

```
dan 1  pojam: znak, pinyin, Leggeove riječi, jedna rečenica sto znaci (MODERN)
dan 2  glavno poglavlje za pojam (TEXT) + komentar
dan 3  drugo poglavlje, gdje se pojam pojavljuje drukcije
dan 4  pitanje bez tocnog odgovora
dan 5  mala vjezba u obicnom danu (opazanje ili jedan postupak), bez ocjene
dan 6  gdje se pojam sudara s drugim (ren i li, xiao i yi)
dan 7  sto prijevod skriva: Leggeova rijec naspram znaka
```

- Napredak po korisniku, ne po kalendaru (`Practice`): „word 3 of 12", nikad „zaostaješ".
- „Done" zove `Practice.markPracticed` + `advance`. Dvaput u danu broji jedan dan.
- Nakon zadnjeg koraka `finished` i „Begin again" (`Practice.restart`); dani ostaju.

## 5. Ljudi u Analektima

Kartica osobe: Leggeovo ime (kako ga tekst piše), pinyin, jedna rečenica (`MODERN`), poglavlja.
Početni popis za provjeru (alat broji pojavljivanja Leggeova oblika):

| Legge | pinyin |
|---|---|
| Hui | Yan Hui |
| Tsze-lu | Zilu |
| Tsze-kung | Zigong |
| Tsang | Zengzi |
| Yu (the philosopher) | Youzi |
| Tsze-hsia | Zixia |
| Tsze-chang | Zizhang |
| Yen Yu / Zan Yu | Ran You |

Provjeri svaki oblik u tekstu prije nego uđe. Ne nađe se - ne ulazi.

## 6. Testovi
- **`AnalectsDataTest`** (stvarni `analects.json`): 20 knjiga, `N` poglavlja, XX ima 3; nijedan
  redak s CJK znakom; nijedan U+2014; redoslijed knjiga i poglavlja strog.
- **`DailyTest`**: sidra iz §2; isti datum isti tekst; 23:59 i 00:01 u Europe/Zagreb dva poglavlja.
- **`CourseTest`**: 12 cjelina po 7 koraka; `advance` s kraja daje `finished`; svaki pojam ima
  barem dva poglavlja u `terms.json`.
