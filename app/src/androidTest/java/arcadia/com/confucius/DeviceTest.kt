package arcadia.com.confucius

import android.Manifest
import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.compose.ui.test.junit4.createEmptyComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import arcadia.com.confucius.core.Daily
import arcadia.com.confucius.platform.ConfuciusData
import arcadia.com.confucius.platform.Reminders
import arcadia.com.confucius.widget.ConfuciusWidget
import com.arcadia.core.ArcadiaNotifier
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** What only a device can show: the bundled resources, the widget's text, the reminder and a link. */
@RunWith(AndroidJUnit4::class)
class DeviceTest {
    @get:Rule val ui = createEmptyComposeRule()
    private val context: Context get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test fun theBundledContentLoadsFromResources() {
        ConfuciusData.preload(context)
        assertTrue(ConfuciusData.loaded())
        assertEquals(499, ConfuciusData.analects(context).chapters.size)
        assertEquals(499, ConfuciusData.commentary(context).size)
        assertEquals(12, ConfuciusData.course(context).units.size)
    }

    /** A freshly installed widget must already say something true: today's chapter. */
    @Test fun theWidgetShowsTodaysChapterWithoutAnyInput() {
        val c = ConfuciusWidget().content(context, 1)
        val today = Daily.chapter(ConfuciusData.today(), ConfuciusData.analects(context))
        assertEquals("ANALECTS · TODAY", c.label)
        assertEquals("${today.ref} · ${today.title}", c.title)
        assertEquals("Tr. James Legge", c.subtitle)
        assertTrue(c.body.isNotBlank() && c.body.length <= 160)
        assertFalse(c.body.startsWith("1. "))
    }

    @Test fun theReminderPostsTheDaysChapter() {
        if (Build.VERSION.SDK_INT >= 33) {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission(context.packageName, Manifest.permission.POST_NOTIFICATIONS)
        }
        ArcadiaNotifier.ensureChannel(context, Reminders.CHANNEL, "Daily chapter", "The day's chapter of the Analects at your chosen hour.")
        val chapter = ConfuciusData.analects(context).byRef("I.4")!!
        Reminders.show(context, chapter)
        val manager = context.getSystemService(NotificationManager::class.java)
        var posted = manager.activeNotifications.firstOrNull { it.id == Reminders.NOTIFICATION_ID }
        repeat(20) { if (posted == null) { Thread.sleep(250); posted = manager.activeNotifications.firstOrNull { it.id == Reminders.NOTIFICATION_ID } } }
        assertNotNull(posted)
        val extras = posted!!.notification.extras
        assertEquals("Today: Analects I.4", extras.getCharSequence(Notification.EXTRA_TITLE).toString())
        assertTrue(extras.getCharSequence(Notification.EXTRA_TEXT).toString().startsWith("Three questions a day. The philosopher Tsang said"))
        manager.cancel(Reminders.NOTIFICATION_ID)
    }

    @Test fun aSuiteLinkOpensItsChapter() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("arcadia://confucius?chapter=II.4"))
            .setPackage(context.packageName).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ActivityScenario.launch<MainActivity>(intent).use {
            ui.waitUntil(20_000) { ui.onAllNodesWithText("From fifteen to seventy").fetchSemanticsNodes().isNotEmpty() }
            ui.waitUntil(5_000) { ui.onAllNodesWithText("ANALECTS II.4").fetchSemanticsNodes().isNotEmpty() }
        }
    }
}
