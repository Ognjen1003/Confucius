package arcadia.com.confucius.ui

import android.Manifest
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import arcadia.com.confucius.core.Article
import arcadia.com.confucius.core.Level
import arcadia.com.confucius.core.MinorText
import arcadia.com.confucius.core.StepKind
import arcadia.com.confucius.platform.*
import arcadia.com.confucius.ui.theme.*
import com.arcadia.core.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

@Composable
fun PracticePage(onChapter: (String) -> Unit, onTerm: (String) -> Unit, onNote: (String) -> Unit) {
    val context = LocalContext.current
    val course = ConfuciusData.course(context)
    val analects = ConfuciusData.analects(context)
    val state = Practice.state(course.id)
    val unit = course.units[state.unitIndex.coerceIn(0, course.units.lastIndex)]
    val step = unit.steps[state.stepIndex.coerceIn(0, unit.steps.lastIndex)]
    val term = ConfuciusData.term(context, unit.id)
    val list = rememberLazyListState()
    val scope = rememberCoroutineScope()
    Page(list) {
        item { Header(course.title, "Practice") }
        if (state.finished) {
            item {
                Panel {
                    Label("ALL TWELVE")
                    Text("You have walked through the twelve words.", style = MaterialTheme.typography.titleLarge)
                    Text("They stay in the book, to be met again in any chapter, or the course can begin from the first word.")
                    OutlinedButton(onClick = { Practice.restart(course.id) }, modifier = Modifier.fillMaxWidth()) { Text("Begin again") }
                }
            }
        } else {
            item { Text("Word ${state.unitIndex + 1} of ${course.units.size} · step ${state.stepIndex + 1} of ${unit.steps.size}", color = Muted) }
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (term != null) Text(term.character, color = Seal, style = MaterialTheme.typography.headlineLarge,
                        modifier = Modifier.padding(end = 14.dp))
                    Column {
                        Text(unit.title, style = MaterialTheme.typography.headlineMedium)
                        Text(unit.summary, color = Muted)
                    }
                }
            }
            item {
                Panel {
                    LevelLabel(Level.MODERN, "${step.kind.label}: ${step.title}")
                    Body(step.text)
                    val c = step.chapter?.let(analects::byRef)
                    if (c != null) {
                        HorizontalDivider()
                        LevelLabel(Level.TEXT, c.citation)
                        c.paragraphs.forEach { Body(it, serif = true) }
                        Text(analects.credit, color = Muted, style = MaterialTheme.typography.bodySmall)
                        TextButton(onClick = { onChapter(c.ref) }, contentPadding = PaddingValues(0.dp)) { Text("Open in the book") }
                    }
                    if (step.kind == StepKind.TERM && term != null) {
                        TextButton(onClick = { onTerm(term.id) }, contentPadding = PaddingValues(0.dp)) { Text("Every chapter with ${term.character} ${term.pinyin}") }
                    }
                    if (step.kind == StepKind.QUESTION || step.kind == StepKind.EXERCISE) OutlinedButton(
                        onClick = { onNote("Twelve words · ${unit.title} · ${step.title}") }, modifier = Modifier.fillMaxWidth(),
                    ) { Text("Write in the journal") }
                }
            }
            item {
                Button(onClick = {
                    Practice.advance(course.practice)
                    Practice.markPracticed(ConfuciusData.PRACTICE_ID)
                    WidgetSchedule.refreshAll(context)
                    scope.launch { list.scrollToItem(0) }
                }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (state.stepIndex < unit.steps.lastIndex) "Next step" else if (state.unitIndex < course.units.lastIndex) "Finish this word" else "Finish the course")
                }
            }
        }
        item { Text("Days practised: ${Practice.state(ConfuciusData.PRACTICE_ID).daysPracticed}", color = Muted) }
        item { Label("THE TWELVE WORDS") }
        items(course.units.size) { i ->
            val u = course.units[i]
            val t = ConfuciusData.term(context, u.id)
            Card(onClick = { Practice.goToUnit(course.practice, i) }, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text(t?.character ?: "${i + 1}", color = Seal, style = MaterialTheme.typography.titleLarge, modifier = Modifier.width(52.dp))
                    Column(Modifier.weight(1f)) {
                        Text(u.title, style = MaterialTheme.typography.titleMedium)
                        Text(u.summary, color = Muted, style = MaterialTheme.typography.bodySmall, maxLines = 2)
                    }
                    if (!state.finished && i == state.unitIndex) Text("now", color = Seal)
                }
            }
        }
    }
}

