package arcadia.com.confucius.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arcadia.com.confucius.core.*
import arcadia.com.confucius.platform.ConfuciusData
import arcadia.com.confucius.platform.Prefs
import arcadia.com.confucius.ui.theme.*
import com.arcadia.core.ArcadiaBannerAd
import com.arcadia.core.DailyReadingShareButton
import com.arcadia.core.Practice
import com.arcadia.core.WidgetSchedule
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/** The image carries Legge's words, the reference, the translator, the date and the app: never our comment, never a note. */
@Composable
fun ChapterShare(c: Chapter, date: LocalDate, label: String) {
    DailyReadingShareButton("Confucius", date.toString(), c.citation, c.text,
        "Tr. James Legge, The Chinese Classics (1893)", label)
}

@Composable
fun TodayPage(date: LocalDate, onChapter: (String) -> Unit, onTerm: (String) -> Unit, onNote: (Chapter) -> Unit) {
    val context = LocalContext.current
    val analects = ConfuciusData.analects(context)
    val c = Daily.chapter(date, analects)
    val state = Practice.state(ConfuciusData.PRACTICE_ID)
    val done = state.lastPracticedEpochDay == date.toEpochDay()
    val terms = ConfuciusData.terms(context).filter { it.id in c.terms }
    Page {
        item { Header("The chapter of the day", "Confucius · today") }
        item { Text(date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.ENGLISH)), color = Muted) }
        item {
            Column {
                Text(c.citation, color = Seal, style = MaterialTheme.typography.titleMedium)
                Text(c.title, style = MaterialTheme.typography.headlineMedium)
            }
        }
        item { ChapterText(c, analects.credit) }
        if (terms.isNotEmpty()) item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                LevelLabel(Level.LEGGE, "The word in this chapter")
                TermRow(terms, c, onTerm)
            }
        }
        item { CommentPanel(c, ConfuciusData.commentary(context)[c.ref]) }
        item { ChapterShare(c, date, "Share daily image") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { onChapter(c.ref) }, modifier = Modifier.weight(1f)) { Text("Open in the book") }
                OutlinedButton(onClick = { onNote(c) }, modifier = Modifier.weight(1f)) { Text("Write a note") }
            }
        }
        item {
            Button(
                onClick = { Practice.markPracticed(ConfuciusData.PRACTICE_ID); WidgetSchedule.refreshAll(context) },
                enabled = !done, modifier = Modifier.fillMaxWidth(),
            ) { Text(if (done) "Done for today" else "Done") }
        }
        item { Text("Days practised: ${state.daysPracticed}", color = Muted) }
        item { ArcadiaBannerAd() }
    }
}

/** The reader: one chapter, its words, the people in it, our comment, and the way to the next. */
@Composable
fun ChapterPage(
    ref: String, onBack: () -> Unit, onChapter: (String) -> Unit, onTerm: (String) -> Unit,
    onPerson: (String) -> Unit, onNote: (Chapter) -> Unit,
) {
    val context = LocalContext.current
    val analects = ConfuciusData.analects(context)
    val c = analects.byRef(ref) ?: return
    val terms = ConfuciusData.terms(context).filter { it.id in c.terms }
    val people = ConfuciusData.people(context).filter { it.id in c.people }
    Page {
        item { Header(c.title, c.citation, onBack) }
        item { ChapterText(c, analects.credit) }
        if (terms.isNotEmpty()) item {
            Panel {
                LevelLabel(Level.LEGGE, "Legge's word, and the Chinese")
                terms.forEach { t ->
                    Text("${t.character} ${t.pinyin}: here Legge writes ${t.wordsIn(c).joinToString(", ") { "\"$it\"" }}.",
                        style = MaterialTheme.typography.bodyMedium)
                }
                TermRow(terms, c, onTerm)
                Text("Character and pinyin are ours, from a dictionary.", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        if (people.isNotEmpty()) item {
            Panel {
                LevelLabel(Level.MODERN, "Who is speaking")
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    people.forEach { p -> AssistChip(onClick = { onPerson(p.id) }, label = { Text("${p.legge} · ${p.pinyin}") }) }
                }
            }
        }
        item { CommentPanel(c, ConfuciusData.commentary(context)[c.ref]) }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { Prefs.toggle(c.ref) }, modifier = Modifier.weight(1f)) {
                    Text(if (Prefs.marked(c.ref)) "Remove bookmark" else "Bookmark")
                }
                OutlinedButton(onClick = { onNote(c) }, modifier = Modifier.weight(1f)) { Text("Write a note") }
            }
        }
        item { ChapterShare(c, ConfuciusData.today(), "Share this passage") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val prev = analects.previous(c.ref)
                val next = analects.next(c.ref)
                OutlinedButton(onClick = { prev?.let { onChapter(it.ref) } }, enabled = prev != null, modifier = Modifier.weight(1f)) {
                    Text(prev?.let { "← ${it.ref}" } ?: "Beginning")
                }
                OutlinedButton(onClick = { next?.let { onChapter(it.ref) } }, enabled = next != null, modifier = Modifier.weight(1f)) {
                    Text(next?.let { "${it.ref} →" } ?: "The end")
                }
            }
        }
    }
}

