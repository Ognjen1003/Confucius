package arcadia.com.confucius.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import arcadia.com.confucius.core.Chapter
import arcadia.com.confucius.core.Comment
import arcadia.com.confucius.core.Level
import arcadia.com.confucius.core.Term
import arcadia.com.confucius.platform.Prefs
import arcadia.com.confucius.ui.theme.*
import com.arcadia.core.PracticeHeaderActions
import com.arcadia.core.ReadingRecord
import java.util.Locale

@Composable
fun Page(state: LazyListState = rememberLazyListState(), content: LazyListScope.() -> Unit) {
    LazyColumn(
        Modifier.fillMaxSize().imePadding().testTag("page"), state = state,
        contentPadding = PaddingValues(20.dp, 16.dp, 20.dp, 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp), content = content,
    )
}

@Composable
fun Header(title: String, eyebrow: String, back: (() -> Unit)? = null) {
    Column {
        if (back != null) TextButton(onClick = back, contentPadding = PaddingValues(0.dp)) { Text("← Back") }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(eyebrow.uppercase(Locale.ROOT), color = Seal, style = MaterialTheme.typography.labelMedium, letterSpacing = 1.5.sp)
                Text(title, style = MaterialTheme.typography.headlineLarge, modifier = Modifier.padding(top = 4.dp))
            }
            PracticeHeaderActions(Seal)
        }
    }
}

@Composable
fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp), content = content)
    }
}

@Composable fun Label(text: String) = Text(text, color = Seal, style = MaterialTheme.typography.labelLarge)

/** Every block of text carries its level, so a reader always knows whose sentence it is. */
@Composable fun LevelLabel(level: Level, what: String? = null) = Label(if (what == null) level.label else "${level.name} · $what")

@Composable
fun Body(text: String, serif: Boolean = false) {
    SelectionContainer {
        Text(text, fontSize = (17 * Prefs.textSize).sp, lineHeight = (28 * Prefs.textSize).sp,
            fontFamily = if (serif) FontFamily.Serif else null)
    }
}

/** Legge's words for one chapter, as the book prints them, with the credit under them. */
@Composable
fun ChapterText(c: Chapter, credit: String) {
    Panel {
        LevelLabel(Level.TEXT, c.citation)
        c.paragraphs.forEach { Body(it, serif = true) }
        Text(credit, color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

/** 仁 ren · Legge: "perfect virtue". The character and pinyin are ours, the English word is Legge's. */
@Composable
fun TermChip(t: Term, word: String = t.legge.first(), onClick: () -> Unit) {
    AssistChip(onClick = onClick, label = { Text("${t.character} ${t.pinyin} · $word", maxLines = 1) })
}

/** The terms of one chapter, each with the English word Legge uses for it in that chapter. */
@Composable
fun TermRow(terms: List<Term>, chapter: Chapter, onTerm: (String) -> Unit) {
    if (terms.isEmpty()) return
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        terms.forEach { t -> TermChip(t, t.wordsIn(chapter).first()) { onTerm(t.id) } }
    }
}

@Composable
fun CommentPanel(c: Chapter, comment: Comment?) {
    if (comment == null) return
    Panel {
        LevelLabel(Level.MODERN, "A comment")
        Body(comment.reflection)
        HorizontalDivider()
        LevelLabel(Level.MODERN, "A question")
        Body(comment.question)
        comment.exercise?.let {
            HorizontalDivider()
            LevelLabel(Level.MODERN, "In an ordinary day")
            Body(it)
        }
        Text("The title \"${c.title}\", the comment and the question are ours, not the Analects'.",
            color = Muted, style = MaterialTheme.typography.bodySmall)
    }
}

/** One dialog for every note: a new one under [heading], or an edit of [old]. */
@Composable
fun NoteDialog(heading: String, old: ReadingRecord?, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by rememberSaveable(old?.id, heading) { mutableStateOf(old?.body.orEmpty()) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (old == null) "A note" else "Edit note") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(heading, color = Muted)
                OutlinedTextField(
                    value = text, onValueChange = { text = it.take(20_000) }, label = { Text("What you noticed") },
                    modifier = Modifier.fillMaxWidth().testTag("entry_text"), minLines = 4, maxLines = 10,
                )
                Text("Kept on this device only.", color = Muted, style = MaterialTheme.typography.bodySmall)
            }
        },
        confirmButton = { TextButton(enabled = text.isNotBlank(), onClick = { onSave(text) }) { Text("Save") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Composable
fun RowCard(eyebrow: String, title: String, body: String? = null, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Label(eyebrow)
            Text(title, style = MaterialTheme.typography.titleMedium)
            if (!body.isNullOrBlank()) Text(body, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
        }
    }
}