@Composable
fun JournalPage(onChapter: (String) -> Unit, onNew: () -> Unit, onEdit: (ReadingRecord) -> Unit) {
    var deleting by remember { mutableStateOf<ReadingRecord?>(null) }
    val entries = Journal.entries()
    Page {
        item { Header("Kept for yourself", "Journal") }
        item { OutlinedButton(onClick = onNew, modifier = Modifier.fillMaxWidth()) { Text("New note") } }
        if (entries.isEmpty()) item {
            Panel {
                Text("Nothing written yet", style = MaterialTheme.typography.titleLarge)
                Text("Notes stay on this device. They are never shared, never backed up and never counted.", color = Muted)
            }
        }
        items(entries, key = { it.id }) { e ->
            Panel {
                Text(e.title, style = MaterialTheme.typography.titleMedium)
                val day = Instant.ofEpochMilli(e.epochMillis).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                if (day !in e.title) Text(day, color = Muted)
                Body(e.body)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { onEdit(e) }) { Text("Edit") }
                    if (e.payload.isNotBlank()) TextButton(onClick = { onChapter(e.payload) }) { Text("Read ${e.payload}") }
                    TextButton(onClick = { deleting = e }) { Text("Delete") }
                }
            }
        }
    }
    deleting?.let { e ->
        AlertDialog(onDismissRequest = { deleting = null }, title = { Text("Delete this note?") }, text = { Text(e.title) },
            confirmButton = { TextButton(onClick = { Journal.delete(e); deleting = null }) { Text("Delete note") } },
            dismissButton = { TextButton(onClick = { deleting = null }) { Text("Keep it") } })
    }
}

@Composable
fun LorePage(onArticle: (String) -> Unit, onTerm: (String) -> Unit, onPerson: (String) -> Unit, onText: (String) -> Unit) {
    val context = LocalContext.current
    Page {
        item { Header("Around the book", "Lore") }
        items(ConfuciusData.articles(context), key = { it.id }) { a ->
            Card(onClick = { onArticle(a.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Label(a.kicker)
                    Text(a.title, style = MaterialTheme.typography.titleLarge)
                }
            }
        }
        item { Label("TWO SHORTER CLASSICS") }
        items(ConfuciusData.minorTexts(context), key = { it.id }) { t ->
            RowCard("THE FOUR BOOKS · ${t.credit.uppercase(Locale.ROOT)}", t.title) { onText(t.id) }
        }
        item { Label("THE TWELVE WORDS") }
        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ConfuciusData.terms(context).forEach { t -> TermChip(t) { onTerm(t.id) } }
            }
        }
        item { Label("PEOPLE OF THE ANALECTS") }
        items(ConfuciusData.people(context), key = { it.id }) { p ->
            RowCard("IN LEGGE: ${p.legge.uppercase(Locale.ROOT)}", p.pinyin, p.about) { onPerson(p.id) }
        }
        item { ArcadiaBannerAd() }
    }
}

@Composable
fun ArticlePage(a: Article, onBack: () -> Unit) {
    Page {
        item { Header(a.title, a.kicker, onBack) }
        item { LevelLabel(Level.MODERN, "An article") }
        items(a.paragraphs) { Body(it) }
        item { PracticeLoreBridge(a.id) }
    }
}

@Composable
fun MinorTextPage(t: MinorText, onBack: () -> Unit) {
    Page {
        item { Header(t.title, "Lore · ${t.credit}", onBack) }
        item {
            Text("Two short classics that the later tradition read together with the Analects and the Mencius as the Four Books. " +
                "Here in Legge's English, without his notes.", color = Muted, style = MaterialTheme.typography.bodySmall)
        }
        item { LevelLabel(Level.TEXT, t.title) }
        items(t.paragraphs) { p ->
            if (p.startsWith("## ")) Text(p.removePrefix("## "), color = Seal, style = MaterialTheme.typography.titleLarge)
            else Body(p, serif = true)
        }
    }
}

@Composable
fun ReadingSettings() {
    val context = LocalContext.current
    var enabled by remember { mutableStateOf(Reminders.enabled(context)) }
    var minute by remember { mutableIntStateOf(Reminders.minute(context)) }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        Reminders.set(context, granted); enabled = granted
    }
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Reading", style = MaterialTheme.typography.titleLarge)
        Text("Text size", color = Muted)
        Slider(value = Prefs.textSize, onValueChange = Prefs::readingSize, valueRange = .85f..1.5f)
        HorizontalDivider()
        Text("Daily reminder", style = MaterialTheme.typography.titleLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("The day's chapter at your chosen hour", modifier = Modifier.weight(1f))
            Switch(checked = enabled, onCheckedChange = { on ->
                if (on && Build.VERSION.SDK_INT >= 33 &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                ) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                else { Reminders.set(context, on); enabled = on }
            })
        }
        OutlinedButton(onClick = {
            TimePickerDialog(context, { _, h, m -> minute = h * 60 + m; Reminders.set(context, enabled, minute) }, minute / 60, minute % 60, true).show()
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Reminder time: ${String.format(Locale.ROOT, "%02d:%02d", minute / 60, minute % 60)}")
        }
    }
}
