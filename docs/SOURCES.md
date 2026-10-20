# SOURCES - Confucius

Provjereno **19. 10. 2026.**, tekst izvučen i zaključan **20. 10. 2026.**
Vault: `C:\Users\ogmio\Lancelot\Obsidian\Obsidian Vault\sacred-texts\`.

## 1. Glavni izvor
**`Confucianism/CFU/CFU.md`** (444 KB, SHA-256 `d04250a7…c99bbe`): James Legge, *The Chinese Classics,
with a translation, critical and exegetical notes...*, Analects, Great Learning, Doctrine of the Mean.
Snapshot: `tools/source/CFU.md`.

| dio filea | redci | uporaba |
|---|---|---|
| Legge s kineskim tekstom, `BOOK I.`-`BOOK XX.`, `CHAPTER`/`CHAP.`/`CHAP` | 25-4208 | **izvor teksta** (499 poglavlja) |
| „CONFUCIAN ANALECTS ... translated by James Legge [1893]", samo engleski | 4209-6897 | **provjera teksta** riječ po riječ, po knjigama |
| THE GREAT LEARNING | 6898-7191 | čitač u Loreu (`texts_minor.json`) |
| THE DOCTRINE OF THE MEAN | 7192-kraj | čitač u Loreu |

**Izdanje:** naslovnica prvog dijela nema godinu; drugi, treći i četvrti dio nose „[1893]", što je
Leggeovo revidirano izdanje (Oxford, Clarendon Press). App piše „Tr. James Legge, The Chinese Classics
(1893)". Legge (1815-1897): javno dobro svugdje.

## 2. Susjedno, ne u v1
| djelo | zašto ne |
|---|---|
| `Confucianism/Mencius Index` (Legge) | drugi app ili kasnija verzija; ne miješati |
| `Confucianism/The Ethics of Confucius Index` (Dawson 1915.) | sekundarna knjiga; ne izvor teksta |
| `SBE 3`, *Li Ki*, *Book of Odes* | drugi klasici |

## 3. Čišćenje (`tools/extract.py`)
`python -I tools/extract.py [--refresh]` (s `--refresh` prvo kopira snapshot iz vaulta).
- Preskače retke s CJK znakovima i `【…】` oznakama (kineski se skuplja interno, samo za oznake pojmova).
- Poglavlje od `CHAPTER I.` / `CHAP. N.` / `CHAP N.` do iduće oznake; knjiga od `BOOK N.`. Brojanje po
  položaju; krivo otisnuti brojevi se javljaju.
- Prijelomi redaka u razmak; riječ prelomljena crticom na kraju retka spaja se bez razmaka
  („music-" + „master"); `--` i `,-` u ` - `.
- Ispravci samo izričitom listom (`CORRECTIONS`, `SPLITS`), svaki s dokazom: `docs/THEORY.md §1`.
- Usporedba s drugim dijelom i SHA-256 ulaza u `tools/extract_report.md`; SHA i u `analects.json`.
- Spaja naš komentar iz `tools/commentary/bookNN.json` u `res/raw/commentary.json` i naslove u
  `analects.json`; zaustavlja se ako nedostaje polje ili poglavlje ne postoji.
- Oznake pojmova: kandidati u `tools/term_candidates.json`, odluke u `tools/term_decisions.json`,
  konačno `res/raw/terms.json`. Osobe: `res/raw/people.json`, alat provjerava da ime stoji u poglavlju.
- Staje ako bilo koji izlaz sadrži U+2014.

## 4. Što je naše (MODERN)
Svi naslovi poglavlja, komentari, pitanja i vježbe (`tools/commentary/`), opisi pojmova i osoba, tečaj,
članci. Pisani su originalno za ovaj app, poglavlje po poglavlje. Činjenice u člancima (Legge, Wang Tao,
Zhu Xi, ispiti 1313-1905, Sima Qian o Deset krila, Seneka *De Ira* III.36) su opće poznate i iznesene
bez citata tuđih rečenica.
