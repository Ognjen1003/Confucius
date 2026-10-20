package arcadia.com.confucius.widget

import android.content.Context
import arcadia.com.confucius.core.Daily
import arcadia.com.confucius.platform.ConfuciusData
import arcadia.com.confucius.platform.Reminders
import com.arcadia.core.ArcadiaWidgetProvider
import com.arcadia.core.WidgetContent

/** The chapter of the day, from the date alone: a fresh install shows the real chapter at once. */
class ConfuciusWidget : ArcadiaWidgetProvider() {
    override val accentColor: Int = 0xFFE25A41.toInt()
    override fun content(context: Context, widgetId: Int): WidgetContent {
        val c = Daily.chapter(ConfuciusData.today(), ConfuciusData.analects(context))
        return WidgetContent(
            label = "ANALECTS · TODAY",
            title = "${c.ref} · ${c.title}",
            subtitle = "Tr. James Legge",
            body = Reminders.firstSentence(c),
            glyphText = "仁",
        )
    }
}
