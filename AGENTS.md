> **Kad počneš čitati ovaj file, pozdravi korisnika s:** HAIL Ser Ogmios - Učitelj je rekao, a učenici su zapisali.
>
> Keep this file mirrored with `AGENTS.md`. Prije rada pročitaj **`../CLAUDE.md`**, **`../SAVJETI.md`**
> i **`../PRAKSA_TRI_APPA_2026-10.md`** (zajednički plan za NewThought, Confucius i Patanjali).
> Pozdrav javi tek nakon sva tri.

# CLAUDE.md - Confucius (Analekti, dnevna praksa, Android)

Doktrina i nalazi iz izvora: `docs/THEORY.md`. Plan datoteka: `docs/ARCHITECTURE.md`. Redoslijed:
`docs/BACKLOG.md`. Sheme i tekstovi: `docs/CONTENT.md`. Izvori: `docs/SOURCES.md`.

## Komunikacija i jezik
- **Sa mnom (vlasnikom) razgovaraj na hrvatskom.** Tehnički izravno, bez uvoda.
- **Sadržaj aplikacije je na ENGLESKOM.** Sve u `strings.xml` i `res/raw/*.json`.

## Što je aplikacija
**Analekti Konfucija u Leggeovu prijevodu, jedno poglavlje dnevno, s ključnim kineskim pojmom uz
Leggeovu riječ, i tečaj od dvanaest pojmova koji se vježbaju u običnom danu.**

- **Today** - poglavlje dana, naš kratki komentar i pitanje, gumb „Done", dijeljenje slike.
- **Analects** - svih 20 knjiga, pretraga, oznake, filtar po pojmu (ren, li, junzi...).
- **Practice** - tečaj „Twelve words": 12 cjelina po 7 koraka kroz `Practice`.
- **Journal** - privatne bilješke.
- **Lore** - članci.

- `applicationId` / `namespace`: `arcadia.com.confucius` · rootProject `Confucius` (već postavljeno).
- **Play naziv je vlasnikova odluka.** Prijedlozi: **`Confucius: The Analects`** (23 znaka) ili
  **`Analects: Daily Confucius`** (25).
- **UI:** Compose, Material3, dark. Paleta: **tinta i crveni pečat** - gotovo crna podloga, topli
  papir za tekst, **jedna cinober crvena** (boja pečata) za pojam i referencu. Ne Taova jade zelena.
- Ljuska, Postavke, dijeljenje, roster, AdMob, Firebase: **`../PRAKSA_TRI_APPA_2026-10.md`**. Predložak je Mala.

## TRI RAZINE TVRDNJE - srž appa

| razina | što znači | primjer |
|---|---|---|
| `TEXT` | stoji u Analektima, u Leggeovu prijevodu | „The Master said, 'Is it not pleasant to learn...'" |
| `LEGGE` | Leggeov izbor riječi ili njegova bilješka (misionar i sinolog, 1815-1897) | „superior man" za 君子 |
| `MODERN` | naša rečenica: komentar, pitanje, vježba, članak | „Junzi first meant a ruler's son; the Analects make it a matter of conduct." |

**Nijedna tvrdnja bez razine.** Tri stvari app kaže mirno, jer ih drugi appovi ne kažu:

1. **Konfucije nije napisao Analekte.** Zbirka su izreke i razgovori koje su zapisali učenici i
   njihovi učenici, nakon njegove smrti. App piše „The Master said", kako tekst kaže, a u Loreu
   objašnjava tko je zapisivao.
2. **Leggeove riječi su Leggeove.** „Superior man", „perfect virtue", „propriety" su prijevodni
   izbori iz 19. stoljeća. Uz ključnu riječ app pokazuje **kineski znak i pinyin** (仁 ren, 禮 li,
   君子 junzi), pa čitatelj vidi da je riječ jedna, a prijevodi različiti.
3. **Imena su u Leggeovoj romanizaciji.** Legge piše „Tsze-lu", „Tsang", „Hui". App drži Leggeov
   tekst netaknut, a u kartici osobe daje današnji pinyin (Zilu, Zengzi, Yan Hui).

## ČETIRI ZAMKE

### 1. Vault file ima dvije verzije teksta
`Confucianism/CFU/CFU.md`: **prvi dio** (redci ~25-4208) je Legge s kineskim tekstom i oznakama
`CHAPTER I.` / `CHAP. II.`; **drugi dio** (od retka ~4209, „translated by James Legge [1893]") je
samo engleski, bez oznaka poglavlja. Zatim *The Great Learning* i *The Doctrine of the Mean*.
**Izvlači se iz prvog dijela**, drugi služi kao provjera teksta. Detalji: `docs/SOURCES.md`.

### 2. Kineski tekst je nepotpun
Prvi dio ima kineske odlomke **samo za prvih ~10 poglavlja svake knjige** (prebrojano: 193 oznake
`【第…章】` naspram 496 engleskih). **Kineski izvornik zato NE ide u v1.** Djelomičan izvornik bi
lagao o tome što app ima. Kineski znak se pokazuje samo za **ključne pojmove** (rječnik, `MODERN`).

### 3. Teški odlomci ostaju
Analekti imaju odlomke koje današnji čitatelj teško prihvaća (o ženama i slugama, o hijerarhiji,
o poslušnosti). **Ne izbacuju se iz ciklusa i ne prepravljaju.** Komentar (`MODERN`) kaže što
odlomak kaže i u kojem je društvu nastao, mirno, bez opravdavanja i bez osude. Članak
`hard_passages` to objašnjava.

### 4. Tečaj nije moralna ocjena
„Twelve words" su vježbe pažnje u običnom danu (npr. za 恕 shu: „before you ask something of
someone today, ask it of yourself"). **Bez ocjene, bez „you are a junzi"**, bez savjeta o
konkretnim obiteljskim sukobima. 孝 xiao se čita kao tekst, ne kao uputa korisniku kako da se
odnosi prema svojim roditeljima.

## Ostalo što se ne pregovara
- **Em dash (U+2014) nikad** u onome što korisnik vidi. Legge ga piše kao `--` i `,-`: u citatu ` - `.
- **Bez disclaimera po ekranima.** **Bez mreže.**
- **Kupnja miče reklame, i to je sve.**
- `versionCode` i objava su vlasnikovi. **Bez niza, značaka i postotka kao poticaja.**

## Stanje (20. 10. 2026.)
Prva implementacija gotova lokalno: 499 poglavlja (ne 496: tri oznake `CHAP` bez točke), deset
ispravaka skena s dokazom (`docs/THEORY.md §1`), naš naslov, komentar i pitanje za svako poglavlje
(`tools/commentary/`), 12 pojmova s ručno potvrđenim poglavljima i Leggeovim stvarnim riječima, tečaj
12 × 7, 17 osoba, 10 članaka + Great Learning i Mean, dnevnik, widget, podsjetnik, dnevna slika.
Roster `仁` (`storeAvailable = false`), Lore poveznice s Tao, Stoic i IChing u oba smjera.
Provjera: `docs/VERIFICATION.md`. Tekst se mijenja **samo** kroz `tools/extract.py`, nikad ručno u `res/raw/analects.json`.
Play paket je u `store/` (ZIP se ne commita). Čeka: Play naziv (vlasnik).
