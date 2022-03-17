package cc.oxie.morsebuzzer

import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.text.TextUtils
import android.view.KeyEvent
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
    private val ACTION_NOTIFICATION_LISTENER_SETTINGS =
        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

    private var vibrator: Vibrator? = null
    private var vibrationStartTime: Long = 0

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // If the user did not turn the notification listener service on we prompt him to do so
        if (!isNotificationServiceEnabled()) {
            buildNotificationServiceAlertDialog().show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                this.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            this.vibrator = vibratorManager.defaultVibrator
        } else {
            this.vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    override fun onResume() {
        super.onResume()
        updateText()
    }

    private fun updateText() {
        val app: MorseBuzzerApplication = applicationContext as MorseBuzzerApplication
        val sb = StringBuilder(app.notificationLog.size * 2)
        sb.append("Log:\n")

        val s = app.NOTIFICATION_LOG_SIZE
        for (k in s-1 downTo 0) {
            val index = (k + app.notificationLogIndex) % s
            val entry = app.notificationLog[index]?.logValue() ?: ""
            if (entry != "") {
                sb.append(entry, "\n")
            }
        }
        val text = sb.toString()

        val textView = findViewById<TextView>(R.id.theMorseBuzzerTextView)
        textView.text = text
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return handleKey(keyCode, event, false)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return handleKey(keyCode, event, true)
    }

    private fun handleKey(keyCode: Int, event: KeyEvent?, release: Boolean): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            if (release) { // key up
                val elapsed = System.currentTimeMillis() - vibrationStartTime
                val target = 150 - elapsed
                if (target > 0) {
                    vibrator?.vibrate(target)
                } else {
                    vibrator?.cancel()
                }
            } else { // key down
                if (event?.repeatCount == 0) {
                    vibrator?.vibrate(2000)
                    vibrationStartTime = System.currentTimeMillis()
                }
            }
            return true
        }
        return false
    }

    /**
     * See https://github.com/Chagall/notification-listener-service-example/tree/master/app/src/main/java/com/github/chagall/notificationlistenerexample
     */

    /**
     * Is Notification Service Enabled.
     * Verifies if the notification listener service is enabled.
     * Got it from: https://github.com/kpbird/NotificationListenerService-Example/blob/master/NLSExample/src/main/java/com/kpbird/nlsexample/NLService.java
     * @return True if enabled, false otherwise.
     */
    private fun isNotificationServiceEnabled(): Boolean {
        val pkgName = packageName
        val flat: String = Settings.Secure.getString(
            contentResolver,
            ENABLED_NOTIFICATION_LISTENERS
        )
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":").toTypedArray()
            for (i in names.indices) {
                val cn = ComponentName.unflattenFromString(names[i])
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * Build Notification Listener Alert Dialog.
     * Builds the alert dialog that pops up if the user has not turned
     * the Notification Listener Service on yet.
     * @return An alert dialog which leads to the notification enabling screen
     */
    private fun buildNotificationServiceAlertDialog(): AlertDialog {
        val alertDialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle(R.string.notification_listener_service)
        alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation)
        alertDialogBuilder.setPositiveButton(R.string.yes,
            DialogInterface.OnClickListener { dialog, id ->
                startActivity(
                    Intent(
                        ACTION_NOTIFICATION_LISTENER_SETTINGS
                    )
                )
            })
        alertDialogBuilder.setNegativeButton(R.string.no,
            DialogInterface.OnClickListener { dialog, id ->
                // If you choose to not enable the notification listener
                // the app. will not work as expected
            })
        return alertDialogBuilder.create()
    }
}