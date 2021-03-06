package cc.oxie.morsebuzzer

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.Vibrator
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi

class BuzzNotificationListenerService : NotificationListenerService() {
    private var vibrator: Vibrator? = null

    override fun onBind(intent: Intent?): IBinder? {
        return super.onBind(intent)
    }

    override fun onCreate() {
        super.onCreate()

        this.vibrator = (getSystemService(Context.VIBRATOR_SERVICE) as Vibrator)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (false
            || sbn.packageName.endsWith("android.apps.maps")
            || sbn.packageName.endsWith("android.dialer")
        ) {
            // com.google.android.apps.maps
            // ignore this app because it creates too many notification signals
            // com.google.android.dialer
            // ignore this because it creates notifications during phone calls
            return
        }

        val notificationEntry = NotificationEntry(sbn)
        var isDuplicate = false

        val app: MorseBuzzerApplication = applicationContext as MorseBuzzerApplication
        val size = app.NOTIFICATION_LOG_SIZE
        for (k in 0 until size) {
            if(notificationEntry.isSimilarTo(app.notificationLog[k])) {
                // duplicate encountered
                isDuplicate = true
                if (notificationEntry.isEqualTo(app.notificationLog[k])) {
                    return
                }
            }
        }

        app.notificationLog[app.notificationLogIndex] = notificationEntry
        app.notificationLogIndex = (1 + app.notificationLogIndex) % size

        if (isDuplicate) {
            return
        }

        notificationEntry.vibrate(this.vibrator!!)
    }
}