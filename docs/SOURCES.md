# SOURCES - Confucius

Provjereno **19. 10. 2026.** Vault: `C:\Users\ogmio\Lancelot\Obsidian\Obsidian Vault\sacred-texts\`.

## 1. Glavni izvor
**`Confucianism/CFU/CFU.md`** (444 KB): James Legge, *The Chinese Classics, with a translation,
critical and exegetical notes...*, Analects, Great Learning, Doctrine of the Mean.

| dio filea | redci (približno) | uporaba |
|---|---|---|
| Legge s kineskim tekstom, `BOOK I.`-`BOOK XX.`, `CHAPTER`/`CHAP.` | 25-4208 | **izvor teksta** |
| „CONFUCIAN ANALECTS ... translated by James Legge [1893]", samo engleski | 4209-6897 | **provjera teksta** (alat uspoređuje riječi po poglavlju i ispisuje razlike) |
| THE GREAT LEARNING | 6898-7191 | sporedni čitač u Loreu |
| THE DOCTRINE OF THE MEAN | 7192-kraj | sporedni čitač u Loreu |

**Godinu i izdanje** upiši s naslovnice prvog dijela; drugi dio nosi „[1893]". Legge (1815-1897):
javno dobro svugdje.

## 2. Susjedno, ne u v1
| djelo | zašto ne |
|---|---|
| `Confucianism/Mencius Index` (Legge) | drugi app ili kasnija verzija; ne miješati |
| `Confucianism/The Ethics of Confucius Index` (Dawson 1915.) | sekundarna knjiga, tematski složena; ne izvor teksta |
| `SBE 3`, *Li Ki*, *Book of Odes* | drugi klasici |

## 3. Čišćenje (`tools/extract.py`)
- Preskače retke s CJK znakovima i `【…】` oznakama.
- Poglavlje od oznake `CHAPTER I.`/`CHAP. N.` do iduće; knjiga od `BOOK N.`.
- Prijelomi redaka u razmak; `--` i `,-` u ` - `; ništa drugo.
- Usklađuje broj poglavlja s drugim dijelom i s kineskim oznakama; svako odstupanje u
  `tools/extract_report.md`. **Ne razriješi - stani i javi.**
- Zapisuje SHA-256 ulaza u `analects.json`. Snapshot ulaza u `tools/source/`.
