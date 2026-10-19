# BACKLOG - Confucius

Faze redom; faza je gotova kad su `assembleDebug` + `testDebugUnitTest` + `lintDebug` zeleni.
Zajednički dio: `../../PRAKSA_TRI_APPA_2026-10.md`.

## F0 - temelj
- **F0.1** Gradle iz Male (PRAKSA §3). **AdMob idovi iz komentara u `strings.xml` postaju aktivni
  stringovi** (PRAKSA §2), testni zakomentirani; `keep.xml` s tri AdMob stringa.
- **F0.2** Roster: `ArcadiaApp("confucius", "Confucius", "The Analects, one chapter a day", "Every chapter of the Analects in James Legge's translation, twelve key words to practise, and a private journal.", "arcadia.com.confucius", "仁", storeAvailable = false, promoteBeforeRelease = true)`;
  `<queries>` red; `ArcadiaAppsTest` i `SuiteQueriesTest` zeleni. Ako 仁 nije jedinstven, **孔** i javi.
- **F0.3** Application, consent, backup pravila, Postavke iz zupčanika, prazni JSON-ovi.
- Gotovo kad: app se pokreće (testni idovi), banner, Postavke, `assembleRelease` prolazi.

## F1 - tekst (lokalno, vault)
`tools/extract.py` po `SOURCES.md §3`. Sidra u `THEORY.md §1` popuniti stvarnim brojem.
`AnalectsDataTest`. **Book XX mora imati 3 poglavlja.**

## F2 - `core/` i Today
`Daily`, `Analects`, `Search`, Today ekran, čitač, `DailyTest`. Dnevna slika (PRAKSA §4) radi i
viđena je u drugom appu.

## F3 - pojmovi, osobe, tečaj
`terms.json` (ručno potvrđeno), `people.json`, `course.json`, Practice ekran, `CourseTest`.

## F4 - komentar
`commentary.json` za **sva** poglavlja, originalno po poglavlju. Ovo je najveći posao; radi knjigu
po knjigu i nakon svake pokreni `extract.py`.

## F5 - Journal, Lore, widget, podsjetnik, poveznice
Članci iz `CONTENT.md §4`, Great Learning i Mean u Loreu, PracticeLoreLinks u oba smjera (dodaj
članke i u Tao, Stoic, IChing: to su zasebni repoi), widget, podsjetnik.

## F6 - provjera i store
PRAKSA §7. `docs/VERIFICATION.md`. `/play-store-assets`. Tracker u `APP_IDEJE.md`.

## Odluke koje čekaju vlasnika
1. Play naziv.
2. Mencius kao kasnija verzija ili zaseban app.

## Ne u ovoj verziji
Kineski izvornik (nepotpun u vaultu), Mencius, audio, mreža, niz i značke.
