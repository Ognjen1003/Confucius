package arcadia.com.confucius.core

import java.time.LocalDate

/**
 * The chapter of the day, from the local date alone (THEORY §2): the whole Analects in order, I.1 to
 * XX.3, and round again. Never from practice history, so two readers on one calendar day read the same
 * chapter, and a missed day skips that day and nothing else. The hard passages are in the cycle.
 */
object Daily {
    /** Day 1 of the cycle. Anchors are pinned in docs/THEORY.md and in DailyTest. */
    val EPOCH: LocalDate = LocalDate.of(2026, 1, 1)

    /** 1-based position in a cycle of [size] for [date]; dates before the epoch wrap backwards. */
    fun number(date: LocalDate, size: Int): Int {
        require(size > 0)
        return Math.floorMod(date.toEpochDay() - EPOCH.toEpochDay(), size.toLong()).toInt() + 1
    }

    fun chapter(date: LocalDate, analects: Analects): Chapter =
        analects.chapters[number(date, analects.chapters.size) - 1]
}
