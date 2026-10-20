package arcadia.com.confucius.platform

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import arcadia.com.confucius.MainActivity
import arcadia.com.confucius.R
import arcadia.com.confucius.core.Chapter
import arcadia.com.confucius.core.Daily
import com.arcadia.core.ArcadiaNotifier
import com.arcadia.core.NotificationSchedule
import com.arcadia.core.Practice

/**
 * Off by default; inexact; re-armed after boot, a package update and a clock or zone change; skipped on
 * a day already practised. No exact alarm and no foreground service (PRAKSA §3).
 */
object Reminders {
    const val CHANNEL = "confucius_daily"
    const val ACTION = "arcadia.com.confucius.REMINDER"
    private const val REQUEST = 4201
    const val NOTIFICATION_ID = REQUEST + 1

    private fun prefs(c: Context) = c.getSharedPreferences("confucius_settings", Context.MODE_PRIVATE)
    fun enabled(c: Context) = prefs(c).getBoolean("reminder", false)
    fun minute(c: Context) = prefs(c).getInt("minute", 8 * 60).coerceIn(0, 1439)

    fun set(c: Context, on: Boolean, minute: Int = minute(c)) {
        prefs(c).edit().putBoolean("reminder", on).putInt("minute", minute.coerceIn(0, 1439)).apply()
        rearm(c)
    }

    private fun intent(c: Context) = Intent(c, ReminderReceiver::class.java).setAction(ACTION)

    fun rearm(c: Context) {
        if (!enabled(c)) { NotificationSchedule.cancel(c, REQUEST, intent(c)); return }
        val m = minute(c)
        NotificationSchedule.scheduleInexact(c, REQUEST, NotificationSchedule.nextLocalTime(m / 60, m % 60), intent(c))
    }

    fun text(chapter: Chapter): Pair<String, String> =
        "Today: ${chapter.citation}" to "${chapter.title}. ${firstSentence(chapter)}"

    fun post(c: Context) {
        val today = ConfuciusData.today()
        if (!enabled(c) || Practice.state(ConfuciusData.PRACTICE_ID).lastPracticedEpochDay == today.toEpochDay()) return
        show(c, Daily.chapter(today, ConfuciusData.analects(c)))
    }

    /** Posts the reminder for [chapter] without asking whether it is due; [post] decides that. */
    fun show(c: Context, chapter: Chapter) {
        val (title, line) = text(chapter)
        ArcadiaNotifier.post(c, CHANNEL, NOTIFICATION_ID, title, line, R.drawable.ic_notification,
            PendingIntent.getActivity(c, REQUEST, Intent(c, MainActivity::class.java).putExtra(MainActivity.EXTRA_TODAY, true)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
    }

    /** The chapter's first sentence without Legge's section number, at most 160 characters. */
    fun firstSentence(chapter: Chapter): String {
        val text = chapter.paragraphs.first().replace(Regex("^\\d+\\.\\s+"), "")
        val end = Regex("[.?!][\"”']?(\\s|$)").find(text)?.range?.first ?: return text.take(160)
        val sentence = text.substring(0, end + 1).let { s -> if (text.getOrNull(end + 1) == '\'') "$s'" else s }
        return if (sentence.length <= 160) sentence else sentence.take(157).substringBeforeLast(' ') + "…"
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Reminders.ACTION) runCatching { Reminders.post(context) }
            .onFailure { Log.w("ConfuciusReminder", "reminder not posted", it) }
        Reminders.rearm(context)
    }
}
