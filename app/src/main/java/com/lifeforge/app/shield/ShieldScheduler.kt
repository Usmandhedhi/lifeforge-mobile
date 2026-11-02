package com.lifeforge.app.shield

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.lifeforge.app.data.ShieldPreferences
import java.util.Calendar

object ShieldScheduler {

    const val ACTION_BEDTIME_START = "com.lifeforge.app.ACTION_BEDTIME_START"
    const val ACTION_BEDTIME_STOP = "com.lifeforge.app.ACTION_BEDTIME_STOP"

    private const val START_REQ_CODE = 1001
    private const val STOP_REQ_CODE = 1002

    fun scheduleBedtimeAlarms(context: Context, prefs: ShieldPreferences) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // 1. Bedtime Start Alarm (e.g. 11:45 PM)
        val startCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, prefs.bedtimeHour)
            set(Calendar.MINUTE, prefs.bedtimeMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val startIntent = Intent(context, ShieldAlarmReceiver::class.java).apply {
            action = ACTION_BEDTIME_START
        }
        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            START_REQ_CODE,
            startIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Wakeup Stop Alarm (e.g. 07:15 AM)
        val stopCal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, prefs.wakeHour)
            set(Calendar.MINUTE, prefs.wakeMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val stopIntent = Intent(context, ShieldAlarmReceiver::class.java).apply {
            action = ACTION_BEDTIME_STOP
        }
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            STOP_REQ_CODE,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    startCal.timeInMillis,
                    startPendingIntent
                )
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    stopCal.timeInMillis,
                    stopPendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    startCal.timeInMillis,
                    startPendingIntent
                )
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    stopCal.timeInMillis,
                    stopPendingIntent
                )
            }
            Log.i("ShieldScheduler", "Bedtime alarms scheduled: Start at ${startCal.time}, Stop at ${stopCal.time}")
        } catch (e: SecurityException) {
            Log.e("ShieldScheduler", "Permission needed for exact alarms", e)
        }
    }

    fun cancelAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val startIntent = Intent(context, ShieldAlarmReceiver::class.java).apply {
            action = ACTION_BEDTIME_START
        }
        val stopIntent = Intent(context, ShieldAlarmReceiver::class.java).apply {
            action = ACTION_BEDTIME_STOP
        }
        val p1 = PendingIntent.getBroadcast(context, START_REQ_CODE, startIntent, PendingIntent.FLAG_IMMUTABLE)
        val p2 = PendingIntent.getBroadcast(context, STOP_REQ_CODE, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(p1)
        alarmManager.cancel(p2)
    }
}
