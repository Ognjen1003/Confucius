package arcadia.com.confucius

import android.app.Application
import arcadia.com.confucius.platform.Prefs
import arcadia.com.confucius.platform.Reminders
import com.arcadia.core.AdManager
import com.arcadia.core.AnalyticsManager
import com.arcadia.core.ArcadiaNotifier
import com.arcadia.core.BillingHolder
import com.arcadia.core.Practice
import com.arcadia.core.PremiumManager
import com.arcadia.core.ReadingHistory

class ConfuciusApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        PremiumManager.init(this, "confucius_premium_prefs")
        AdManager.init(this)
        AnalyticsManager.init(this)
        BillingHolder.start(this)
        Practice.init(this, "confucius_practice")
        ReadingHistory.init(this, "confucius_journal", 2000)
        Prefs.load(this)
        ArcadiaNotifier.ensureChannel(this, Reminders.CHANNEL, "Daily chapter", "The day's chapter of the Analects at your chosen hour.")
        Reminders.rearm(this)
    }
}