@Composable
fun AnalectsPage(onBook: (Int) -> Unit, onChapter: (String) -> Unit) {
    val context = LocalContext.current
    val analects = ConfuciusData.analects(context)
    val comments = ConfuciusData.commentary(context)
    val terms = ConfuciusData.terms(context)
    var mode by rememberSaveable { mutableIntStateOf(0) }
    var query by rememberSaveable { mutableStateOf("") }
    var term by rememberSaveable { mutableStateOf<String?>(null) }
    Page {
        item { Header("Twenty books", "The Analects") }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Books", "Search", "Bookmarks").forEachIndexed { i, label ->
                    FilterChip(selected = mode == i, onClick = { mode = i }, label = { Text(label) })
                }
            }
        }
        when (mode) {
            0 -> {
                item { Text("${analects.chapters.size} chapters in James Legge's translation.", color = Muted) }
                items(analects.books, key = { it.number }) { b ->
                    RowCard("BOOK ${b.roman} · ${b.legge}", "${b.chapters.size} chapters",
                        b.chapters.take(3).joinToString(" · ") { it.title }) { onBook(b.number) }
                }
            }
            1 -> {
                item {
                    OutlinedTextField(
                        value = query, onValueChange = { query = it.take(80) }, singleLine = true, modifier = Modifier.fillMaxWidth(),
                        label = { Text("Search words, or a reference like II.4") },
                    )
                }
                item {
                    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        terms.forEach { t ->
                            FilterChip(selected = term == t.id, onClick = { term = if (term == t.id) null else t.id },
                                label = { Text("${t.character} ${t.pinyin}") })
                        }
                    }
                }
                val direct = Search.reference(analects, query)
                if (direct != null) item {
                    RowCard("GO TO", "${direct.citation} · ${direct.title}") { onChapter(direct.ref) }
                }
                if (query.trim().length >= 2 || term != null) {
                    val hits = Search.chapters(analects, query, term, comments = comments)
                    item { Text(if (hits.size == 1) "1 chapter" else "${hits.size} chapters", color = Muted) }
                    items(hits, key = { it.ref }) { c ->
                        RowCard(c.citation.uppercase(Locale.ROOT), c.title,
                            if (query.trim().length >= 2) Search.snippet(c.text, query) else c.paragraphs.first()) { onChapter(c.ref) }
                    }
                } else item {
                    Text("Type two letters or more, or choose one of the twelve words.", color = Muted)
                }
            }
            else -> {
                val marked = analects.chapters.filter { Prefs.marked(it.ref) }
                if (marked.isEmpty()) item { Text("Bookmark a chapter to keep it here.", color = Muted) }
                items(marked, key = { it.ref }) { c -> RowCard(c.citation.uppercase(Locale.ROOT), c.title, c.paragraphs.first()) { onChapter(c.ref) } }
            }
        }
    }
}

@Composable
fun BookPage(number: Int, onBack: () -> Unit, onChapter: (String) -> Unit) {
    val context = LocalContext.current
    val b = ConfuciusData.analects(context).book(number) ?: return
    Page {
        item { Header("Book ${b.roman}", b.legge, onBack) }
        item {
            Text("The book takes its name from its opening words, in Legge's romanization: ${b.legge}.",
                color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        items(b.chapters, key = { it.ref }) { c ->
            RowCard(c.ref, c.title, c.paragraphs.first()) { onChapter(c.ref) }
        }
    }
}

@Composable
fun TermPage(id: String, onBack: () -> Unit, onChapter: (String) -> Unit) {
    val context = LocalContext.current
    val t = ConfuciusData.term(context, id) ?: return
    val analects = ConfuciusData.analects(context)
    Page {
        item { Header(t.pinyin, "One of twelve words", onBack) }
        item {
            Panel {
                Text(t.character, color = Seal, fontSize = 72.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                LevelLabel(Level.MODERN, "Character and pinyin")
                Text("${t.character} · ${t.pinyin}", style = MaterialTheme.typography.titleLarge)
                Body(t.gloss)
            }
        }
        item {
            Panel {
                LevelLabel(Level.LEGGE, "How Legge renders it")
                Body(t.legge.joinToString(" · ") { "\"$it\"" })
                Text("One Chinese word, several English ones: the difference is the translator's, not the text's.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        item { Label("WHERE THE WORD STANDS · ${t.chapters.size}") }
        items(t.chapters, key = { "c$it" }) { ref ->
            analects.byRef(ref)?.let { c -> RowCard(c.citation.uppercase(Locale.ROOT), c.title, c.paragraphs.first()) { onChapter(c.ref) } }
        }
        if (t.related.isNotEmpty()) {
            item {
                Column {
                    Label("THE IDEA WITHOUT THE WORD")
                    Text("These chapters say the same thing in other words. They are ours to connect, not the text's.",
                        color = Muted, style = MaterialTheme.typography.bodySmall)
                }
            }
            items(t.related, key = { "r$it" }) { ref ->
                analects.byRef(ref)?.let { c -> RowCard(c.citation.uppercase(Locale.ROOT), c.title, c.paragraphs.first()) { onChapter(c.ref) } }
            }
        }
    }
}

@Composable
fun PersonPage(id: String, onBack: () -> Unit, onChapter: (String) -> Unit) {
    val context = LocalContext.current
    val p = ConfuciusData.person(context, id) ?: return
    val analects = ConfuciusData.analects(context)
    Page {
        item { Header(p.pinyin, "In Legge: ${p.legge}", onBack) }
        item {
            Panel {
                LevelLabel(Level.MODERN, "Who this is")
                Body(p.about)
                Text("Legge spells the name ${p.forms.joinToString(" or ") { "\"$it\"" }}; today it is written ${p.pinyin}.",
                    color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        }
        item { Label("CHAPTERS · ${p.chapters.size}") }
        items(p.chapters, key = { it }) { ref ->
            analects.byRef(ref)?.let { c -> RowCard(c.citation.uppercase(Locale.ROOT), c.title, c.paragraphs.first()) { onChapter(c.ref) } }
        }
    }
}
