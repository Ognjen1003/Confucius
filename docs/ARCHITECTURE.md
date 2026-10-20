# ARCHITECTURE - Confucius

Zajednička ljuska (Postavke, dijeljenje, reklame, podsjetnik, widget, backup):
`../../PRAKSA_TRI_APPA_2026-10.md §3-4`. Predložak: NewThought i Mala. Stanje: **20. 10. 2026.**

```
app/src/main/java/arcadia/com/confucius/
  ConfuciusApplication.kt    Premium, Ad, Analytics, Billing, Practice "confucius_practice",
                             ReadingHistory "confucius_journal", Prefs, kanal i podsjetnik
  MainActivity.kt            ConsentManager.gather; NavigationSuiteScaffold, pet destinacija;
                             stog ekrana "kind:id" (Back vraća tamo odakle je otvoreno);
                             SaveableStateHolder po putu stoga (pomak čitača preživi Back i process death);
                             podaci se parsiraju izvan glavne niti prije prvog ekrana (ANR na hladnom startu);
                             arcadia://confucius?chapter=II.4

  core/                      CIST KOTLIN, bez Android importa, JVM testovi
    Content.kt               Level (TEXT/LEGGE/MODERN), Analects/Book/Chapter, Comment/Commentary,
                             Term (+ wordsIn), Person, Article, MinorText; parseri iz String
    Daily.kt                 lokalni datum -> poglavlje (THEORY §2)
    Course.kt                StepKind, Course, PracticeCourse "twelve_words"
    Search.kt                referenca ("ii.4", "2.4"), tekst, pojam, osoba; redoslijed knjige

  platform/
    ConfuciusData.kt         čita res/raw jednom; preload() izvan glavne niti
    Journal.kt               ReadingHistory "journal", update/delete; payload = referenca poglavlja
    Prefs.kt                 veličina slova (backup smije), oznake (bez backupa)
    Reminders.kt             scheduleInexact, re-arm (boot, zamjena paketa, vrijeme, zona),
                             preskače vježbani dan; show() odvojen za test

  widget/ConfuciusWidget.kt  ArcadiaWidgetProvider: "XII.15 · naslov", Legge, prva rečenica, glif 仁

  ui/
    Common.kt                Page, Header (PracticeHeaderActions), Panel, LevelLabel, Body, ChapterText,
                             TermChip/TermRow (riječ iz tog poglavlja), CommentPanel, NoteDialog, RowCard
    ReadingScreens.kt        Today, čitač poglavlja, Analects (knjige / pretraga + filtar pojma / oznake),
                             knjiga, pojam, osoba; ChapterShare (DailyReadingShareButton)
    PracticeScreens.kt       tečaj, dnevnik, Lore, članak (PracticeLoreBridge), Great Learning i Mean,
                             ReadingSettings (veličina slova, podsjetnik)
    theme/Theme.kt           tinta i crveni pečat; svi surfaceContainer* postavljeni

  res/raw/  analects.json  commentary.json  terms.json  people.json  course.json  articles.json  texts_minor.json
  res/raw/keep.xml           tri AdMob stringa + svi raw fileovi
  res/drawable*/             ic_confucius (adaptive + monochrome), ic_notification
tools/
  extract.py                 vault/snapshot -> analects.json, commentary.json, texts_minor.json; izvještaj
  extract_report.md          krivi brojevi, ispravci, usporedba s drugim dijelom, oznake pojmova
  source/CFU.md              snapshot ulaza
  commentary/book01..20.json naši naslovi, komentari, pitanja, vježbe (MODERN), ulaz za extract.py
  term_candidates.json       kandidati po Leggeovoj riječi (+ kineski gdje postoji)
  term_decisions.json        ručno potvrđena poglavlja po pojmu
```

## Pet destinacija

| destinacija | glif | što radi |
|---|---|---|
| Today | 日 | datum, „Analects XII.15" + naš naslov, TEXT, LEGGE čipovi pojmova, MODERN komentar i pitanje, Share daily image, Open in the book, Write a note, Done, Days practised, banner |
| Analects | ☰ | Books / Search (tekst ili referenca, filtar po pojmu) / Bookmarks |
| Practice | 仁 | „Twelve words", „Word 1 of 12 · step 1 of 7", popis riječi |
| Journal | ✎ | bilješke, uredi, „Read IV.15", obriši uz potvrdu |
| Lore | ▤ | 10 članaka, Great Learning i Doctrine of the Mean, 12 riječi, 17 osoba, banner |

Postavke: zupčanik u zaglavlju svake stranice → `PracticeSettingsPage("Confucius", companionPackages =
tao, stoic, iching)`: Remove ads, GDPR (kad UMP traži), Share app, Rate us, Reading, Daily reminder.

Interstitial: zajednička kadenca, svaki treći dodir izbornika; Today drži dospjelu reklamu
(`tick(mayShow = index != 0)`). Banner samo na dnu Today i Lore, nikad preko teksta.

## Dijeljenje
- Dnevna slika i „Share this passage" u čitaču: `DailyReadingShareButton("Confucius", datum,
  "Analects XII.15", Leggeov tekst, "Tr. James Legge, The Chinese Classics (1893)")`.
- Na slici samo izvorni tekst; komentar i bilješke nikad. Play URL na slici i u opisu (biblioteka).

## Poveznice (PracticeLoreLinks, oba smjera)
| iz članka (Confucius) | u app | povratni članak |
|---|---|---|
| `the_way_of_two_masters` | Tao | Tao `two_masters` |
| `examining_oneself` | Stoic | Stoic `three_questions` |
| `the_attributed_wings` | IChing Revealer | IChing `11` („Confucius and the Ten Wings") |
