package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppScreen
import com.example.model.UserRole
import com.example.ui.components.KmlImportExportDialog
import com.example.ui.components.RoleSelectionDialog
import com.example.ui.components.TopHeaderBar
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AutoDriveScreen
import com.example.ui.screens.BotScreen
import com.example.ui.screens.CorridorReconScreen
import com.example.ui.screens.DispatchLinksScreen
import com.example.ui.screens.GisMapScreen
import com.example.ui.screens.HubScreen
import com.example.ui.screens.OtdrTraceScreen
import com.example.ui.screens.RoutesRepoScreen
import com.example.ui.theme.BgDark
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.FiberGridViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                FiberGridApp()
            }
        }
    }
}

@Composable
fun FiberGridApp(viewModel: FiberGridViewModel = viewModel()) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val routes by viewModel.routes.collectAsStateWithLifecycle()
    val activeRouteId by viewModel.activeRouteId.collectAsStateWithLifecycle()
    val faultResult by viewModel.faultResult.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val faultTickets by viewModel.faultTickets.collectAsStateWithLifecycle()
    val staffMembers by viewModel.staffMembers.collectAsStateWithLifecycle()
    val direction by viewModel.direction.collectAsStateWithLifecycle()
    val slackMode by viewModel.slackMode.collectAsStateWithLifecycle()

    val isDriving by viewModel.isDriving.collectAsStateWithLifecycle()
    val isRealGps by viewModel.isRealGps.collectAsStateWithLifecycle()
    val gpsStatusText by viewModel.gpsStatusText.collectAsStateWithLifecycle()
    val gpsAccuracy by viewModel.gpsAccuracy.collectAsStateWithLifecycle()
    val autoIntervalMeters by viewModel.autoIntervalMeters.collectAsStateWithLifecycle()
    val drivePoints by viewModel.drivePoints.collectAsStateWithLifecycle()
    val driveOdometer by viewModel.driveOdometer.collectAsStateWithLifecycle()
    val driveSpeedKmh by viewModel.driveSpeedKmh.collectAsStateWithLifecycle()

    var showRoleDialog by remember { mutableStateOf(false) }
    var showKmlDialog by remember { mutableStateOf(false) }

    val activeRoute = viewModel.getActiveRoute()

    // Handle toast events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Hardware back button returns to Hub if in any subscreen
    BackHandler(enabled = currentScreen != AppScreen.HUB) {
        viewModel.navigateTo(AppScreen.HUB)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BgDark,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopHeaderBar(
                currentScreen = currentScreen,
                currentRole = currentRole,
                routes = routes,
                activeRouteId = activeRouteId,
                onRouteSelected = { viewModel.setActiveRoute(it) },
                onBackToHub = { viewModel.navigateTo(AppScreen.HUB) },
                onOpenRoleDialog = { showRoleDialog = true }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    AppScreen.HUB -> {
                        HubScreen(
                            currentRole = currentRole,
                            activeRoute = activeRoute,
                            faultResult = faultResult,
                            totalRoutesCount = routes.size,
                            openTicketsCount = faultTickets.count { it.status != com.example.model.TicketStatus.RESOLVED },
                            onNavigate = { viewModel.navigateTo(it) },
                            onOpenKmlDialog = { showKmlDialog = true },
                            onActivateReverseOtdr = {
                                viewModel.setDirection("reverse")
                                viewModel.navigateTo(AppScreen.BOT)
                            }
                        )
                    }

                    AppScreen.BOT -> {
                        BotScreen(
                            activeRoute = activeRoute,
                            faultResult = faultResult,
                            chatMessages = chatMessages,
                            direction = direction,
                            slackMode = slackMode,
                            onSendMessage = { viewModel.sendChatMessage(it) },
                            onDirectionChange = { viewModel.setDirection(it) },
                            onSlackModeChange = { viewModel.setSlackMode(it) },
                            onSliderChange = { viewModel.setFaultDistance(it) },
                            onClearChat = { viewModel.clearChat() },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    AppScreen.GIS_MAP -> {
                        GisMapScreen(
                            activeRoute = activeRoute,
                            faultResult = faultResult,
                            currentRole = currentRole,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    AppScreen.AUTO_DRIVE -> {
                        AutoDriveScreen(
                            activeRoute = activeRoute,
                            isDriving = isDriving,
                            isRealGps = isRealGps,
                            gpsStatusText = gpsStatusText,
                            gpsAccuracy = gpsAccuracy,
                            autoIntervalMeters = autoIntervalMeters,
                            drivePoints = drivePoints,
                            driveOdometerMeters = driveOdometer,
                            driveSpeedKmh = driveSpeedKmh,
                            onStartRealGpsDrive = { viewModel.startRealGpsDrive() },
                            onStartDriveSimulation = { viewModel.startDriveSimulation() },
                            onStopDrive = { viewModel.stopDrive() },
                            onRealLocationReceived = { lat, lng, speed, acc -> viewModel.onRealLocationUpdate(lat, lng, speed, acc) },
                            onStatusChanged = { viewModel.setGpsStatus(it) },
                            onAutoIntervalChange = { viewModel.setAutoInterval(it) },
                            onDropManualPoint = { type, slack -> viewModel.recordManualPoint(type, slack) },
                            onFinishAndSave = { routeName -> viewModel.finishAndSaveDrivenRoute(routeName) }
                        )
                    }

                    AppScreen.TRACE -> {
                        OtdrTraceScreen(
                            activeRoute = activeRoute,
                            faultResult = faultResult,
                            onDistanceScrub = { viewModel.setFaultDistance(it) },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    AppScreen.DISPATCH_LINKS -> {
                        DispatchLinksScreen(
                            activeRoute = activeRoute,
                            faultResult = faultResult,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    AppScreen.CORRIDOR_RECON -> {
                        CorridorReconScreen(
                            activeRoute = activeRoute,
                            faultResult = faultResult,
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }

                    AppScreen.ROUTES_REPO -> {
                        RoutesRepoScreen(
                            routes = routes,
                            activeRouteId = activeRouteId,
                            onSelectRoute = { viewModel.setActiveRoute(it) },
                            onNavigate = { viewModel.navigateTo(it) },
                            onOpenKmlDialog = { showKmlDialog = true }
                        )
                    }

                    AppScreen.ADMIN_PANEL -> {
                        AdminPanelScreen(
                            totalRoutesCount = routes.size,
                            faultTickets = faultTickets,
                            staffMembers = staffMembers,
                            onToggleTicket = { viewModel.toggleTicketStatus(it) },
                            onAddStaff = { name, phone, role -> viewModel.addStaffMember(name, phone, role) }
                        )
                    }
                }
            }
        }

        if (showRoleDialog) {
            RoleSelectionDialog(
                currentRole = currentRole,
                onRoleSelected = { role ->
                    viewModel.setUserRole(role)
                    if (role == UserRole.SURVEYOR) {
                        viewModel.navigateTo(AppScreen.AUTO_DRIVE)
                    }
                },
                onDismiss = { showRoleDialog = false }
            )
        }

        if (showKmlDialog) {
            KmlImportExportDialog(
                activeRoute = activeRoute,
                onImport = { kmlText -> viewModel.importKmlOrCoordinates(kmlText) },
                onDismiss = { showKmlDialog = false }
            )
        }
    }
}
