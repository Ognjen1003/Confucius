package arcadia.com.confucius.core

import java.text.Normalizer
import java.util.Locale

/**
 * Search over the Analects by text, by one of the twelve words, or by a person. No ranking: hits come
 * in the order of the book, which is the only order the text itself has.
 */
object Search {
    fun fold(text: String): String = Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD)
        .replace(Regex("\\p{M}"), "").replace('’', '\'').replace('‘', '\'').replace('“', '"').replace('”', '"')

    /** A typed reference ("ii.4", "II 4", "2.4") goes straight to that chapter. */
    fun reference(analects: Analects, query: String): Chapter? {
        val m = Regex("^\\s*([ivxl]+|\\d{1,2})\\s*[. :]\\s*(\\d{1,2})\\s*$", RegexOption.IGNORE_CASE).find(query) ?: return null
        val bookPart = m.groupValues[1]
        val book = bookPart.toIntOrNull() ?: analects.books.firstOrNull { it.roman.equals(bookPart, ignoreCase = true) }?.number
        return book?.let { b -> analects.byRef("${analects.book(b)?.roman}.${m.groupValues[2].toInt()}") }
    }

    fun chapters(
        analects: Analects, query: String, term: String? = null, person: String? = null,
        comments: Map<String, Comment> = emptyMap(),
    ): List<Chapter> {
        val wanted = fold(query.trim())
        return analects.chapters.filter { c ->
            (term == null || term in c.terms) && (person == null || person in c.people) &&
                (wanted.length < 2 || wanted in fold("${c.ref} ${c.title} ${c.text} ${comments[c.ref]?.reflection.orEmpty()}"))
        }
    }

    internal fun snippet(text: String, query: String, around: Int = 70): String {
        val folded = fold(text)
        val at = folded.indexOf(fold(query.trim())).takeIf { it >= 0 } ?: return text.take(around * 2)
        val start = (at - around).coerceAtLeast(0).let { s -> if (s == 0) 0 else text.indexOf(' ', s).takeIf { it in s until at } ?: s }
        val end = (at + query.trim().length + around).coerceAtMost(text.length)
            .let { e -> if (e == text.length) e else text.lastIndexOf(' ', e).takeIf { it > at } ?: e }
        return (if (start > 0) "…" else "") + text.substring(start, end).trim() + (if (end < text.length) "…" else "")
    }
}
