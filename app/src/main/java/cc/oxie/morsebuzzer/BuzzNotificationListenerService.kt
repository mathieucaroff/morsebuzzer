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
        Log.i("BuzzNotificationListenrService", "notificationPosted")
        val notificationEntry = NotificationEntry(sbn)

        val app: MorseBuzzerApplication = applicationContext as MorseBuzzerApplication
        app.notificationLog[app.notificationLogIndex] = notificationEntry
        app.notificationLogIndex += 1
        app.notificationLogIndex %= app.NOTIFICATION_LOG_SIZE

        val size = app.NOTIFICATION_LOG_SIZE
        if (!notificationEntry.isSimilarTo(app.notificationLog[(app.notificationLogIndex + size - 2) % size])) {
            notificationEntry.vibrate(this.vibrator!!)
        }
    }
}