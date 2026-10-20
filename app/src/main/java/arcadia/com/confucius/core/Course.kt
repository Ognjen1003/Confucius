package arcadia.com.confucius.core

import com.arcadia.core.PracticeCourse
import org.json.JSONObject

/**
 * "Twelve words" (THEORY §4): twelve units, one per word, of seven steps whose kinds always come in the
 * same order. A step may carry a chapter, which is shown as TEXT beside our words. No step grades the
 * reader, and none tells them how to treat their own family (CLAUDE.md, zamka 4).
 */
enum class StepKind(val label: String) {
    TERM("The word"),
    CHAPTER("A chapter"),
    CHAPTER_2("Another chapter"),
    QUESTION("A question"),
    EXERCISE("In an ordinary day"),
    TENSION("Where it meets another word"),
    TRANSLATION("What the translation hides"),
}

data class CourseStep(val id: String, val kind: StepKind, val title: String, val text: String, val chapter: String?)

data class CourseUnit(val id: String, val title: String, val summary: String, val steps: List<CourseStep>)

class Course(val id: String, val title: String, val units: List<CourseUnit>) {
    init {
        require(units.isNotEmpty() && units.all { it.steps.isNotEmpty() })
        require(units.flatMap { u -> u.steps.map { it.id } }.let { it.toSet().size == it.size }) { "duplicate step id" }
    }

    val practice: PracticeCourse = PracticeCourse(id, units.map { u -> PracticeCourse.Unit(u.id, u.steps.map { it.id }) })

    companion object {
        const val ID = "twelve_words"
        val KINDS: List<StepKind> = StepKind.entries

        fun parse(json: String): Course {
            val root = JSONObject(json)
            return Course(root.getString("id"), root.getString("title"), root.getJSONArray("units").objects().map { u ->
                CourseUnit(u.getString("id"), u.getString("title"), u.getString("summary"),
                    u.getJSONArray("steps").objects().map { s ->
                        CourseStep(s.getString("id"), StepKind.valueOf(s.getString("kind")), s.getString("title"),
                            s.getString("text"), s.optString("chapter").takeIf { it.isNotBlank() })
                    })
            })
        }
    }
}
