# Confucius - Google Play listing

Written to Play's field limits, with the limit noted on each one. Copy straight into Play Console.
Counts are measured by `store/count.py`, not estimated. **The Play name is the owner's decision.**

## App name - 30 characters max

```
Confucius: The Analects
```
23 characters. Brand first, the searched word second (SAVJETI §7).

**Alternative:** `Analects: Daily Confucius` (25 characters).

## Short description - 80 characters max

```
The Analects of Confucius, one chapter a day, in James Legge's translation.
```
75 characters.

**Alternatives:**
- `All 499 chapters of the Analects, twelve key words, and a private journal.` (74)

## Full description - 4000 characters max

Character count: **2392**.

```
Confucius brings you the Analects one chapter a day: every one of its 499 chapters in James Legge's classic translation, with a short comment and a question beside each.

The Analects are the sayings of Confucius as his disciples remembered them, written down after his death. The app says so, and writes what the text writes: "The Master said". Legge's words are kept exactly as he printed them; our comments, titles and questions are always marked as ours.

TODAY
• The chapter of the day, in order through all twenty books.
• A short comment on what the chapter says, and one question to take into the day.
• The Chinese key word beside Legge's English, so you can see when one word becomes many.
• Share the day's chapter as an image, with its reference and translator.

THE ANALECTS
• All 499 chapters, book by book.
• Search by word or go straight to a reference like II.4.
• Filter by one of the twelve key words. Bookmark any chapter.

TWELVE WORDS
A course of twelve units, seven steps each: ren, li, yi, zhi, xin, xiao, junzi, xue, shu, zhong, de and zhengming. Each word is shown with its character, its pinyin and the English words Legge actually used for it, then read in two chapters, turned into a question, a small exercise for an ordinary day, and a look at what the translation hides. Nothing is graded. The app counts the days you practised as a total that never falls. No streaks, no badges.

PEOPLE
Seventeen people of the Analects, from Yan Hui and Zilu to Zigong and Zengzi, with Legge's spelling beside today's pinyin and every chapter in which they appear.

LORE
Ten articles: who wrote the Analects, one word and its many translations, James Legge, names in Legge, why the hard passages stay in, the Way of Confucius and Laozi, three questions a day, Confucius and the Book of Changes, the Four Books, and more. The Great Learning and the Doctrine of the Mean are included to read in full.

JOURNAL
Private notes, linked to the chapter you were reading. They stay on this device: never shared, never backed up, never counted.

The text is Legge's 1893 revised translation, in the public domain. It was checked word for word against a second copy of the same translation, and every correction is listed in the app's source notes. Everything works offline, with no account.

Free to install, with ads. A single one-time purchase removes them; nothing else is for sale.
```

## Graphics

| Asset | File | Size | Play requirement |
|---|---|---|---|
| App icon | `icon-512.png` | 512 × 512 RGBA | 512 × 512, 32-bit PNG |
| Feature graphic | `feature-graphic-1024x500.png` | 1024 × 500 RGB | 1024 × 500, no transparency |
| Phone screenshots | `phone/p1…p6` | 1080 × 1920 | 2-8, 9:16, 320-3840 px |
| Tablet screenshots | `tablet/t1…t4` | 2560 × 1600 | ≤ 2:1, 320-3840 px |

The icon is the app's own adaptive launcher icon (a vermilion seal over five bamboo slips), rendered
by Android at 512 and cropped to the visible 72 of 108 dp; it is not a separate drawing. The feature
graphic composites that file (`store/regenerate.sh`; no starfield and no ring motif on purpose: this is
a book, not a sky app). Screenshots were taken from a debug build with ads removed through the premium
prefs (`debug.arcadia.premium.keep 1`).

## Screenshot captions

Phone, in upload order:
1. `p1_today` - The chapter of the day, with Legge's text and the key words in it.
2. `p2_reader` - Legge's English beside the Chinese word it translates.
3. `p3_term` - One of twelve words: character, pinyin and Legge's renderings.
4. `p4_practice` - Twelve words, seven steps each.
5. `p5_search` - Every chapter with 仁 ren: 56 of them.
6. `p6_lore` - Who wrote the Analects, and other articles.

Tablet: `t1_today` (XII.15), `t2_reader` (XI.25, Bathing in the Yi), `t3_practice`, `t4_lore`.

## Store settings checklist

- [ ] Contains ads declared
- [ ] In-app purchase `remove_ads` (managed, non-consumable)
- [ ] Price: Free (Free → Paid is irreversible)
- [ ] Data safety: Firebase Analytics and AdMob collected; no account, no sign-in; notes stay on the device
- [ ] Content rating (IARC)
- [ ] Target audience: not for children
- [ ] Privacy policy URL
- [x] AdMob UMP / GDPR consent flow (`ConsentManager.gather`, seen on the emulator)
- [x] `app/google-services.json`: real Firebase project (`androiddivinaton`), client `arcadia.com.confucius`
- [x] Production AdMob ids active in `strings.xml`, verified in the final debug and release APK
- [x] No paywall: everything is free, `remove_ads` only removes ads
