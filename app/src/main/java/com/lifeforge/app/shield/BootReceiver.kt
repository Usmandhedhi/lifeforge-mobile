package com.lifeforge.app.shield

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lifeforge.app.data.ShieldPreferences

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val prefs = ShieldPreferences(context)
            if (prefs.isShieldScheduled) {
                ShieldScheduler.scheduleBedtimeAlarms(context, prefs)
            }
        }
    }
}
