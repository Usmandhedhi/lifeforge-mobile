package com.lifeforge.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.lifeforge.app.data.AppDatabase
import com.lifeforge.app.data.ShieldPreferences
import com.lifeforge.app.shield.ShieldScheduler

class LifeForgeApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferences: ShieldPreferences
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
        preferences = ShieldPreferences(this)

        createNotificationChannels()

        // Ensure bedtime alarms are scheduled
        if (preferences.isShieldScheduled) {
            ShieldScheduler.scheduleBedtimeAlarms(this, preferences)
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                SHIELD_CHANNEL_ID,
                getString(R.string.shield_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = getString(R.string.shield_channel_desc)
                enableVibration(true)
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val SHIELD_CHANNEL_ID = "lifeforge_bedtime_shield_channel"
        lateinit var instance: LifeForgeApp
            private set
    }
}
