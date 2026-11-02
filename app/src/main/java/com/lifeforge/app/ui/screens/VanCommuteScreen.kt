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
fun VanCommuteScreen(viewModel: RoutineViewModel) {
    val scrollState = rememberScrollState()
    val log by viewModel.todayLog.collectAsState()

    var isVanTimerRunning by remember { mutableStateOf(false) }
    var vanTimerSeconds by remember { mutableIntStateOf(0) }

    LaunchedEffect(isVanTimerRunning) {
        while (isVanTimerRunning) {
            kotlinx.coroutines.delay(1000L)
            vanTimerSeconds++
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Commute Superpower Header
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = LearnAmber)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "COMMUTE INTO PRODUCTIVITY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = LearnAmber
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Convert your office van travel (reaching home at 8:30 PM) into 45 mins of dedicated skill learning & gym mental preparation.",
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
        }

        // Active Van Session Timer
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardDark),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Van Learning Session", fontSize = 14.sp, color = TextSecondary)
                Spacer(modifier = Modifier.height(8.dp))
                val minutes = vanTimerSeconds / 60
                val seconds = vanTimerSeconds % 60
                Text(
                    text = String.format("%02d:%02d", minutes, seconds),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = LearnAmber
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = { isVanTimerRunning = !isVanTimerRunning },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isVanTimerRunning) DangerRed else HealthGreen
                        )
                    ) {
                        Icon(
                            if (isVanTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isVanTimerRunning) "Pause" else "Start Commute Learning")
                    }

                    if (vanTimerSeconds > 0) {
                        OutlinedButton(
                            onClick = {
                                val mins = (vanTimerSeconds / 60).coerceAtLeast(1)
                                viewModel.updateLearning(mins, "Van Commute Study", isVanCommute = true)
                                isVanTimerRunning = false
                                vanTimerSeconds = 0
                            }
                        ) {
                            Text("Log ${vanTimerSeconds / 60}m")
                        }
                    }
                }
            }
        }

        // Pre-Gym Van Checklist
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("8:00 PM - 8:30 PM: Gym Transition Checklist", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Prepare your body & mind while on the van so fatigue doesn't stop you:", fontSize = 13.sp, color = TextSecondary)

                ChecklistRow(item = "Drink 500ml water on the van")
                ChecklistRow(item = "Pre-visualize the 35m workout routine (Push / Pull / Legs)")
                ChecklistRow(item = "Do not check work emails after stepping into the van")
                ChecklistRow(item = "Put gym clothes ready as soon as you step home at 8:30 PM")
            }
        }
    }
}

@Composable
private fun ChecklistRow(item: String) {
    var checked by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { checked = it },
            colors = CheckboxDefaults.colors(checkedColor = HealthGreen)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(item, fontSize = 14.sp, color = if (checked) TextMuted else TextPrimary)
    }
}
