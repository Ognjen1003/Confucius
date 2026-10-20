package arcadia.com.confucius

import org.junit.Assert.*
import org.junit.Test

/** CONTENT.md §2 and §5: one original comment per chapter, never a template with a word changed. */
class CommentaryTest {
    private val comments = Raw.commentary

    @Test fun `every chapter has a reflection and a question`() {
        assertEquals(Raw.analects.chapters.map { it.ref }.toSet(), comments.keys)
        comments.values.forEach { c ->
            assertTrue(c.ref, c.reflection.length in 80..1100)
            assertTrue(c.ref, c.question.trim().endsWith("?"))
        }
    }

    @Test fun `no two chapters share a reflection, a question or a title`() {
        assertEquals(comments.size, comments.values.map { it.reflection }.toSet().size)
        assertEquals(comments.size, comments.values.map { it.question }.toSet().size)
        assertEquals(Raw.analects.chapters.size, Raw.analects.chapters.map { it.title }.toSet().size)
    }

    @Test fun `what is never written`() {
        val banned = listOf("Confucius wrote", "you are a junzi", "ancient Chinese secret", "—")
        comments.values.forEach { c ->
            val all = "${c.reflection} ${c.question} ${c.exercise.orEmpty()}"
            banned.forEach { assertFalse("${c.ref}: $it", all.contains(it, ignoreCase = true)) }
        }
    }

    @Test fun `exercises are offered, not on every chapter`() {
        val n = comments.values.count { it.exercise != null }
        assertTrue("$n exercises", n in 20..150)
    }
}
