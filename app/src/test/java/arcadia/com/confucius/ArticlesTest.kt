package arcadia.com.confucius

import com.arcadia.core.PracticeLoreLinks
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ArticlesTest {
    @Test fun `the articles of CONTENT md are all there`() {
        val ids = Raw.articles.map { it.id }
        listOf("who_wrote_the_analects", "one_word_many_translations", "legge", "names_in_legge", "hard_passages",
            "the_way_of_two_masters", "examining_oneself", "the_attributed_wings", "no_streaks_here").forEach { assertTrue(it, it in ids) }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test fun `every companion link of this app has its article`() {
        val links = PracticeLoreLinks.all.filter { it.sourcePackage == "arcadia.com.confucius" }
        assertEquals(3, links.size)
        links.forEach { l -> assertTrue(l.articleId, Raw.articles.any { it.id == l.articleId }) }
    }

    @Test fun `the two shorter classics are readable`() {
        assertEquals(listOf("great_learning", "doctrine_of_the_mean"), Raw.minor.map { it.id })
        Raw.minor.forEach { t -> assertTrue(t.id, t.paragraphs.size > 30) }
        assertTrue(Raw.minor[0].paragraphs.first().startsWith("WHAT THE GREAT LEARNING teaches"))
    }

    /** SAVJETI §10: the reader never sees an em dash, anywhere. */
    @Test fun `no em dash in anything bundled or displayed`() {
        File(Raw.app(), "src/main/res").walkTopDown().filter { it.isFile && (it.extension == "json" || it.extension == "xml") }.forEach { f ->
            assertFalse(f.path, f.readText().contains('—'))
        }
        File(Raw.app(), "src/main/java").walkTopDown().filter { it.isFile && it.extension == "kt" }.forEach { f ->
            assertFalse(f.path, f.readText().contains('—'))
        }
    }

    @Test fun `articles never claim Confucius wrote the book`() {
        Raw.articles.forEach { a -> a.paragraphs.forEach { assertFalse(a.id, it.contains("Confucius wrote the Analects")) } }
        // The Ten Wings attribution is stated only as a tradition.
        val wings = Raw.articles.first { it.id == "the_attributed_wings" }.paragraphs.first()
        assertTrue(wings.startsWith("Chinese tradition holds that Confucius wrote the Ten Wings"))
    }
}
