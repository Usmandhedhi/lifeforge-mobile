package com.lifeforge.app.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lifeforge.app.data.AppDatabase
import com.lifeforge.app.data.DailyLogEntity
import com.lifeforge.app.data.ShieldPreferences
import com.lifeforge.app.shield.BedtimeShieldVpnService
import com.lifeforge.app.shield.ShieldOverlayService
import com.lifeforge.app.shield.ShieldScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoutineViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dao = db.routineDao()
    val preferences = ShieldPreferences(application)

    private val todayDate: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    private val _todayLog = MutableStateFlow(
        DailyLogEntity(
            date = todayDate,
            weightKg = preferences.currentWeight,
            bedTime = String.format("%02d:%02d", preferences.bedtimeHour, preferences.bedtimeMinute),
            wakeTime = String.format("%02d:%02d", preferences.wakeHour, preferences.wakeMinute)
        )
    )
    val todayLog: StateFlow<DailyLogEntity> = _todayLog.asStateFlow()

    val recentLogs: StateFlow<List<DailyLogEntity>> = dao.getRecentLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weightHistory: StateFlow<List<DailyLogEntity>> = dao.getWeightHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isShieldActiveNow = MutableStateFlow(preferences.isShieldActiveNow)
    val isShieldActiveNow: StateFlow<Boolean> = _isShieldActiveNow.asStateFlow()

    init {
        loadTodayLog()
    }

    private fun loadTodayLog() {
        viewModelScope.launch {
            val existing = dao.getLogByDate(todayDate)
            if (existing != null) {
                _todayLog.value = existing
            } else {
                val initial = DailyLogEntity(
                    date = todayDate,
                    weightKg = preferences.currentWeight,
                    bedTime = String.format("%02d:%02d", preferences.bedtimeHour, preferences.bedtimeMinute),
                    wakeTime = String.format("%02d:%02d", preferences.wakeHour, preferences.wakeMinute)
                )
                dao.insertOrUpdate(initial)
                _todayLog.value = initial
            }
        }
    }

    fun updateGym(done: Boolean, minutes: Int, workoutType: String) {
        val current = _todayLog.value
        val updated = current.copy(gymDone = done, gymMinutes = minutes, gymWorkoutType = workoutType)
        _todayLog.value = updated
        viewModelScope.launch { dao.insertOrUpdate(updated) }
    }

    fun updateLearning(minutesAdded: Int, topic: String, isVanCommute: Boolean = false) {
        val current = _todayLog.value
        val newTotalMinutes = current.learningMinutes + minutesAdded
        val updated = current.copy(
            learningDone = newTotalMinutes >= 120, // 2+ hours
            learningMinutes = newTotalMinutes,
            learningTopic = if (topic.isNotBlank()) topic else current.learningTopic,
            vanCommuteLearningDone = if (isVanCommute) true else current.vanCommuteLearningDone,
            vanCommuteMinutes = if (isVanCommute) current.vanCommuteMinutes + minutesAdded else current.vanCommuteMinutes
        )
        _todayLog.value = updated
        viewModelScope.launch { dao.insertOrUpdate(updated) }
    }

    fun updateSleep(hours: Float, bedTime: String, wakeTime: String, reelsResisted: Boolean) {
        val current = _todayLog.value
        val updated = current.copy(
            sleepHours = hours,
            sleepGoalMet = hours >= 7.0f,
            bedTime = bedTime,
            wakeTime = wakeTime,
            reelsResisted = reelsResisted
        )
        _todayLog.value = updated
        viewModelScope.launch { dao.insertOrUpdate(updated) }
    }

    fun updateWeight(weight: Float) {
        preferences.currentWeight = weight
        val current = _todayLog.value
        val updated = current.copy(weightKg = weight)
        _todayLog.value = updated
        viewModelScope.launch { dao.insertOrUpdate(updated) }
    }

    fun saveShieldSchedule(bedHour: Int, bedMinute: Int, wakeHour: Int, wakeMinute: Int) {
        preferences.bedtimeHour = bedHour
        preferences.bedtimeMinute = bedMinute
        preferences.wakeHour = wakeHour
        preferences.wakeMinute = wakeMinute
        preferences.isShieldScheduled = true
        ShieldScheduler.scheduleBedtimeAlarms(getApplication(), preferences)
    }

    fun toggleShieldManually(context: Context) {
        val newState = !_isShieldActiveNow.value
        _isShieldActiveNow.value = newState
        preferences.isShieldActiveNow = newState

        if (newState) {
            val vpnIntent = Intent(context, BedtimeShieldVpnService::class.java).apply {
                action = BedtimeShieldVpnService.ACTION_START_SHIELD
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(vpnIntent)
            } else {
                context.startService(vpnIntent)
            }
        } else {
            val stopIntent = Intent(context, BedtimeShieldVpnService::class.java).apply {
                action = BedtimeShieldVpnService.ACTION_STOP_SHIELD
            }
            context.startService(stopIntent)

            val stopOverlay = Intent(context, ShieldOverlayService::class.java).apply {
                action = ShieldOverlayService.ACTION_HIDE_OVERLAY
            }
            context.startService(stopOverlay)
        }
    }
}
