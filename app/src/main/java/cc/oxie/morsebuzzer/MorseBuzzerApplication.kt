package cc.oxie.morsebuzzer

import android.app.Application

class MorseBuzzerApplication : Application() {
    public val NOTIFICATION_LOG_SIZE = 10
    public var notificationLog = Array<NotificationEntry?>(NOTIFICATION_LOG_SIZE) { null }
    public var notificationLogIndex = 0
}