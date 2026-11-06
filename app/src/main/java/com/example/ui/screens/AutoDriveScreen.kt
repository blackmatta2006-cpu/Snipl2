package com.example.ui.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.OfcRoute
import com.example.model.PointType
import com.example.model.RoutePoint
import com.example.ui.components.GisMapCanvas
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.FaultRed
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber
import com.example.util.RealLocationTracker

@Composable
fun AutoDriveScreen(
    activeRoute: OfcRoute,
    isDriving: Boolean,
    isRealGps: Boolean,
    gpsStatusText: String,
    gpsAccuracy: Float,
    autoIntervalMeters: Int,
    drivePoints: List<RoutePoint>,
    driveOdometerMeters: Double,
    driveSpeedKmh: Double,
    onStartRealGpsDrive: () -> Unit,
    onStartDriveSimulation: () -> Unit,
    onStopDrive: () -> Unit,
    onRealLocationReceived: (Double, Double, Double, Float) -> Unit,
    onStatusChanged: (String) -> Unit,
    onAutoIntervalChange: (Int) -> Unit,
    onDropManualPoint: (PointType, Int) -> Unit,
    onFinishAndSave: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var routeNameInput by remember { mutableStateOf("") }
    var locationTracker: RealLocationTracker? by remember { mutableStateOf(null) }

    // Runtime Permission Launcher for Real Device GPS
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val fineGranted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseGranted = perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineGranted || coarseGranted) {
            locationTracker?.startTracking()
            onStartRealGpsDrive()
        } else {
            onStatusChanged("⚠️ Location Permission Denied (Please allow location)")
        }
    }

    DisposableEffect(context) {
        val tracker = RealLocationTracker(
            context = context,
            onLocationChanged = { lat, lng, speedKmh, accuracy ->
                onRealLocationReceived(lat, lng, speedKmh, accuracy)
            },
            onStatusChanged = { status ->
                onStatusChanged(status)
            }
        )
        locationTracker = tracker

        onDispose {
            tracker.stopTracking()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .testTag("auto_drive_screen")
    ) {
        // Telemetry header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .border(1.dp, BorderDark)
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = if (isRealGps) "🛰️" else "🚗", fontSize = 24.sp)
                        Column {
                            Text(
                                text = if (isRealGps) "Real Road GPS Auto-Drive" else "Simulated Road Drive (Demo)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = TextWhite
                            )
                            Text(
                                text = gpsStatusText,
                                fontSize = 11.sp,
                                color = if (isDriving) NeonEmerald else TextMuted
                            )
                        }
                    }

                    // Mode switch pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isDriving) NeonEmerald.copy(alpha = 0.2f) else SurfaceCard)
                            .border(
                                1.dp,
                                if (isDriving) NeonEmerald else BorderDark,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isDriving) "🟢 RECORDING" else "STANDBY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDriving) NeonEmerald else TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Mode toggle (Real GPS vs Demo Sim) & Interval Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        ModeChip(
                            label = "📡 Real GPS",
                            isSelected = isRealGps,
                            accent = NeonEmerald
                        ) {
                            if (!isDriving) {
                                // switch mode
                            }
                        }
                        ModeChip(
                            label = "🚙 Demo Sim",
                            isSelected = !isRealGps,
                            accent = CyberCyan
                        ) {
                            if (!isDriving) {
                                // switch mode
                            }
                        }
                    }

                    // Interval Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "Interval:", fontSize = 10.sp, color = TextMuted)
                        listOf(10, 15, 25, 50).forEach { dist ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (autoIntervalMeters == dist) WarningAmber.copy(alpha = 0.25f) else Color(0xFF070B14))
                                    .border(1.dp, if (autoIntervalMeters == dist) WarningAmber else BorderDark, RoundedCornerShape(6.dp))
                                    .clickable { onAutoIntervalChange(dist) }
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${dist}m",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (autoIntervalMeters == dist) WarningAmber else TextMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 4 Telemetry Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DriveMetricBox(
                        label = "ODOMETER",
                        value = "${driveOdometerMeters.toInt()} m",
                        color = NeonEmerald,
                        modifier = Modifier.weight(1.1f)
                    )
                    DriveMetricBox(
                        label = "SPEED",
                        value = "${driveSpeedKmh.toInt()} km/h",
                        color = CyberCyan,
                        modifier = Modifier.weight(1f)
                    )
                    DriveMetricBox(
                        label = "GPS POINTS",
                        value = "${drivePoints.size}",
                        color = TextWhite,
                        modifier = Modifier.weight(0.9f)
                    )
                    DriveMetricBox(
                        label = "ACCURACY",
                        value = if (gpsAccuracy > 0f) "±${gpsAccuracy.toInt()}m" else "Ready",
                        color = WarningAmber,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Map showing live vehicle & recorded route
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            GisMapCanvas(
                route = activeRoute,
                faultResult = null,
                drivenPoints = drivePoints
            )
        }

        // Quick drop and drive controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .border(1.dp, BorderDark)
                .padding(14.dp)
        ) {
            Column {
                // Drop buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = { onDropManualPoint(PointType.POLE, 0) },
                        modifier = Modifier.weight(1f).height(40.dp).testTag("drop_pole_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard)
                    ) {
                        Text("📍 + Pole", fontSize = 11.sp, color = TextWhite)
                    }

                    Button(
                        onClick = { onDropManualPoint(PointType.JC, 30) },
                        modifier = Modifier.weight(1.1f).height(40.dp).testTag("drop_jc_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber)
                    ) {
                        Text("📦 + JC (30m)", fontSize = 11.sp, color = WarningAmber, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onDropManualPoint(PointType.CHAMBER, 40) },
                        modifier = Modifier.weight(1.1f).height(40.dp).testTag("drop_chamber_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent.copy(alpha = 0.2f)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PurpleAccent)
                    ) {
                        Text("🕳️ + Chamber", fontSize = 11.sp, color = PurpleAccent, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Route Name Input and Action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = routeNameInput,
                        onValueChange = { routeNameInput = it },
                        placeholder = { Text("Route Name (e.g. NH-48 Highway OFC)", fontSize = 11.sp, color = TextMuted) },
                        modifier = Modifier.weight(1f).testTag("driven_route_name_input"),
                        shape = RoundedCornerShape(12.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextWhite),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF070B14),
                            unfocusedContainerColor = Color(0xFF070B14),
                            focusedBorderColor = NeonEmerald,
                            unfocusedBorderColor = BorderDark
                        ),
                        singleLine = true
                    )

                    if (!isDriving) {
                        Button(
                            onClick = {
                                if (locationTracker?.hasPermissions() == true) {
                                    locationTracker?.startTracking()
                                    onStartRealGpsDrive()
                                } else {
                                    permissionLauncher.launch(
                                        arrayOf(
                                            Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION
                                        )
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                            modifier = Modifier.height(48.dp).testTag("start_real_gps_drive_button")
                        ) {
                            Text("▶️ Real GPS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                        }

                        OutlinedButton(
                            onClick = onStartDriveSimulation,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                            modifier = Modifier.height(48.dp).testTag("start_drive_sim_button")
                        ) {
                            Text("Sim Demo", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                locationTracker?.stopTracking()
                                onStopDrive()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = FaultRed),
                            modifier = Modifier.height(48.dp).testTag("stop_drive_button")
                        ) {
                            Text("⏹️ Pause", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = {
                            locationTracker?.stopTracking()
                            onFinishAndSave(routeNameInput)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CyberCyan),
                        modifier = Modifier.height(48.dp).testTag("save_driven_route_button")
                    ) {
                        Text("💾 Save", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }
                }
            }
        }
    }
}

@Composable
private fun ModeChip(
    label: String,
    isSelected: Boolean,
    accent: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) accent.copy(alpha = 0.2f) else SurfaceCard)
            .border(1.dp, if (isSelected) accent else BorderDark, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) accent else TextMuted
        )
    }
}

@Composable
private fun DriveMetricBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF070B14))
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp)
    ) {
        Column {
            Text(text = label, fontSize = 8.sp, color = TextMuted)
            Text(
                text = value,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = color
            )
        }
    }
}
