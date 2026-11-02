package com.lifeforge.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_logs")
data class DailyLogEntity(
    @PrimaryKey
    val date: String, // Format: YYYY-MM-DD
    
    // Pillar 1: Health & Gym
    val gymDone: Boolean = false,
    val gymMinutes: Int = 0, // Target 30-40 mins
    val gymWorkoutType: String = "Upper Body / Push", // Push, Pull, Legs, Cardio
    val weightKg: Float = 90.0f, // Starting 90 kg
    
    // Pillar 2: Self Learning & Freelancing
    val learningDone: Boolean = false,
    val learningMinutes: Int = 0, // Target 120-180 mins (2-3 hrs)
    val learningTopic: String = "",
    val vanCommuteLearningDone: Boolean = false,
    val vanCommuteMinutes: Int = 0,
    
    // Pillar 3: Sleep & Recovery (Enforced by Bedtime Shield)
    val sleepHours: Float = 0f, // Target 7.0 - 8.0 hrs
    val sleepGoalMet: Boolean = false,
    val bedTime: String = "23:45", // Target 11:45 PM
    val wakeTime: String = "07:15", // Target 07:15 AM
    val reelsResisted: Boolean = true,
    
    // Day Reflections
    val energyScore: Int = 5, // 1 to 5 scale
    val notes: String = ""
)
