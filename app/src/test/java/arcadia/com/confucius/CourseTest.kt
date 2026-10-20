package arcadia.com.confucius

import arcadia.com.confucius.core.Course
import arcadia.com.confucius.core.StepKind
import com.arcadia.core.PracticeRules
import com.arcadia.core.PracticeState
import org.junit.Assert.*
import org.junit.Test

class CourseTest {
    private val course = Raw.course

    @Test fun `twelve words of seven steps in the order of THEORY md`() {
        assertEquals(Course.ID, course.id)
        assertEquals(listOf("ren", "li", "yi", "zhi", "xin", "xiao", "junzi", "xue", "shu", "zhong", "de", "zhengming"), course.units.map { it.id })
        course.units.forEach { u -> assertEquals(u.id, StepKind.entries, u.steps.map { it.kind }) }
        assertEquals(84, course.practice.totalSteps)
    }

    @Test fun `every unit is one of the twelve terms and every cited chapter exists`() {
        val terms = Raw.terms.map { it.id }.toSet()
        course.units.forEach { u ->
            assertTrue(u.id, u.id in terms)
            u.steps.filter { it.kind == StepKind.CHAPTER || it.kind == StepKind.CHAPTER_2 }.forEach { s ->
                assertNotNull(s.id, s.chapter?.let(Raw.analects::byRef))
            }
            u.steps.forEach { s ->
                s.chapter?.let { assertNotNull(s.id, Raw.analects.byRef(it)) }
                assertTrue(s.id, s.text.length > 40 && !s.text.contains('—'))
            }
        }
    }

    @Test fun `the chapter step of a word is a chapter where the word stands`() {
        course.units.forEach { u ->
            val t = Raw.terms.first { it.id == u.id }
            val main = u.steps.first { it.kind == StepKind.CHAPTER }.chapter
            assertTrue(u.id, main in t.chapters)
        }
    }

    @Test fun `no step grades the reader`() {
        val banned = listOf("you are a junzi", "you failed", "well done", "streak", "%")
        course.units.flatMap { it.steps }.forEach { s ->
            banned.forEach { assertFalse("${s.id}: $it", s.text.contains(it, ignoreCase = true)) }
        }
    }

    @Test fun `walking the course to the end marks it finished and stays there`() {
        var s = PracticeState(course.id)
        repeat(83) { s = PracticeRules.advance(s, course.practice) }
        assertFalse(s.finished)
        assertEquals(11, s.unitIndex)
        assertEquals(6, s.stepIndex)
        s = PracticeRules.advance(s, course.practice)
        assertTrue(s.finished)
        assertTrue(PracticeRules.advance(s, course.practice).finished)
    }
}
