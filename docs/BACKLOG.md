# BACKLOG - Confucius

Faze redom; faza je gotova kad su `assembleDebug` + `testDebugUnitTest` + `lintDebug` zeleni.
Zajednički dio: `GP/divination/CLAUDE.md` (Standard nove aplikacije). **Stanje 20. 10. 2026.: F0-F6 gotovo**, osim odluka vlasnika niže.

## F0 - temelj ✅
- F0.1 Gradle iz NewThoughta/Male; produkcijski AdMob idovi aktivni, testni zakomentirani; `keep.xml`.
- F0.2 Roster `仁`, `storeAvailable = false, promoteBeforeRelease = true`; `<queries>`; core testovi zeleni.
- F0.3 Application, consent, backup pravila, Postavke iz zupčanika.

## F1 - tekst ✅
`tools/extract.py`: 499 poglavlja, XX ima 3; 10 ispravaka s dokazom; `AnalectsDataTest`.

## F2 - `core/` i Today ✅
`Daily`, `Analects`, `Search`, Today, čitač, `DailyTest`. Dnevna slika viđena (PNG izvučen iz cachea).

## F3 - pojmovi, osobe, tečaj ✅
`terms.json` (ručno potvrđeno, `tools/term_decisions.json`), `people.json` (17), `course.json` (84 koraka).

## F4 - komentar ✅
`tools/commentary/book01..20.json`: naslov, komentar i pitanje za **svih 499** poglavlja, 21 vježba.

## F5 - Journal, Lore, widget, podsjetnik, poveznice ✅
10 članaka, Great Learning i Mean, poveznice u oba smjera (članci dodani u Tao, Stoic, IChing),
widget, podsjetnik.

## F6 - provjera i store
- ✅ `docs/VERIFICATION.md`, emulator debug i minificirani release.
- ✅ Play paket u `store/`: ikona 512, feature 1024x500, 6 phone + 4 tablet, `LISTING.md` (izmjereno `count.py`), ZIP.
- ✅ Tracker u `APP_IDEJE.md` (upisano 20. 10. 2026.).

## Odluke koje čekaju vlasnika
1. Play naziv (`Confucius: The Analects` 23 znaka ili `Analects: Daily Confucius` 25).
2. Mencius kao kasnija verzija ili zaseban app.
3. Nove verzije Taoa, Stoica i IChinga (povratni članci i poveznica) i appova na rosteru: build i objava su vlasnikovi.

## Ne u ovoj verziji
Kineski izvornik (nepotpun u vaultu: 372/499), Mencius, audio, mreža, niz i značke.

## Kasnije, ako vlasnik želi
- Pozicija pomaka se pamti po putu stoga; stare putanje se ne čiste kad se promijeni tab (malo memorije).
- „A Little while ago" (XII.22) i slični tragovi skena ostaju kako ih Legge/sken pišu; nisu greške smisla.
