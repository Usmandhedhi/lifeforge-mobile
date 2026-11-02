package com.lifeforge.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RoutineDao {

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    fun getLogByDateFlow(date: String): Flow<DailyLogEntity?>

    @Query("SELECT * FROM daily_logs WHERE date = :date LIMIT 1")
    suspend fun getLogByDate(date: String): DailyLogEntity?

    @Query("SELECT * FROM daily_logs ORDER BY date DESC LIMIT 30")
    fun getRecentLogs(): Flow<List<DailyLogEntity>>

    @Query("SELECT * FROM daily_logs WHERE weightKg > 0 ORDER BY date ASC")
    fun getWeightHistory(): Flow<List<DailyLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(log: DailyLogEntity)

    @Update
    suspend fun update(log: DailyLogEntity)

    @Query("SELECT COUNT(*) FROM daily_logs WHERE gymDone = 1")
    suspend fun getTotalGymSessions(): Int

    @Query("SELECT COUNT(*) FROM daily_logs WHERE sleepHours >= 7.0")
    suspend fun getTotalGoodSleepDays(): Int

    @Query("SELECT SUM(learningMinutes) FROM daily_logs")
    suspend fun getTotalLearningMinutes(): Int?
}
