package com.lifeforge.app.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.VpnService
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lifeforge.app.ui.RoutineViewModel
import com.lifeforge.app.ui.theme.*

@Composable
fun BedtimeShieldScreen(viewModel: RoutineViewModel) {
    val context = LocalContext.current
    val isShieldActive by viewModel.isShieldActiveNow.collectAsState()
    val prefs = viewModel.preferences
    val scrollState = rememberScrollState()

    var bedtimeHour by remember { mutableIntStateOf(prefs.bedtimeHour) }
    var bedtimeMinute by remember { mutableIntStateOf(prefs.bedtimeMinute) }
    var wakeHour by remember { mutableIntStateOf(prefs.wakeHour) }
    var wakeMinute by remember { mutableIntStateOf(prefs.wakeMinute) }
    var pledgeText by remember { mutableStateOf(prefs.familyPledge) }

    // Launcher for VPN permission
    val vpnLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.toggleShieldManually(context)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shield Header Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = if (isShieldActive) CardDark else SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, if (isShieldActive) DangerRed else BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Bedtime Shield Status", fontSize = 14.sp, color = TextSecondary)
                        Text(
                            text = if (isShieldActive) "ACTIVE (INTERNET BLOCKED)" else "READY (SCHEDULED)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isShieldActive) DangerRed else HealthGreen
                        )
                    }
                    Button(
                        onClick = {
                            val prepareIntent = VpnService.prepare(context)
                            if (prepareIntent != null) {
                                vpnLauncher.launch(prepareIntent)
                            } else {
                                viewModel.toggleShieldManually(context)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isShieldActive) DangerRed else SleepBlue
                        )
                    ) {
                        Text(if (isShieldActive) "Turn Off" else "Test Shield Now")
                    }
                }
            }
        }

        // Schedule Configuration
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Protection Schedule", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    "Bedtime is strictly enforced to protect your 7 to 8 hours of restorative sleep.",
                    fontSize = 13.sp,
                    color = TextSecondary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Bedtime Cutoff", fontSize = 12.sp, color = TextSecondary)
                        Text(String.format("%02d:%02d PM", bedtimeHour, bedtimeMinute), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = SleepBlue)
                    }
                    Column {
                        Text("Wake Up Alarm", fontSize = 12.sp, color = TextSecondary)
                        Text(String.format("%02d:%02d AM", wakeHour, wakeMinute), fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = HealthGreen)
                    }
                }

                Button(
                    onClick = {
                        viewModel.saveShieldSchedule(bedtimeHour, bedtimeMinute, wakeHour, wakeMinute)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = CardDark)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = SleepBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Arm Schedule", color = TextPrimary)
                }
            }
        }

        // Infinix Hot 30i / Android System Permissions Checklist
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Infinix Hot 30i Permissions Checklist", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("Grant these once so Infinix XOS doesn't kill the background shield:", fontSize = 13.sp, color = TextSecondary)

                // 1. Overlay Permission
                val hasOverlayPermission = Settings.canDrawOverlays(context)
                PermissionRow(
                    title = "1. Display Over Other Apps",
                    granted = hasOverlayPermission,
                    onGrantClick = {
                        val intent = Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:${context.packageName}")
                        )
                        context.startActivity(intent)
                    }
                )

                // 2. Battery Optimization Bypass
                val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                val isIgnoringBattery = powerManager.isIgnoringBatteryOptimizations(context.packageName)
                PermissionRow(
                    title = "2. Unrestricted Battery (XOS)",
                    granted = isIgnoringBattery,
                    onGrantClick = {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                    }
                )

                // 3. Exact Alarm Permission (Android 12/13/14)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
                    val canExact = alarmManager.canScheduleExactAlarms()
                    PermissionRow(
                        title = "3. Exact Alarm Trigger",
                        granted = canExact,
                        onGrantClick = {
                            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                                data = Uri.parse("package:${context.packageName}")
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }

        // Family Pledge Customizer
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            modifier = Modifier.fillMaxWidth().border(1.dp, BorderDark, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Father's Shield Pledge Message", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("This message displays during lockdown to instantly break the doom-scrolling reflex:", fontSize = 13.sp, color = TextSecondary)

                OutlinedTextField(
                    value = pledgeText,
                    onValueChange = { pledgeText = it },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Button(
                    onClick = { prefs.familyPledge = pledgeText },
                    colors = ButtonDefaults.buttonColors(containerColor = CardDark),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Save Pledge", color = HealthGreen)
                }
            }
        }
    }
}

@Composable
private fun PermissionRow(
    title: String,
    granted: Boolean,
    onGrantClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text(
                if (granted) "Granted ✓" else "Not Granted (Required)",
                fontSize = 12.sp,
                color = if (granted) HealthGreen else DangerRed
            )
        }
        if (!granted) {
            OutlinedButton(onClick = onGrantClick) {
                Text("Allow", fontSize = 12.sp)
            }
        } else {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = HealthGreen)
        }
    }
}
