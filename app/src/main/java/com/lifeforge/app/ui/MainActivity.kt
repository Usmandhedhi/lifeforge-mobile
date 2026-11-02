package com.lifeforge.app.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.core.content.ContextCompat
import com.lifeforge.app.ui.screens.BedtimeShieldScreen
import com.lifeforge.app.ui.screens.ProgressScreen
import com.lifeforge.app.ui.screens.TodayDashboardScreen
import com.lifeforge.app.ui.screens.VanCommuteScreen
import com.lifeforge.app.ui.theme.BgDark
import com.lifeforge.app.ui.theme.CardDark
import com.lifeforge.app.ui.theme.HealthGreen
import com.lifeforge.app.ui.theme.LifeForgeTheme
import com.lifeforge.app.ui.theme.TextMuted
import com.lifeforge.app.ui.theme.TextPrimary

class MainActivity : ComponentActivity() {

    private val viewModel: RoutineViewModel by viewModels()

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* Handled */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Request POST_NOTIFICATIONS on Android 13+ (Infinix Hot 30i)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            LifeForgeTheme {
                MainAppScaffold(viewModel)
            }
        }
    }
}

enum class NavItem(val label: String, val icon: ImageVector) {
    TODAY("Today", Icons.Default.CheckCircle),
    SHIELD("Shield", Icons.Default.Security),
    COMMUTE("Van Mode", Icons.Default.DirectionsBus),
    PROGRESS("Progress", Icons.Default.TrendingUp)
}

@Composable
fun MainAppScaffold(viewModel: RoutineViewModel) {
    var selectedItem by remember { mutableStateOf(NavItem.TODAY) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = CardDark
            ) {
                NavItem.values().forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) },
                        selected = selectedItem == item,
                        onClick = { selectedItem = item },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = HealthGreen,
                            selectedTextColor = HealthGreen,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = BgDark
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BgDark)
                .padding(innerPadding)
        ) {
            when (selectedItem) {
                NavItem.TODAY -> TodayDashboardScreen(viewModel)
                NavItem.SHIELD -> BedtimeShieldScreen(viewModel)
                NavItem.COMMUTE -> VanCommuteScreen(viewModel)
                NavItem.PROGRESS -> ProgressScreen(viewModel)
            }
        }
    }
}
