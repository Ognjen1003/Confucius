package arcadia.com.confucius.platform

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * Reading size (may travel in a backup) and bookmarks (excluded from backup with the journal: what a
 * reader marks is as private as what they write).
 */
object Prefs {
    private lateinit var settings: SharedPreferences
    private lateinit var marks: SharedPreferences

    var textSize by mutableFloatStateOf(1f); private set
    var bookmarks by mutableStateOf(emptySet<String>()); private set

    fun load(context: Context) {
        settings = context.getSharedPreferences("confucius_settings", Context.MODE_PRIVATE)
        marks = context.getSharedPreferences("confucius_bookmarks", Context.MODE_PRIVATE)
        textSize = settings.getFloat("text_size", 1f).takeIf { it.isFinite() }?.coerceIn(.85f, 1.5f) ?: 1f
        bookmarks = marks.getStringSet("marks", emptySet()).orEmpty().toSet()
    }

    fun readingSize(value: Float) {
        textSize = value.coerceIn(.85f, 1.5f)
        settings.edit().putFloat("text_size", textSize).apply()
    }

    /** A chapter is marked by its reference, "II.4". */
    fun marked(ref: String) = ref in bookmarks

    fun toggle(ref: String) {
        bookmarks = if (ref in bookmarks) bookmarks - ref else bookmarks + ref
        marks.edit().putStringSet("marks", bookmarks).apply()
    }
}
