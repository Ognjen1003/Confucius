package arcadia.com.confucius.platform

import com.arcadia.core.ReadingHistory
import com.arcadia.core.ReadingRecord

/**
 * Private notes, stored as `kind = "journal"` in [ReadingHistory], with an optional chapter reference in
 * [ReadingRecord.payload]. Never in analytics, never on a shared image, never backed up.
 */
object Journal {
    const val KIND = "journal"

    fun entries(): List<ReadingRecord> = ReadingHistory.byKind(KIND)

    fun save(title: String, body: String, chapterRef: String? = null, old: ReadingRecord? = null) {
        if (body.isBlank()) return
        if (old != null) ReadingHistory.update(old.copy(body = body.trim()))
        else ReadingHistory.add(KIND, title, "note", body = body.trim(), payload = chapterRef.orEmpty())
    }

    fun delete(record: ReadingRecord) = ReadingHistory.remove(record.id)
}
