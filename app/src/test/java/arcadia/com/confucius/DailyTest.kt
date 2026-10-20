package arcadia.com.confucius

import arcadia.com.confucius.core.Daily
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class DailyTest {
    private val a = Raw.analects
    private val n = a.chapters.size

    /** Pinned in THEORY.md §2. */
    @Test fun `the anchors of THEORY md`() {
        assertEquals(499, n)
        assertEquals("I.1", Daily.chapter(LocalDate.of(2026, 1, 1), a).ref)
        assertEquals("I.2", Daily.chapter(LocalDate.of(2026, 1, 2), a).ref)
        assertEquals("XX.3", Daily.chapter(LocalDate.of(2025, 12, 31), a).ref)
        assertEquals("XII.15", Daily.chapter(LocalDate.of(2026, 10, 20), a).ref)
        assertEquals("XX.3", Daily.chapter(LocalDate.of(2027, 5, 14), a).ref)
        assertEquals("I.1", Daily.chapter(LocalDate.of(2027, 5, 15), a).ref)
    }

    @Test fun `the same date always gives the same chapter`() {
        val d = LocalDate.of(2026, 7, 4)
        assertEquals(Daily.chapter(d, a), Daily.chapter(d, a))
    }

    /** 23:59 and 00:01 in Europe/Zagreb, on the night clocks go forward, are two chapters. */
    @Test fun `two minutes apart across local midnight in Zagreb are two chapters`() {
        val zone = ZoneId.of("Europe/Zagreb")
        val at2359 = ZonedDateTime.of(2026, 3, 28, 23, 59, 0, 0, zone)
        val before = at2359.toLocalDate()
        val after = at2359.plusMinutes(2).toLocalDate()
        assertNotEquals(Daily.chapter(before, a).ref, Daily.chapter(after, a).ref)
        assertEquals(a.next(Daily.chapter(before, a).ref), Daily.chapter(after, a))
    }

    @Test fun `every chapter is reached once per cycle, in the order of the book`() {
        val seen = (0 until n).map { Daily.chapter(Daily.EPOCH.plusDays(it.toLong()), a).ref }
        assertEquals(a.chapters.map { it.ref }, seen)
    }

    @Test fun `the hard passages stay in the cycle`() {
        val cycle = (0 until n).map { Daily.chapter(Daily.EPOCH.plusDays(it.toLong()), a).ref }.toSet()
        listOf("XVII.25", "XIII.18", "VIII.9").forEach { assertTrue(it, it in cycle) }
    }
}
