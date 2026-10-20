package arcadia.com.confucius.core

import org.json.JSONArray
import org.json.JSONObject

/**
 * Whose sentence it is (CLAUDE.md, "TRI RAZINE TVRDNJE"). Nothing on a screen is shown without one.
 *
 * [TEXT] stands in the Analects, in Legge's words. [LEGGE] is Legge's choice of an English word for a
 * Chinese one. [MODERN] is ours: titles, comments, questions, exercises, the characters and pinyin of
 * the twelve words (a dictionary, since the scan's Chinese is incomplete), articles.
 */
enum class Level(val label: String) {
    TEXT("TEXT · The Analects"),
    LEGGE("LEGGE · His word"),
    MODERN("MODERN · Ours"),
}

/** One chapter of the Analects. [paragraphs] are Legge's words (TEXT); [title] is ours (MODERN). */
data class Chapter(
    val ref: String,
    val book: Int,
    val number: Int,
    val paragraphs: List<String>,
    val title: String,
    val terms: List<String>,
    val people: List<String>,
) {
    val text: String get() = paragraphs.joinToString("\n\n")
    /** "Analects II.4": how a chapter is cited everywhere, on the image too. */
    val citation: String get() = "Analects $ref"
}

data class Book(val number: Int, val roman: String, val legge: String, val chapters: List<Chapter>)

class Analects(val books: List<Book>, val sourceSha256: String, val edition: String, val credit: String) {
    val chapters: List<Chapter> = books.flatMap { it.chapters }
    private val index: Map<String, Int> = chapters.withIndex().associate { (i, c) -> c.ref to i }

    init {
        require(books.isNotEmpty()) { "no books" }
        require(index.size == chapters.size) { "duplicate chapter ref" }
    }

    fun byRef(ref: String): Chapter? = index[ref]?.let { chapters[it] }
    fun book(number: Int): Book? = books.firstOrNull { it.number == number }
    fun position(ref: String): Int? = index[ref]
    fun next(ref: String): Chapter? = index[ref]?.let { chapters.getOrNull(it + 1) }
    fun previous(ref: String): Chapter? = index[ref]?.let { chapters.getOrNull(it - 1) }

    companion object {
        fun parse(json: String): Analects {
            val root = JSONObject(json)
            val books = root.getJSONArray("books").objects().map { b ->
                val n = b.getInt("number")
                Book(n, b.getString("roman"), b.getString("legge"), b.getJSONArray("chapters").objects().map { c ->
                    Chapter(
                        c.getString("ref"), n, c.getInt("number"), c.getJSONArray("paragraphs").strings(),
                        c.optString("title"), c.optJSONArray("terms")?.strings().orEmpty(),
                        c.optJSONArray("people")?.strings().orEmpty(),
                    )
                })
            }
            return Analects(books, root.getString("sourceSha256"), root.getString("edition"), root.getString("credit"))
        }
    }
}

/** Our comment on one chapter (MODERN): what it says and how it may be read, a question, sometimes an exercise. */
data class Comment(val ref: String, val reflection: String, val question: String, val exercise: String?)

object Commentary {
    fun parse(json: String): Map<String, Comment> = JSONObject(json).getJSONArray("chapters").objects().associate { o ->
        o.getString("ref") to Comment(
            o.getString("ref"), o.getString("reflection"), o.getString("question"),
            o.optString("exercise").takeIf { it.isNotBlank() },
        )
    }
}

/**
 * One of the twelve words. [character] and [pinyin] are MODERN (a dictionary); [legge] are the English
 * renderings Legge actually uses for it (LEGGE); [chapters] are where the word stands, confirmed by hand
 * (THEORY §3); [related] are chapters that state the idea without the word, and say so.
 */
data class Term(
    val id: String,
    val character: String,
    val pinyin: String,
    val legge: List<String>,
    val gloss: String,
    val chapters: List<String>,
    val related: List<String>,
) {
    /**
     * Legge's renderings that stand in [chapter], without one that only appears inside a longer one found
     * there ("faithful" inside "faithfulness"); all of them if none stands there.
     */
    fun wordsIn(chapter: Chapter): List<String> {
        val found = legge.filter { chapter.text.contains(it, ignoreCase = true) }
        return found.filterNot { w -> found.any { it != w && it.contains(w, ignoreCase = true) } }.ifEmpty { legge }
    }
}

object Terms {
    fun parse(json: String): List<Term> = JSONObject(json).getJSONArray("terms").objects().map { o ->
        Term(
            o.getString("id"), o.getString("character"), o.getString("pinyin"), o.getJSONArray("legge").strings(),
            o.getString("gloss"), o.getJSONArray("chapters").strings(), o.optJSONArray("related")?.strings().orEmpty(),
        )
    }
}

/** A person of the Analects: Legge's spelling as the text prints it, and today's pinyin (MODERN). */
data class Person(
    val id: String,
    val legge: String,
    val pinyin: String,
    val forms: List<String>,
    val about: String,
    val chapters: List<String>,
)

object People {
    fun parse(json: String): List<Person> = JSONObject(json).getJSONArray("people").objects().map { o ->
        Person(
            o.getString("id"), o.getString("legge"), o.getString("pinyin"), o.getJSONArray("forms").strings(),
            o.getString("about"), o.getJSONArray("chapters").strings(),
        )
    }
}

data class Article(val id: String, val title: String, val kicker: String, val paragraphs: List<String>)

object Articles {
    fun parse(json: String): List<Article> = JSONObject(json).getJSONArray("articles").objects().map { o ->
        Article(o.getString("id"), o.getString("title"), o.getString("kicker"), o.getJSONArray("paragraphs").strings())
    }
}

/** The Great Learning and the Doctrine of the Mean, read in Lore. A paragraph starting "## " is a heading. */
data class MinorText(val id: String, val title: String, val credit: String, val paragraphs: List<String>)

object MinorTexts {
    fun parse(json: String): List<MinorText> = JSONObject(json).getJSONArray("texts").objects().map { o ->
        MinorText(o.getString("id"), o.getString("title"), o.getString("credit"), o.getJSONArray("paragraphs").strings())
    }
}

internal fun JSONArray.objects(): List<JSONObject> = (0 until length()).map { getJSONObject(it) }
internal fun JSONArray.strings(): List<String> = (0 until length()).map { getString(it) }
