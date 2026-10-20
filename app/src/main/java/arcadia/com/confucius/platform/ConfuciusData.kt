package arcadia.com.confucius.platform

import android.content.Context
import arcadia.com.confucius.R
import arcadia.com.confucius.core.Analects
import arcadia.com.confucius.core.Article
import arcadia.com.confucius.core.Articles
import arcadia.com.confucius.core.Comment
import arcadia.com.confucius.core.Commentary
import arcadia.com.confucius.core.Course
import arcadia.com.confucius.core.MinorText
import arcadia.com.confucius.core.MinorTexts
import arcadia.com.confucius.core.People
import arcadia.com.confucius.core.Person
import arcadia.com.confucius.core.Term
import arcadia.com.confucius.core.Terms
import java.time.LocalDate

/**
 * The bundled content, parsed once per process. About half a megabyte of JSON: the app parses it off the
 * main thread before the first screen ([preload]); a cold start on a slow device otherwise blocks input long
 * enough for an ANR. The widget and the reminder read it on their own broadcast, where that cost is allowed.
 */
object ConfuciusData {
    const val PRACTICE_ID = "confucius_daily"

    @Volatile private var analects: Analects? = null
    @Volatile private var commentary: Map<String, Comment>? = null
    @Volatile private var terms: List<Term>? = null
    @Volatile private var people: List<Person>? = null
    @Volatile private var course: Course? = null
    @Volatile private var articles: List<Article>? = null
    @Volatile private var minor: List<MinorText>? = null

    private fun raw(context: Context, id: Int): String =
        context.resources.openRawResource(id).bufferedReader().use { it.readText() }

    @Synchronized fun analects(context: Context): Analects =
        analects ?: Analects.parse(raw(context, R.raw.analects)).also { analects = it }

    @Synchronized fun commentary(context: Context): Map<String, Comment> =
        commentary ?: Commentary.parse(raw(context, R.raw.commentary)).also { commentary = it }

    @Synchronized fun terms(context: Context): List<Term> =
        terms ?: Terms.parse(raw(context, R.raw.terms)).also { terms = it }

    @Synchronized fun people(context: Context): List<Person> =
        people ?: People.parse(raw(context, R.raw.people)).also { people = it }

    @Synchronized fun course(context: Context): Course =
        course ?: Course.parse(raw(context, R.raw.course)).also { course = it }

    @Synchronized fun articles(context: Context): List<Article> =
        articles ?: Articles.parse(raw(context, R.raw.articles)).also { articles = it }

    @Synchronized fun minorTexts(context: Context): List<MinorText> =
        minor ?: MinorTexts.parse(raw(context, R.raw.texts_minor)).also { minor = it }

    fun term(context: Context, id: String): Term? = terms(context).firstOrNull { it.id == id }
    fun person(context: Context, id: String): Person? = people(context).firstOrNull { it.id == id }

    /** True once everything a screen reads synchronously has been parsed. */
    fun loaded(): Boolean = analects != null && commentary != null && terms != null && people != null &&
        course != null && articles != null && minor != null

    /** Parses everything once; called off the main thread before the first screen is drawn. */
    fun preload(context: Context) {
        analects(context); commentary(context); terms(context); people(context)
        course(context); articles(context); minorTexts(context)
    }

    fun today(): LocalDate = LocalDate.now()
}
