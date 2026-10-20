package arcadia.com.confucius

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.security.MessageDigest

class AnalectsDataTest {
    private val a = Raw.analects
    private val romans = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X",
        "XI", "XII", "XIII", "XIV", "XV", "XVI", "XVII", "XVIII", "XIX", "XX")

    @Test fun `twenty books, 499 chapters, Book XX has three`() {
        assertEquals(20, a.books.size)
        assertEquals(listOf(16, 24, 26, 26, 27, 28, 37, 21, 30, 18, 25, 24, 30, 47, 41, 14, 26, 11, 25, 3), a.books.map { it.chapters.size })
        assertEquals(499, a.chapters.size)
        assertEquals(3, a.books.last().chapters.size)
    }

    @Test fun `books and chapters are in strict order and references match`() {
        a.books.forEachIndexed { i, b ->
            assertEquals(i + 1, b.number)
            assertEquals(romans[i], b.roman)
            b.chapters.forEachIndexed { j, c ->
                assertEquals(j + 1, c.number)
                assertEquals("${b.roman}.${j + 1}", c.ref)
            }
        }
    }

    @Test fun `no Chinese characters, no em dash, no double hyphen in the text`() {
        a.chapters.forEach { c ->
            c.paragraphs.forEach { p ->
                assertFalse(c.ref, p.any { it.code in 0x3000..0x9FFF || it.code in 0xFF00..0xFFEF })
                assertFalse(c.ref, p.contains('—'))
                assertFalse(c.ref, p.contains("--"))
                assertTrue(c.ref, p.isNotBlank())
            }
        }
    }

    @Test fun `numbered sections run 1, 2, 3 within a chapter`() {
        a.chapters.forEach { c ->
            val nums = c.paragraphs.mapNotNull { Regex("^(\\d+)\\. ").find(it)?.groupValues?.get(1)?.toInt() }
            if (nums.isNotEmpty()) {
                assertEquals(c.ref, c.paragraphs.size, nums.size)
                assertEquals(c.ref, (1..nums.size).toList(), nums)
            }
        }
    }

    @Test fun `the corrections of extract py are in the bundle`() {
        assertTrue(a.byRef("II.14")!!.text.contains("catholic and not partisan"))
        assertTrue(a.byRef("XIX.14")!!.text.startsWith("Tsze-yu said"))
        assertFalse(a.byRef("III.19")!!.text.contains("according to according to"))
        assertEquals(4, a.byRef("XIII.20")!!.paragraphs.size)
        assertTrue(a.byRef("XV.23")!!.text.contains("RECIPROCITY"))
    }

    @Test fun `every chapter carries our title`() {
        a.chapters.forEach { assertTrue(it.ref, it.title.isNotBlank() && it.title.length <= 48) }
    }

    @Test fun `the recorded input hash is the snapshot in tools`() {
        val source = File(Raw.app(), "../tools/source/CFU.md").takeIf { it.isFile } ?: File("tools/source/CFU.md")
        val sha = MessageDigest.getInstance("SHA-256").digest(source.readBytes()).joinToString("") { "%02x".format(it) }
        assertEquals(sha, a.sourceSha256)
    }
}
