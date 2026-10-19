# ARCHITECTURE - Confucius

Zajednička ljuska (Postavke, dijeljenje, reklame, podsjetnik, widget, backup):
`../../PRAKSA_TRI_APPA_2026-10.md §3-4`. Predložak datoteka: `../../Mala/`.

```
app/src/main/java/arcadia/com/confucius/
  ConfuciusApplication.kt    init iz PRAKSA §3 (Practice "confucius_practice", ReadingHistory "confucius_journal")
  MainActivity.kt            ConsentManager.gather; NavigationSuiteScaffold, pet destinacija

  core/                      CIST KOTLIN, JVM testovi
    Level.kt                 TEXT / LEGGE / MODERN
    Analects.kt              knjige, poglavlja, parsira analects.json iz String
    Daily.kt                 lokalni datum -> poglavlje (THEORY §2)
    Terms.kt                 12 pojmova, poglavlja po pojmu
    People.kt                kartice osoba (Legge oblik + pinyin)
    Course.kt                gradi PracticeCourse "twelve_words"
    Search.kt                tekst, pojam, osoba; bez rangiranja
    Articles.kt

  platform/
    ConfuciusData.kt         cita res/raw jednom
    Journal.kt               ReadingHistory "journal", update/delete; tekst nikad u analytics
    Prefs.kt                 velicina slova, podsjetnik
    Reminder.kt              scheduleInexact

  widget/ConfuciusWidget.kt  ArcadiaWidgetProvider: poglavlje dana

  ui/
    today/                   poglavlje dana, komentar, pitanje, Done, DailyReadingShareButton
    analects/                knjige -> poglavlja, pretraga, filtar po pojmu, oznake
    read/                    citac poglavlja: TEXT, LEGGE napomena o rijeci, MODERN komentar, Share this passage
    practice/                tecaj "Twelve words"
    journal/                 biljeske
    lore/                    clanci + Great Learning i Doctrine of the Mean kao citac
    settings/                PracticeSettingsPage
    common/                  LevelChip, TermChip (znak + pinyin + Leggeova rijec), PassageRef
    theme/                   tinta i crveni pecat

  res/raw/  analects.json  terms.json  people.json  course.json  commentary.json  articles.json  texts_minor.json
  res/raw/keep.xml           svi raw fileovi + tri AdMob stringa
tools/
  extract.py                 vault -> analects.json, texts_minor.json; sidra; SHA-256 ulaza
  source/                    snapshot CFU.md (kao Tao)
  commentary.json            nasi komentari/pitanja po poglavlju (MODERN), ulaz za extract.py
```

## Pet destinacija

| destinacija | glif | što radi |
|---|---|---|
| Today | ◷ | „Analects II.4", tekst, `TermChip` za pojmove u njemu, komentar i pitanje (`MODERN`), Done, Share |
| Analects | ☰ | 20 knjiga, pretraga, filtar po pojmu, oznake |
| Practice | 仁 | „Twelve words", „word 3 of 12, day 5" |
| Journal | ✎ | bilješke; nova bilješka može nositi referencu poglavlja |
| Lore | ▤ | članci, kartice osoba, Great Learning i Doctrine of the Mean |

## Dijeljenje
- Dnevna slika: `DailyReadingShareButton(appName = "Confucius", date, title = "Analects II.4",
  text = <TEXT>, credit = "Tr. James Legge, The Chinese Classics")`. Godinu izdanja upiši s naslovnice filea.
- „Share this passage" u čitaču, isti renderer, boje teme.
- **Komentar ne ide na sliku**, samo tekst Analekata. Bilješke nikad.

## Poveznice (PracticeLoreLinks, oba smjera)
| iz članka (Confucius) | u app | zašto |
|---|---|---|
| `the_way_of_two_masters` | Tao | dva klasična kineska odgovora na „put" |
| `examining_oneself` (I.4, „I daily examine myself") | Stoic | dnevno ispitivanje savjesti |
| `the_attributed_wings` | IChing Revealer | predaja koja Konfuciju pripisuje Deset krila |
| odgovarajući članci u Tao, Stoic, IChing | Confucius | nazad |

`PracticeSettingsPage(companionPackages = setOf("arcadia.com.tao", "arcadia.com.stoic", "arcadia.com.iching"))`.
