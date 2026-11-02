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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifeforge.app.ui.RoutineViewModel
import com.lifeforge.app.ui.theme.*

@Composable
fun ProgressScreen(viewModel: RoutineViewModel) {
    val recentLogs by viewModel.recentLogs.collectAsState()
    val scrollState = rememberScrollState()
    val prefs = viewModel.preferences

    val totalGym = recentLogs.count { it.gymDone }
    val totalGoodSleep = recentLogs.count { it.sleepHours >= 7.0f }
    val totalLearnMinutes = recentLogs.sumOf { it.learningMinutes }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Stats Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Gym Sessions",
                value = "$totalGym days",
                color = HealthGreen,
                icon = Icons.Default.FitnessCenter,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "7+ Hr Sleep",
                value = "$totalGoodSleep days",
                color = SleepBlue,
                icon = Icons.Default.Bedtime,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Learning",
                value = "${totalLearnMinutes / 60} hrs",
                color = LearnAmber,
                icon = Icons.Default.School,
                modifier = Modifier.weight(1f)
            )
        }

        // Weight Transformation Target
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Weight Goal (Age 31)", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("Starting 90.0 kg ➔ Goal 78.0 kg", fontSize = 13.sp, color = TextSecondary)
                    }
                    Text(
                        text = "${prefs.currentWeight} kg",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = HealthGreen
                    )
                }

                val weightLost = (90.0f - prefs.currentWeight).coerceAtLeast(0f)
                val targetToLose = (90.0f - prefs.targetWeight).coerceAtLeast(1f)
                val progress = (weightLost / targetToLose).coerceIn(0f, 1f)

                LinearProgressIndicator(
                    progress = { progress },
                    color = HealthGreen,
                    trackColor = BorderDark,
                    modifier = Modifier.fillMaxWidth().height(8.dp)
                )

                Text(
                    text = String.format("%.1f kg reduced so far. Keep going, brother!", weightLost),
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            }
        }

        // Recent Daily History
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Recent Daily Pillar Logs", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)

                if (recentLogs.isEmpty()) {
                    Text("No past logs yet. Start today by conquering the 3 pillars!", fontSize = 13.sp, color = TextMuted)
                } else {
                    recentLogs.take(7).forEach { log ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(log.date, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text("Sleep: ${log.sleepHours}h • Gym: ${log.gymMinutes}m • Study: ${log.learningMinutes}m", fontSize = 12.sp, color = TextSecondary)
                            }
                            Text(
                                text = if (log.gymDone && log.sleepHours >= 7.0f) "🔥 All Pillars" else "In Progress",
                                fontSize = 12.sp,
                                color = if (log.gymDone && log.sleepHours >= 7.0f) HealthGreen else LearnAmber
                            )
                        }
                        HorizontalDivider(color = BorderDark, thickness = 0.5.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        modifier = modifier.border(1.dp, BorderDark, RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(title, fontSize = 11.sp, color = TextSecondary)
        }
    }
}
