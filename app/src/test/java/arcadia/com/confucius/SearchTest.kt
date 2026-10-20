package arcadia.com.confucius

import arcadia.com.confucius.core.Search
import org.junit.Assert.*
import org.junit.Test

class SearchTest {
    private val a = Raw.analects

    @Test fun `a typed reference goes to its chapter`() {
        listOf("II.4", "ii.4", "2.4", "II 4", " ii : 4 ").forEach { assertEquals(it, "II.4", Search.reference(a, it)?.ref) }
        assertNull(Search.reference(a, "XX.4"))
        assertNull(Search.reference(a, "reciprocity"))
    }

    @Test fun `text search ignores case and keeps the order of the book`() {
        assertEquals(listOf("XV.23"), Search.chapters(a, "RECIPROCITY").map { it.ref })
        assertEquals("II.1", Search.chapters(a, "north polar star").first().ref)
    }

    @Test fun `a term filter returns exactly the tagged chapters`() {
        assertEquals(listOf("IV.15", "XV.23"), Search.chapters(a, "", term = "shu").map { it.ref })
    }
}
