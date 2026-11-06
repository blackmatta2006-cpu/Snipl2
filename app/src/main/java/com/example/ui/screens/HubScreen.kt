package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppScreen
import com.example.model.FaultResult
import com.example.model.OfcRoute
import com.example.model.UserRole
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

@Composable
fun HubScreen(
    currentRole: UserRole,
    activeRoute: OfcRoute,
    faultResult: FaultResult,
    totalRoutesCount: Int,
    openTicketsCount: Int,
    onNavigate: (AppScreen) -> Unit,
    onOpenKmlDialog: () -> Unit,
    onActivateReverseOtdr: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .testTag("hub_screen"),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Hero Header Card
        item(span = { GridItemSpan(2) }) {
            HeroHeaderCard(
                currentRole = currentRole,
                activeRoute = activeRoute,
                faultResult = faultResult,
                totalRoutesCount = totalRoutesCount,
                openTicketsCount = openTicketsCount
            )
        }

        if (currentRole == UserRole.SPLICER) {
            // SPLICER EXCLUSIVE 8 CARDS
            item {
                HubFeatureCard(
                    icon = "💬",
                    title = "1. OTDR Cut Bot",
                    subtitle = "ಮೀಟರ್ ಕಳಿಸಿದ ತಕ್ಷಣ ನಿಖರ Google Maps & Street View ಲಿಂಕ್",
                    badge = "BOT →",
                    accentColor = NeonEmerald,
                    onClick = { onNavigate(AppScreen.BOT) },
                    testTag = "hub_card_bot"
                )
            }
            item {
                HubFeatureCard(
                    icon = "🗺️",
                    title = "2. Giant Fault Map",
                    subtitle = "ಇಡೀ ಸ್ಕ್ರೀನ್‌ನಲ್ಲಿ ದೊಡ್ಡ OFC ಕೇಬಲ್ ರೂಟ್ ಮತ್ತು ಕಟ್ ಜಾಗ",
                    badge = "MAP →",
                    accentColor = CyberCyan,
                    onClick = { onNavigate(AppScreen.GIS_MAP) },
                    testTag = "hub_card_map"
                )
            }
            item {
                HubFeatureCard(
                    icon = "🧍‍♂️",
                    title = "3. 360° Street View",
                    subtitle = "ಕಟ್ ಜಾಗದ ರಸ್ತೆ ನೋಟ, ಕ್ಯಾಮೆರಾ ದಿಕ್ಕು ಮತ್ತು ಪೋಲ್ ವಿವರ",
                    badge = "360° VIEW →",
                    accentColor = WarningAmber,
                    onClick = { onNavigate(AppScreen.CORRIDOR_RECON) },
                    testTag = "hub_card_streetview"
                )
            }
            item {
                HubFeatureCard(
                    icon = "📈",
                    title = "4. OTDR Trace",
                    subtitle = "ದೊಡ್ಡ OTDR Reflectometer ಗ್ರಾಫ್ & ಲೈವ್ ಟ್ರೇಸ್ ಅನಾಲಿಸಿಸ್",
                    badge = "TRACE →",
                    accentColor = PurpleAccent,
                    onClick = { onNavigate(AppScreen.TRACE) },
                    testTag = "hub_card_trace"
                )
            }
            item {
                HubFeatureCard(
                    icon = "📋",
                    title = "5. Copy & WhatsApp",
                    subtitle = "Google Maps, Street View ಲಿಂಕ್ ಕಾಪಿ & WhatsApp ಶೇರ್",
                    badge = "SHARE →",
                    accentColor = FaultRed,
                    onClick = { onNavigate(AppScreen.DISPATCH_LINKS) },
                    testTag = "hub_card_links"
                )
            }
            item {
                HubFeatureCard(
                    icon = "🛰️",
                    title = "6. Satellite View",
                    subtitle = "ಉಪಗ್ರಹ ಚಿತ್ರಣದಲ್ಲಿ ಮರಗಳು ಮತ್ತು ಕಟ್ಟಡಗಳ ಪಕ್ಕದ ಕಟ್ ನೋಡಿ",
                    badge = "SATELLITE →",
                    accentColor = CyberCyan,
                    onClick = { onNavigate(AppScreen.GIS_MAP) },
                    testTag = "hub_card_satellite"
                )
            }
            item {
                HubFeatureCard(
                    icon = "📦",
                    title = "7. Poles & Joint Boxes",
                    subtitle = "ರೂಟ್‌ನಲ್ಲಿರುವ ಎಲ್ಲಾ ಜಾಯಿಂಟ್ ಬಾಕ್ಸ್, ಪೋಲ್ಸ್ ಮತ್ತು ಲೂಪ್ ಸ್ಲ್ಯಾಕ್",
                    badge = "POLES →",
                    accentColor = NeonEmerald,
                    onClick = { onNavigate(AppScreen.ROUTES_REPO) },
                    testTag = "hub_card_poles"
                )
            }
            item {
                HubFeatureCard(
                    icon = "🧭",
                    title = "8. Reverse OTDR",
                    subtitle = "ಕೊನೆಯ ನೋಡ್‌ನಿಂದ (End → OLT) ಸ್ಲ್ಯಾಕ್ ಸಮೇತ ಲೆಕ್ಕಾಚಾರ",
                    badge = "REVERSE →",
                    accentColor = WarningAmber,
                    onClick = onActivateReverseOtdr,
                    testTag = "hub_card_reverse"
                )
            }
        } else {
            // ADMIN / SURVEYOR EXCLUSIVE 8 CARDS
            item {
                HubFeatureCard(
                    icon = "🚗",
                    title = "1. Auto-Draw (Driving)",
                    subtitle = "ಗಾಡಿಯಲ್ಲಿ ಡ್ರೈವ್ ಮಾಡುತ್ತಾ ಹೊಸ OFC ರೂಟ್ ಆಟೋ-ಮ್ಯಾಪ್ ಮಾಡಿ",
                    badge = "AUTO-DRIVE →",
                    accentColor = NeonEmerald,
                    onClick = { onNavigate(AppScreen.AUTO_DRIVE) },
                    testTag = "hub_card_drive"
                )
            }
            item {
                HubFeatureCard(
                    icon = "✏️",
                    title = "2. Add Route on Map",
                    subtitle = "ದೊಡ್ಡ GIS ಮ್ಯಾಪ್ ಮೇಲೆ ಪಾಯಿಂಟ್ಸ್ ಸೇರಿಸಿ ಹೊಸ ಲೈನ್ ಬಿಡಿಸಿ",
                    badge = "ADD ROUTE →",
                    accentColor = CyberCyan,
                    onClick = { onNavigate(AppScreen.GIS_MAP) },
                    testTag = "hub_card_add_route"
                )
            }
            item {
                HubFeatureCard(
                    icon = "🛣️",
                    title = "3. Routes Repository",
                    subtitle = "ಎಲ್ಲಾ OFC ಕೇಬಲ್ ರೂಟ್‌ಗಳು & ಫೈಬರ್ ಕೋರ್ ವಿವರಗಳ ಪಟ್ಟಿ",
                    badge = "ALL ROUTES →",
                    accentColor = WarningAmber,
                    onClick = { onNavigate(AppScreen.ROUTES_REPO) },
                    testTag = "hub_card_routes_repo"
                )
            }
            item {
                HubFeatureCard(
                    icon = "📂",
                    title = "4. KML & GeoJSON",
                    subtitle = "Google Earth KML ಕೋಆರ್ಡಿನೇಟ್ಸ್ ನೇರ ಆಮದು/ರಫ್ತು",
                    badge = "KML / GIS →",
                    accentColor = PurpleAccent,
                    onClick = onOpenKmlDialog,
                    testTag = "hub_card_kml"
                )
            }
            item {
                HubFeatureCard(
                    icon = "👥",
                    title = "5. Staff & Fault Tickets",
                    subtitle = "ಸ್ಪ್ಲೈಸರ್ಸ್, ಡ್ರೈವರ್ಸ್ ನಿರ್ವಹಣೆ & ಲೈವ್ ಕಟ್ ಟಿಕೆಟ್ಸ್ ಸ್ಟೇಟಸ್",
                    badge = "ADMIN SUITE →",
                    accentColor = FaultRed,
                    onClick = { onNavigate(AppScreen.ADMIN_PANEL) },
                    testTag = "hub_card_admin_suite"
                )
            }
            item {
                HubFeatureCard(
                    icon = "💬",
                    title = "6. OTDR Splicer Bot",
                    subtitle = "ಯಾವುದೇ ರೂಟ್‌ನ OTDR ಮೀಟರ್ ಹಾಕಿ ಮ್ಯಾಪ್ ಲಿಂಕ್ ಪರೀಕ್ಷಿಸಿ",
                    badge = "BOT TEST →",
                    accentColor = NeonEmerald,
                    onClick = { onNavigate(AppScreen.BOT) },
                    testTag = "hub_card_admin_bot"
                )
            }
            item {
                HubFeatureCard(
                    icon = "📈",
                    title = "7. OTDR Trace Graph",
                    subtitle = "ದೊಡ್ಡ OTDR Reflectometer ಟ್ರೇಸ್ ಗ್ರಾಫ್ ಮತ್ತು dB ಲಾಸ್",
                    badge = "WAVEFORM →",
                    accentColor = CyberCyan,
                    onClick = { onNavigate(AppScreen.TRACE) },
                    testTag = "hub_card_admin_trace"
                )
            }
            item {
                HubFeatureCard(
                    icon = "☁️",
                    title = "8. Cloud Database Sync",
                    subtitle = "Firebase Realtime DB (ott-app-96be5) ಲೈವ್ ಸಿಂಕ್",
                    badge = "CLOUD SYNC →",
                    accentColor = WarningAmber,
                    onClick = { onNavigate(AppScreen.ADMIN_PANEL) },
                    testTag = "hub_card_cloud_sync"
                )
            }
        }
    }
}

