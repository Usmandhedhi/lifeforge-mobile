package com.lifeforge.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifeforge.app.data.DailyLogEntity
import com.lifeforge.app.ui.RoutineViewModel
import com.lifeforge.app.ui.theme.*

@Composable
fun TodayDashboardScreen(viewModel: RoutineViewModel) {
    val log by viewModel.todayLog.collectAsState()
    val isShieldActive by viewModel.isShieldActiveNow.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showWeightDialog by remember { mutableStateOf(false) }
    var weightInput by remember { mutableStateOf(log.weightKg.toString()) }

    var showGymDialog by remember { mutableStateOf(false) }
    var gymMinutesInput by remember { mutableStateOf("35") }
    var gymWorkoutType by remember { mutableStateOf("Push / Upper Body") }

    var showLearnDialog by remember { mutableStateOf(false) }
    var learnMinutesInput by remember { mutableStateOf("45") }
    var learnTopicInput by remember { mutableStateOf("Freelance Skill & Coding") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Family Anchor Banner
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = DangerRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FAMILY FIRST • 2 DAUGHTERS' FUTURE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "“Every habit you conquer today builds safety, health, and freedom for your daughters.”",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )
            }
        }

        // 3 Pillars Completion Bar
        val pillarsCompleted = (if (log.gymDone) 1 else 0) +
                (if (log.learningDone || log.learningMinutes >= 120) 1 else 0) +
                (if (log.sleepHours >= 7.0f || log.reelsResisted) 1 else 0)

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Today's Pillar Score", fontSize = 14.sp, color = TextSecondary)
                    Text("$pillarsCompleted / 3 Pillars Achieved", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = HealthGreen)
                }
                CircularProgressIndicator(
                    progress = { pillarsCompleted / 3f },
                    color = HealthGreen,
                    trackColor = BorderDark,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        // Pillar 1: Health & Gym
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = HealthGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pillar 1: Gym Workout (30–40m)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Switch(
                        checked = log.gymDone,
                        onCheckedChange = { checked ->
                            if (checked) {
                                showGymDialog = true
                            } else {
                                viewModel.updateGym(false, 0, log.gymWorkoutType)
                            }
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = HealthGreen)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                if (log.gymDone) {
                    Text("✅ Completed: ${log.gymMinutes} mins (${log.gymWorkoutType})", color = HealthGreen, fontSize = 14.sp)
                } else {
                    Text("⏳ Target: 35 mins at gym (8:45 PM after van commute)", color = TextMuted, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(12.dp))
                // Weight Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Current Weight", fontSize = 12.sp, color = TextSecondary)
                        Text("${log.weightKg} kg (Target: ${viewModel.preferences.targetWeight} kg)", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    }
                    OutlinedButton(
                        onClick = {
                            weightInput = log.weightKg.toString()
                            showWeightDialog = true
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = HealthGreen)
                    ) {
                        Text("Log Weight")
                    }
                }
            }
        }

        // Pillar 2: Self-Learning & Freelancing
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.School, contentDescription = null, tint = LearnAmber)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pillar 2: Learning & Growth", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    Text("${log.learningMinutes} / 120 mins", fontWeight = FontWeight.Bold, color = LearnAmber)
                }

                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { (log.learningMinutes / 120f).coerceAtMost(1f) },
                    color = LearnAmber,
                    trackColor = BorderDark,
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.updateLearning(30, "Freelance Practice") },
                        colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("+30m", color = LearnAmber)
                    }
                    Button(
                        onClick = { viewModel.updateLearning(45, "Van Audio Learning", isVanCommute = true) },
                        colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Van +45m", color = LearnAmber)
                    }
                    Button(
                        onClick = { showLearnDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Custom", color = TextPrimary)
                    }
                }
            }
        }

        // Pillar 3: Sleep & Bedtime Shield
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Bedtime, contentDescription = null, tint = SleepBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pillar 3: 7–8h Sleep & Shield", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    }
                    FilledTonalButton(
                        onClick = { viewModel.toggleShieldManually(context) },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = if (isShieldActive) DangerRed else SleepBlue
                        )
                    ) {
                        Text(if (isShieldActive) "Shield ON" else "Shield OFF", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scheduled Bedtime: ${String.format("%02d:%02d", viewModel.preferences.bedtimeHour, viewModel.preferences.bedtimeMinute)} PM ➔ Wake: ${String.format("%02d:%02d", viewModel.preferences.wakeHour, viewModel.preferences.wakeMinute)} AM",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
                Text(
                    text = "🛑 Automatically cuts internet to destroy 3:00 AM reels doom-scrolling loop.",
                    fontSize = 13.sp,
                    color = SleepBlue,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Logged Sleep: ${log.sleepHours} hrs", color = TextPrimary, fontWeight = FontWeight.Medium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.updateSleep(7.5f, "23:45", "07:15", true) },
                            colors = ButtonDefaults.buttonColors(containerColor = CardDark)
                        ) {
                            Text("Log 7.5h", color = SleepBlue)
                        }
                    }
                }
            }
        }
    }

    // Weight Dialog
    if (showWeightDialog) {
        AlertDialog(
            onDismissRequest = { showWeightDialog = false },
            title = { Text("Log Today's Weight (kg)") },
            text = {
                OutlinedTextField(
                    value = weightInput,
                    onValueChange = { weightInput = it },
                    label = { Text("Weight (kg)") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    weightInput.toFloatOrNull()?.let { viewModel.updateWeight(it) }
                    showWeightDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showWeightDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Gym Dialog
    if (showGymDialog) {
        AlertDialog(
            onDismissRequest = { showGymDialog = false },
            title = { Text("Log Gym Workout") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = gymMinutesInput,
                        onValueChange = { gymMinutesInput = it },
                        label = { Text("Duration (minutes)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = gymWorkoutType,
                        onValueChange = { gymWorkoutType = it },
                        label = { Text("Workout Split (e.g., Push, Pull, Legs, Cardio)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val mins = gymMinutesInput.toIntOrNull() ?: 35
                    viewModel.updateGym(true, mins, gymWorkoutType)
                    showGymDialog = false
                }) { Text("Done") }
            },
            dismissButton = {
                TextButton(onClick = { showGymDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Learn Dialog
    if (showLearnDialog) {
        AlertDialog(
            onDismissRequest = { showLearnDialog = false },
            title = { Text("Log Learning Time") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = learnMinutesInput,
                        onValueChange = { learnMinutesInput = it },
                        label = { Text("Minutes") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = learnTopicInput,
                        onValueChange = { learnTopicInput = it },
                        label = { Text("Topic / Skill") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val mins = learnMinutesInput.toIntOrNull() ?: 45
                    viewModel.updateLearning(mins, learnTopicInput)
                    showLearnDialog = false
                }) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = { showLearnDialog = false }) { Text("Cancel") }
            }
        )
    }
}
