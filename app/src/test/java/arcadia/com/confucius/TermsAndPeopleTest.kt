package arcadia.com.confucius

import org.junit.Assert.*
import org.junit.Test

class TermsAndPeopleTest {
    private val a = Raw.analects

    @Test fun `twelve terms, distinct characters, each with at least two chapters`() {
        assertEquals(12, Raw.terms.size)
        assertEquals(12, Raw.terms.map { it.character }.toSet().size)
        Raw.terms.forEach { t ->
            assertTrue(t.id, t.chapters.isNotEmpty())
            assertTrue(t.id, t.chapters.size + t.related.size >= 2)
            assertTrue(t.id, t.gloss.length > 60 && !t.gloss.contains('—'))
            (t.chapters + t.related).forEach { assertNotNull("${t.id} $it", a.byRef(it)) }
            assertTrue(t.id, t.related.none { it in t.chapters })
        }
    }

    @Test fun `every Legge word listed for a term stands in one of its chapters`() {
        Raw.terms.forEach { t ->
            val text = t.chapters.joinToString(" ") { a.byRef(it)!!.text }
            t.legge.forEach { w -> assertTrue("${t.id}: $w", text.contains(w, ignoreCase = true)) }
        }
    }

    @Test fun `the reader names only the words Legge uses in that chapter`() {
        val zhong = Raw.terms.first { it.id == "zhong" }
        assertEquals(listOf("true to the principles of our nature"), zhong.wordsIn(a.byRef("IV.15")!!))
        val junzi = Raw.terms.first { it.id == "junzi" }
        assertEquals(listOf("man of complete virtue"), junzi.wordsIn(a.byRef("I.1")!!))
        assertEquals(listOf("Chun-tsze"), junzi.wordsIn(a.byRef("III.7")!!))
    }

    @Test fun `chapter tags in analects json agree with terms json`() {
        Raw.terms.forEach { t ->
            assertEquals(t.id, t.chapters.toSet(), a.chapters.filter { t.id in it.terms }.map { it.ref }.toSet())
        }
    }

    @Test fun `each person is named in every chapter listed for them`() {
        Raw.people.forEach { p ->
            assertTrue(p.id, p.chapters.isNotEmpty() && p.about.length > 40)
            p.chapters.forEach { ref ->
                val text = a.byRef(ref)!!.text
                assertTrue("${p.id} $ref", p.forms.any { f -> Regex("\\b${Regex.escape(f)}\\b").containsMatchIn(text) })
            }
            assertEquals(p.id, p.chapters.toSet(), a.chapters.filter { p.id in it.people }.map { it.ref }.toSet())
        }
    }

    @Test fun `homonyms are kept apart`() {
        val hui = Raw.people.first { it.id == "yan_hui" }
        assertFalse("XVIII.2 is Hui of Liu-hsia", "XVIII.2" in hui.chapters)
        val ziyou = Raw.people.first { it.id == "ziyou" }
        assertFalse("XIV.9 is Tsze-yu of Tung-li", "XIV.9" in ziyou.chapters)
        assertTrue("XIX.14 is Tsze-yu, as the Chinese reads", "XIX.14" in ziyou.chapters)
    }
}