@Composable
private fun HeroHeaderCard(
    currentRole: UserRole,
    activeRoute: OfcRoute,
    faultResult: FaultResult,
    totalRoutesCount: Int,
    openTicketsCount: Int
) {
    val brush = if (currentRole == UserRole.ADMIN) {
        Brush.linearGradient(
            listOf(Color(0xFF082F49), Color(0xFF0F172A), Color(0xFF042F2E))
        )
    } else {
        Brush.linearGradient(
            listOf(Color(0xFF022C22), Color(0xFF0F172A), Color(0xFF083344))
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(brush)
            .border(
                1.dp,
                if (currentRole == UserRole.ADMIN) CyberCyan.copy(alpha = 0.5f) else NeonEmerald.copy(alpha = 0.5f),
                RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (currentRole == UserRole.ADMIN) "🛡️ MASTER ADMIN SUITE" else "🔧 FIELD SPLICER PORTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = if (currentRole == UserRole.ADMIN) CyberCyan else NeonEmerald,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (currentRole == UserRole.ADMIN) "ಅಡ್ಮಿನ್ ಪ್ಯಾನೆಲ್ (Admin Mode)" else "ಯೂಸರ್ ಪ್ಯಾನೆಲ್ (Splicer Mode)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = activeRoute.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1)
                    )
                }

                // Fault Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xCC030712))
                        .border(1.dp, FaultRed.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "ACTIVE CUT",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "${faultResult.clampedMeters}m",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            fontFamily = FontFamily.Monospace,
                            color = FaultRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricPill(
                    label = "Optical Len",
                    value = "${(faultResult.totalOpticalMeters / 1000.0).let { "%.2f".format(it) }} km",
                    accent = NeonEmerald,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Total Routes",
                    value = "$totalRoutesCount Routes",
                    accent = CyberCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricPill(
                    label = "Open Cuts",
                    value = "$openTicketsCount Tickets",
                    accent = FaultRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MetricPill(
    label: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x880F172A))
            .border(1.dp, BorderDark, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column {
            Text(text = label, fontSize = 9.sp, color = TextMuted)
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = accent)
        }
    }
}

@Composable
private fun HubFeatureCard(
    icon: String,
    title: String,
    subtitle: String,
    badge: String,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(145.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 20.sp)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = accentColor
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = TextWhite,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = TextMuted,
                    lineHeight = 13.sp,
                    maxLines = 2
                )
            }
        }
    }
}
