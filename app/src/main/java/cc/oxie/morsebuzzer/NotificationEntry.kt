package cc.oxie.morsebuzzer

import android.app.Notification
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.service.notification.StatusBarNotification
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

class NotificationEntry(val origin: StatusBarNotification) : Any() {
    private val bundle = this.origin.notification.extras
    private val text = StringBuilder().append(
        bundle?.getString(Notification.EXTRA_TITLE) ?: "", " ",
        bundle?.getString(Notification.EXTRA_TEXT) ?: "", " ",
        bundle?.getString(Notification.EXTRA_SUB_TEXT) ?: "", " ",
        this.origin.packageName
    ).toString()

    private val trigram = text.toUpperCase().replace("[^A-Za-z]".toRegex(), "").slice(0 until 3)
    private val timingArray =
        stringToMorseTimingArray(trigram.split("").joinToString(" ")).map { it.toLong() * 150L }
            .toLongArray()
    private val amplitudeArray = createAmplitudeArray(timingArray.size)

    override fun toString(): String {
        return "NotificationEntry--" + text
    }

    fun vibrate(vibrator: Vibrator) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createWaveform(timingArray, amplitudeArray, -1))
        } else {
            vibrator.vibrate(1000)
        }
    }

    fun logValue(): String {
        return StringBuilder().append(
            SimpleDateFormat("dd'T'HH:mm:ss").format(Date()),
            " [", trigram, "]\n\"", text, "\"\n"
        ).toString()
    }

    fun isSimilarTo(other: NotificationEntry?): Boolean {
        Log.i("isSimilarTo", this.toString() + other?.toString() ?: "_")
        if (other === null) {
            return false
        }
        val tt = trigram == other?.trigram
        val pp = Math.abs(origin.postTime.compareTo(other.origin.postTime)) < 30_000
        Log.i("isSimilarTo", tt.toString() + "_" + pp.toString())
        return tt && pp
    }
}