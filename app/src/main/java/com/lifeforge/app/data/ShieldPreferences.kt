package com.lifeforge.app.data

import android.content.Context
import android.content.SharedPreferences

class ShieldPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("lifeforge_shield_prefs", Context.MODE_PRIVATE)

    var bedtimeHour: Int
        get() = prefs.getInt(KEY_BEDTIME_HOUR, 23) // 11 PM
        set(value) = prefs.edit().putInt(KEY_BEDTIME_HOUR, value).apply()

    var bedtimeMinute: Int
        get() = prefs.getInt(KEY_BEDTIME_MINUTE, 45) // 11:45 PM
        set(value) = prefs.edit().putInt(KEY_BEDTIME_MINUTE, value).apply()

    var wakeHour: Int
        get() = prefs.getInt(KEY_WAKE_HOUR, 7) // 7 AM
        set(value) = prefs.edit().putInt(KEY_WAKE_HOUR, value).apply()

    var wakeMinute: Int
        get() = prefs.getInt(KEY_WAKE_MINUTE, 15) // 7:15 AM
        set(value) = prefs.edit().putInt(KEY_WAKE_MINUTE, value).apply()

    var isShieldScheduled: Boolean
        get() = prefs.getBoolean(KEY_IS_SHIELD_SCHEDULED, true)
        set(value) = prefs.edit().putBoolean(KEY_IS_SHIELD_SCHEDULED, value).apply()

    var isShieldActiveNow: Boolean
        get() = prefs.getBoolean(KEY_IS_SHIELD_ACTIVE_NOW, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_SHIELD_ACTIVE_NOW, value).apply()

    var currentWeight: Float
        get() = prefs.getFloat(KEY_CURRENT_WEIGHT, 90.0f)
        set(value) = prefs.edit().putFloat(KEY_CURRENT_WEIGHT, value).apply()

    var targetWeight: Float
        get() = prefs.getFloat(KEY_TARGET_WEIGHT, 78.0f)
        set(value) = prefs.edit().putFloat(KEY_TARGET_WEIGHT, value).apply()

    var frictionSeconds: Int
        get() = prefs.getInt(KEY_FRICTION_SECONDS, 60)
        set(value) = prefs.edit().putInt(KEY_FRICTION_SECONDS, value).apply()

    var familyPledge: String
        get() = prefs.getString(
            KEY_FAMILY_PLEDGE,
            "Sleep now, Father. Build our family's future tomorrow!"
        ) ?: "Sleep now, Father. Build our family's future tomorrow!"
        set(value) = prefs.edit().putString(KEY_FAMILY_PLEDGE, value).apply()

    companion object {
        private const val KEY_BEDTIME_HOUR = "bedtime_hour"
        private const val KEY_BEDTIME_MINUTE = "bedtime_minute"
        private const val KEY_WAKE_HOUR = "wake_hour"
        private const val KEY_WAKE_MINUTE = "wake_minute"
        private const val KEY_IS_SHIELD_SCHEDULED = "is_shield_scheduled"
        private const val KEY_IS_SHIELD_ACTIVE_NOW = "is_shield_active_now"
        private const val KEY_CURRENT_WEIGHT = "current_weight"
        private const val KEY_TARGET_WEIGHT = "target_weight"
        private const val KEY_FRICTION_SECONDS = "friction_seconds"
        private const val KEY_FAMILY_PLEDGE = "family_pledge"
    }
}
