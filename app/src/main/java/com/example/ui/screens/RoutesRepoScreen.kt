package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.data.OtdrCalculator
import com.example.model.AppScreen
import com.example.model.OfcRoute
import com.example.model.PointType
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber

@Composable
fun RoutesRepoScreen(
    routes: List<OfcRoute>,
    activeRouteId: String,
    onSelectRoute: (String) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    onOpenKmlDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .padding(14.dp)
            .testTag("routes_repo_screen")
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "🛣️ OFC Routes Repository",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                    Text(
                        text = "${routes.size} OFC ಲೈನ್‌ಗಳು & ಜಾಯಿಂಟ್ ಬಾಕ್ಸ್ ಪಟ್ಟಿ",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = onOpenKmlDialog,
                        modifier = Modifier.height(36.dp).testTag("routes_import_kml_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                    ) {
                        Text("📂 KML", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Button(
                        onClick = { onNavigate(AppScreen.AUTO_DRIVE) },
                        modifier = Modifier.height(36.dp).testTag("routes_drive_button"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald)
                    ) {
                        Text("🚗 + Drive", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(routes, key = { it.id }) { r ->
                val analysis = OtdrCalculator.analyzeRoute(r, "forward", "optical", 3.0)
                val isActive = r.id == activeRouteId
                var expanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceDark)
                        .border(
                            width = if (isActive) 1.5.dp else 1.dp,
                            color = if (isActive) NeonEmerald else BorderDark,
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(14.dp)
                        .testTag("route_card_${r.id}")
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NeonEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = r.fiberType,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonEmerald
                                )
                            }
                            Text(
                                text = "${r.points.size} Nodes",
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = r.name,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Ground: ${analysis.totalGroundMeters.toInt()}m • Optical: ${analysis.totalOpticalMeters.toInt()}m (with slack)",
                            fontSize = 11.sp,
                            color = CyberCyan
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onSelectRoute(r.id)
                                    onNavigate(AppScreen.GIS_MAP)
                                },
                                modifier = Modifier.weight(1f).height(36.dp).testTag("select_route_map_${r.id}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald)
                            ) {
                                Text("🗺️ View Map", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            Button(
                                onClick = {
                                    onSelectRoute(r.id)
                                    onNavigate(AppScreen.BOT)
                                },
                                modifier = Modifier.weight(1f).height(36.dp).testTag("select_route_bot_${r.id}"),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
                            ) {
                                Text("💬 Test Bot", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            }

                            OutlinedButton(
                                onClick = { expanded = !expanded },
                                modifier = Modifier.height(36.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                            ) {
                                Text(if (expanded) "▲" else "▼ Nodes", fontSize = 11.sp)
                            }
                        }

                        // Expanded Nodes List
                        if (expanded) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF070B14))
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                r.points.forEachIndexed { idx, pt ->
                                    val color = when (pt.type) {
                                        PointType.OLT -> CyberCyan
                                        PointType.JC -> WarningAmber
                                        PointType.CHAMBER -> PurpleAccent
                                        PointType.END -> CyberCyan
                                        PointType.POLE -> TextMuted
                                    }
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "${idx + 1}. ${pt.label}",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = color
                                            )
                                            Text(
                                                text = "${pt.lat}, ${pt.lng} • Slack: ${pt.slackMeters}m",
                                                fontSize = 9.sp,
                                                fontFamily = FontFamily.Monospace,
                                                color = TextMuted
                                            )
                                        }
                                        Text(
                                            text = "Maps ↗",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyberCyan,
                                            modifier = Modifier
                                                .clickable {
                                                    val url = OtdrCalculator.buildGoogleMapsUrl(pt.lat, pt.lng)
                                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                                                }
                                                .padding(4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
