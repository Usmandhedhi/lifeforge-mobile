package com.lifeforge.app.shield

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.app.NotificationCompat
import com.lifeforge.app.LifeForgeApp
import com.lifeforge.app.R
import com.lifeforge.app.data.ShieldPreferences
import com.lifeforge.app.ui.MainActivity

class BedtimeShieldVpnService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null
    private lateinit var preferences: ShieldPreferences

    override fun onCreate() {
        super.onCreate()
        preferences = ShieldPreferences(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_SHIELD) {
            stopVpn()
            stopSelf()
            return START_NOT_STICKY
        }

        startVpn()
        return START_STICKY
    }

    private fun startVpn() {
        try {
            // Build Foreground Notification
            val notification = createShieldNotification()
            startForeground(NOTIFICATION_ID, notification)

            // Local Loopback Dummy Interface - Blackhole all outgoing traffic
            val builder = Builder()
                .setSession("LifeForge Bedtime Shield")
                .addAddress("10.120.0.1", 32)
                .addRoute("0.0.0.0", 0) // Capture all IPv4 traffic
                .setBlocking(true)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                builder.setMetered(false)
            }

            vpnInterface = builder.establish()
            preferences.isShieldActiveNow = true
            Log.i("BedtimeShield", "Bedtime Shield activated. Internet traffic blackholed.")
        } catch (e: Exception) {
            Log.e("BedtimeShield", "Failed to establish Bedtime Shield VPN", e)
            stopSelf()
        }
    }

    private fun stopVpn() {
        try {
            vpnInterface?.close()
            vpnInterface = null
            preferences.isShieldActiveNow = false
            Log.i("BedtimeShield", "Bedtime Shield deactivated. Internet restored.")
        } catch (e: Exception) {
            Log.e("BedtimeShield", "Error stopping Bedtime Shield", e)
        }
    }

    private fun createShieldNotification(): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, LifeForgeApp.SHIELD_CHANNEL_ID)
            .setContentTitle("Bedtime Shield ACTIVE")
            .setContentText("Internet blocked to protect your 7-8h sleep. Sleep well for your family!")
            .setSmallIcon(android.R.drawable.ic_lock_idle_lock)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(pendingIntent)
            .build()
    }

    override fun onDestroy() {
        stopVpn()
        super.onDestroy()
    }

    companion object {
        const val ACTION_START_SHIELD = "com.lifeforge.app.ACTION_START_SHIELD"
        const val ACTION_STOP_SHIELD = "com.lifeforge.app.ACTION_STOP_SHIELD"
        private const val NOTIFICATION_ID = 9001
    }
}
