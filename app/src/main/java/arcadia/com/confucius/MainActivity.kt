package arcadia.com.confucius

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arcadia.com.confucius.platform.*
import arcadia.com.confucius.ui.*
import arcadia.com.confucius.ui.theme.*
import com.arcadia.core.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private var incoming by mutableStateOf<String?>(null)
    private var request by mutableIntStateOf(0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConsentManager.gather(this)
        incoming = requested(intent)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent { ConfuciusTheme { ConfuciusApp(incoming, request) } }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        // Only a link or the reminder moves the reader; coming back from the launcher keeps their place.
        if (intent.data != null || intent.getBooleanExtra(EXTRA_TODAY, false)) {
            incoming = requested(intent)
            request++
        }
    }

    companion object { const val EXTRA_TODAY = "today" }

    /** `arcadia://confucius?chapter=II.4` opens a chapter; anything else opens Today. */
    private fun requested(intent: Intent): String? = intent.data
        ?.takeIf { it.scheme == "arcadia" && it.host == "confucius" }
        ?.getQueryParameter("chapter")?.uppercase()?.takeIf { it.matches(Regex("[IVXL]{1,5}\\.\\d{1,2}")) }
}

private val tabs = listOf("Today" to "日", "Analects" to "☰", "Practice" to "仁", "Journal" to "✎", "Lore" to "▤")

/**
 * What is open over a tab, as a stack of "kind:id" entries ("chapter:II.4", "term:ren"), so Back always
 * returns to the screen a reader or a word was opened from. Saved as one string, it survives recreation.
 */
private fun String.entries(): List<String> = if (isEmpty()) emptyList() else split('|')

@Composable
private fun ConfuciusApp(incoming: String?, request: Int) {
    val context = LocalContext.current
    val ready by produceState(ConfuciusData.loaded()) {
        if (!value) value = withContext(Dispatchers.IO) { ConfuciusData.preload(context); true }
    }
    if (!ready) { Box(Modifier.fillMaxSize().background(Ink)); return }
    val analects = remember { ConfuciusData.analects(context) }
    val today by produceState(ConfuciusData.today()) { while (true) { value = ConfuciusData.today(); delay(30_000) } }

    var tab by rememberSaveable { mutableIntStateOf(0) }
    var stack by rememberSaveable { mutableStateOf("") }
    // The open note dialog survives recreation: its heading, chapter and the entry being edited.
    var noteOpen by rememberSaveable { mutableStateOf(false) }
    var noteHeading by rememberSaveable { mutableStateOf("") }
    var noteChapter by rememberSaveable { mutableStateOf<String?>(null) }
    var noteOld by rememberSaveable { mutableStateOf<String?>(null) }

    val cadence = rememberInterstitialCadence()
    // Each screen keeps its own saved state (the scroll of a reader, a search query) under the path that
    // led to it, so Back returns to the same place on the page and not only to the same page.
    val saved = rememberSaveableStateHolder()
    fun push(kind: String, id: String) {
        val top = stack.entries().lastOrNull()
        val entry = "$kind:$id"
        // A chapter replaces a chapter (previous / next), so Back leaves the reader rather than paging back.
        stack = (if (kind == "chapter" && top?.startsWith("chapter:") == true) stack.entries().dropLast(1) else stack.entries())
            .plus(entry).joinToString("|")
    }
    fun pop() {
        saved.removeState("$tab/$stack")
        stack = stack.entries().dropLast(1).joinToString("|")
    }
    LaunchedEffect(incoming, request) {
        if (incoming != null || request > 0) {
            tab = 0
            stack = incoming?.takeIf { analects.byRef(it) != null }?.let { "chapter:$it" }.orEmpty()
        }
    }
    fun note(heading: String, chapter: String? = null, old: ReadingRecord? = null) {
        noteHeading = heading; noteChapter = chapter; noteOld = old?.id; noteOpen = true
    }
    val openChapter: (String) -> Unit = { push("chapter", it) }
    val openTerm: (String) -> Unit = { push("term", it) }
    val openPerson: (String) -> Unit = { push("person", it) }
    val noteFor: (arcadia.com.confucius.core.Chapter) -> Unit = { c -> note("${c.citation} · ${c.title}", c.ref) }

    val top = stack.entries().lastOrNull()
    val kind = top?.substringBefore(':')
    val id = top?.substringAfter(':')
    val screen = kind ?: tabs[tab].first.lowercase()
    LaunchedEffect(screen) { AnalyticsManager.logScreenView(screen, "MainActivity") }
    BackHandler(enabled = top != null || tab != 0) { if (top != null) pop() else tab = 0 }

    CompositionLocalProvider(LocalPracticeSettings provides { if (kind != "settings") push("settings", "") }) {
        NavigationSuiteScaffold(
            containerColor = Ink, contentColor = Paper,
            navigationSuiteColors = NavigationSuiteDefaults.colors(
                navigationBarContainerColor = Deep, navigationRailContainerColor = Deep, navigationDrawerContainerColor = Deep,
            ),
            navigationSuiteItems = {
                tabs.forEachIndexed { index, (label, glyph) ->
                    item(
                        selected = tab == index && kind != "settings",
                        icon = { Text(glyph, fontSize = 22.sp) }, label = { Text(label, maxLines = 1) },
                        onClick = {
                            val previous = tab
                            stack = ""; tab = index
                            // Today keeps a due ad for the next move: nothing is put in front of the day's chapter.
                            if (index != previous) cadence.tick(mayShow = index != 0)
                        },
                    )
                }
            },
        ) {
            Scaffold(containerColor = Ink, contentColor = Paper) { insets ->
                Box(Modifier.fillMaxSize().padding(insets), contentAlignment = Alignment.TopCenter) {
                    Box(Modifier.widthIn(max = 760.dp).fillMaxSize()) {
                        saved.SaveableStateProvider("$tab/$stack") {
                            when (kind) {
                                "settings" -> PracticeSettingsPage("Confucius", ::pop, { ReadingSettings() },
                                    setOf("arcadia.com.tao", "arcadia.com.stoic", "arcadia.com.iching"))
                                "chapter" -> ChapterPage(id!!, ::pop, openChapter, openTerm, openPerson, noteFor)
                                "book" -> BookPage(id!!.toInt(), ::pop, openChapter)
                                "term" -> TermPage(id!!, ::pop, openChapter)
                                "person" -> PersonPage(id!!, ::pop, openChapter)
                                "article" -> ConfuciusData.articles(context).firstOrNull { it.id == id }?.let { ArticlePage(it, ::pop) }
                                "text" -> ConfuciusData.minorTexts(context).firstOrNull { it.id == id }?.let { MinorTextPage(it, ::pop) }
                                else -> when (tab) {
                                    0 -> TodayPage(today, openChapter, openTerm, noteFor)
                                    1 -> AnalectsPage({ push("book", it.toString()) }, openChapter)
                                    2 -> PracticePage(openChapter, openTerm) { heading -> note(heading) }
                                    3 -> JournalPage(openChapter, { note("Note · $today") }) { old ->
                                        note(old.title, old.payload.takeIf { it.isNotBlank() }, old)
                                    }
                                    else -> LorePage({ push("article", it) }, openTerm, openPerson) { push("text", it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (noteOpen) {
        val old = noteOld?.let { oid -> ReadingHistory.records.firstOrNull { it.id == oid } }
        NoteDialog(noteHeading, old, { noteOpen = false }) { text ->
            Journal.save(noteHeading, text, noteChapter, old)
            noteOpen = false
        }
    }
}
