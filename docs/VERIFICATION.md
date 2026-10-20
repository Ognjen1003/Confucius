# VERIFICATION - Confucius

Stvarni rezultati, **20. 10. 2026.** Emulator: `Medium_Phone_API_36.0` (API 36), Windows, JBR iz Android Studija.

## Build (završni, s produkcijskim AdMob idovima)

```
gradlew.bat clean assembleDebug testDebugUnitTest lintDebug assembleRelease bundleRelease   -> BUILD SUCCESSFUL
gradlew.bat connectedDebugAndroidTest   (testni idovi, privremeni debug resurs)              -> 4/4
```

- **JVM testovi: 35, 0 grešaka** - AnalectsDataTest 7, ArticlesTest 5, CommentaryTest 4, CourseTest 5,
  DailyTest 5, SearchTest 3, TermsAndPeopleTest 6.
- **Instrumentirani: 4, 0 grešaka** (`DeviceTest`): resursi, widget bez unosa, obavijest podsjetnika,
  deep link `arcadia://confucius?chapter=II.4`.
- **Lint: 0 grešaka, 16 upozorenja** (verzije ovisnosti, `UseKtx`, `UseTomlInstead`, `OldTargetApi`,
  `arcadia_widget_description` „unused" jer ga čita biblioteka) - iste vrste kao NewThought.
- `arcadia-core`: `:core:test` i `:android:testDebugUnitTest` zeleni nakon roster reda, `<queries>` reda i
  6 novih Lore poveznica (`PracticeLoreLinksTest` 27 -> 33, novi test za Confucius u oba smjera).
- Tao, Stoic, IChing: `testDebugUnitTest` + `assembleDebug` zeleni s povratnim člancima (Tao i Stoic
  testovi pinaju popis članaka i ažurirani su).
- **AdMob u finalnim APK-ovima** (`aapt2 dump resources`): debug i release oba nose
  `~7803607355`, `/6985349709`, `/7162188530`. Privremeni `zz_admob_test_ids.xml` (debug i release) obrisan.
- U+2014: `ArticlesTest` prolazi kroz sve `res/**/*.json|xml` i `java/**/*.kt` - nijedan.
- Veličine: debug APK 19 MB, release APK 4.5 MB, AAB 9.5 MB.

## Viđeno na emulatoru (debug, testni idovi)

- Splash taman (`values-v31`), ikona: cinober pečat s pet bambusovih letvica; tematska ikona ima monokrom sloj.
- **GDPR forma** (`debug.arcadia.consent.geo EEA`): pojavila se, „Consent" i (u releaseu) „Do not consent" rade.
- **Today 20. 10. 2026. = XII.15** „Learning held by ritual, again" (sidro iz THEORY). TEXT, LEGGE čipovi
  (禮 li · rules of propriety, 學 xue · learning), MODERN komentar i pitanje, test banner na dnu, ispod teksta.
- **Dnevna slika**: share sheet otvoren, opis = datum, referenca, tekst, Legge, Play URL. PNG izvučen iz
  `cache/arcadia_share/confucius-2026-10-20.png` i pogledan: tekst cijel, „Tr. James Legge, The Chinese
  Classics (1893)", „Read more in Confucius" + Play URL.
- **Analects**: Books (20), Search po referenci („iv.15" -> Go to IV.15), po riječi („reciprocity" -> XV.23).
- **Čitač IV.15**: LEGGE ploča kaže samo riječi iz tog poglavlja („the benevolent exercise of them to others",
  „true to the principles of our nature"); čip -> stranica pojma 恕; Back vraća u čitač **na istu poziciju**;
  osoba Tsang · Zengzi -> kartica s 14 poglavlja.
- **Dnevnik**: bilješka iz čitača, uredi (tekst dopunjen), obriši uz potvrdu -> „Nothing written yet".
- **Interstitial**: test interstitial točno na 3. dodir izbornika; Today ga ne dobiva.
- **Practice**: „Word 1 of 12 · step 1 of 7", Next step -> XII.22 (Love people), stranica skoči na vrh.
- **Lore**: 10 članaka, „The Way of two masters" s mostom „Open in Tao" -> Play (Tao nije instaliran);
  Great Learning se čita; 12 riječi (FlowRow) i 17 osoba.
- **Postavke** (zupčanik): Remove ads, GDPR privacy settings, Share app, Rate us, Reading (veličina slova),
  Daily reminder; companion IChing Revealer, Stoic, Tao; More from Arcadia.
- **Podsjetnik**: uključivanje traži `POST_NOTIFICATIONS` tek tada; alarm u `dumpsys alarm`:
  `RTC_WAKEUP 2026-10-21 08:00 window=+1h` (neprecizno, bez exact dozvole). Receiver nije exportan, pa ga
  shell ne može okinuti; obavijest provjerena instrumentiranim testom.
- **Widget** dodan na početni zaslon preko launchera: „ANALECTS · TODAY", „XII.15 · Learning held by
  ritual, …", „Tr. James Legge", prva rečenica, glif 仁, odmah nakon instalacije.
- **Process death**: XI.25 otvoren i pomaknut, `am kill`, ponovni ulaz -> isti čitač, ista pozicija.
- **Povratak s launchera** zadržava mjesto (nakon ispravka; prije je svaki `onNewIntent` vraćao na Today).

## Minificirani release (R8 + shrink, potpisan debug ključem, testni idovi samo za ovaj test)

Pokrenut bez pada; GDPR „Do not consent"; pretraga „reciprocity" -> XV.23 i čitač; Practice; test
interstitial na 3. dodir; **Store** („仁 Confucius", „Remove ads in Confucius", Play billing nije dostupan
na emulatoru -> SETUP_FAILED red); bilješka spremljena i viđena u dnevniku; share sheet s Play URL-om.

## Nađeno i ispravljeno tijekom provjere

1. **ANR na hladnom startu** (5 s bez odgovora na emulatoru): ~500 KB JSON-a parsiralo se na glavnoj niti.
   Sad `ConfuciusData.preload` ide na `Dispatchers.IO` prije prvog ekrana.
2. Back iz pojma vraćao je čitač na vrh: `SaveableStateHolder` po putu stoga.
3. Povratak s launchera resetirao je na Today: resetira samo deep link ili podsjetnik (`EXTRA_TODAY`).
4. Practice nakon „Next step" ostajao je na dnu: skok na vrh.
5. LEGGE ploča nabrajala je sve Leggeove riječi pojma: sad samo one iz poglavlja (`Term.wordsIn` + test).

## Nije provjereno

- Fizički uređaj, stvarno slanje slike u drugi app (share sheet otvoren, PNG pregledan).

## Tablet i Play paket

Pixel_Tablet (2560x1600, landscape): navigation rail, tekst u stupcu do 760 dp; Today, čitač XI.25, Practice i Lore
viđeni i snimljeni bez grešaka rasporeda. Play paket u `store/` (vidi `store/LISTING.md`).
