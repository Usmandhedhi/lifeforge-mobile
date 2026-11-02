package com.lifeforge.app.shield

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.lifeforge.app.data.ShieldPreferences

class ShieldAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val prefs = ShieldPreferences(context)

        when (intent?.action) {
            ShieldScheduler.ACTION_BEDTIME_START -> {
                // 1. Start the Bedtime Shield VPN
                val vpnIntent = Intent(context, BedtimeShieldVpnService::class.java).apply {
                    action = BedtimeShieldVpnService.ACTION_START_SHIELD
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(vpnIntent)
                } else {
                    context.startService(vpnIntent)
                }

                // 2. Start Overlay Guardian if permission granted
                if (Settings.canDrawOverlays(context)) {
                    val overlayIntent = Intent(context, ShieldOverlayService::class.java).apply {
                        action = ShieldOverlayService.ACTION_SHOW_OVERLAY
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(overlayIntent)
                    } else {
                        context.startService(overlayIntent)
                    }
                }

                // Reschedule for next day
                ShieldScheduler.scheduleBedtimeAlarms(context, prefs)
            }

            ShieldScheduler.ACTION_BEDTIME_STOP -> {
                // 1. Stop VPN
                val stopVpnIntent = Intent(context, BedtimeShieldVpnService::class.java).apply {
                    action = BedtimeShieldVpnService.ACTION_STOP_SHIELD
                }
                context.startService(stopVpnIntent)

                // 2. Stop Overlay
                val stopOverlayIntent = Intent(context, ShieldOverlayService::class.java).apply {
                    action = ShieldOverlayService.ACTION_HIDE_OVERLAY
                }
                context.startService(stopOverlayIntent)

                // Reschedule for next day
                ShieldScheduler.scheduleBedtimeAlarms(context, prefs)
            }
        }
    }
}
