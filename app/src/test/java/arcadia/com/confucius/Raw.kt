package arcadia.com.confucius

import arcadia.com.confucius.core.Analects
import arcadia.com.confucius.core.Articles
import arcadia.com.confucius.core.Commentary
import arcadia.com.confucius.core.Course
import arcadia.com.confucius.core.MinorTexts
import arcadia.com.confucius.core.People
import arcadia.com.confucius.core.Terms
import java.io.File

/** The real bundled files, never a copy: a bad edit to the data fails the build (SAVJETI §9). */
object Raw {
    /** The app module, whether Gradle runs the tests from the module or from the project root. */
    fun app(): File = if (File("src/main/res").isDirectory) File(".") else File("app")
    fun dir(): File = File(app(), "src/main/res/raw")
    fun text(name: String): String = File(dir(), name).readText()
    val analects by lazy { Analects.parse(text("analects.json")) }
    val commentary by lazy { Commentary.parse(text("commentary.json")) }
    val terms by lazy { Terms.parse(text("terms.json")) }
    val people by lazy { People.parse(text("people.json")) }
    val course by lazy { Course.parse(text("course.json")) }
    val articles by lazy { Articles.parse(text("articles.json")) }
    val minor by lazy { MinorTexts.parse(text("texts_minor.json")) }
}
