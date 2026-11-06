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
import androidx.compose.foundation.layout.size
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
import com.example.model.FaultResult
import com.example.model.OfcRoute
import com.example.model.UserRole
import com.example.ui.components.GisMapCanvas
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.FaultRed
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber

@Composable
fun GisMapScreen(
    activeRoute: OfcRoute,
    faultResult: FaultResult,
    currentRole: UserRole,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    var isSatellite by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .testTag("gis_map_screen")
    ) {
        // Map Top Bar with legend and layer toggles
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark)
                .border(1.dp, BorderDark)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = NeonEmerald, label = "Intact")
                LegendItem(color = FaultRed, label = "Cut Point")
                LegendItem(color = WarningAmber, label = "JC Box")
            }

            // Layer toggle & 360 view
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (!isSatellite) NeonEmerald else SurfaceCard)
                        .clickable { isSatellite = false }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("layer_map_button")
                ) {
                    Text(
                        text = "🗺️ Map",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!isSatellite) Color.Black else TextWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSatellite) NeonEmerald else SurfaceCard)
                        .clickable { isSatellite = true }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("layer_satellite_button")
                ) {
                    Text(
                        text = "🛰️ Satellite",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSatellite) Color.Black else TextWhite
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(WarningAmber)
                        .clickable { onNavigate(AppScreen.CORRIDOR_RECON) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("open_recon_view_button")
                ) {
                    Text(
                        text = "🧍 360° View",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }
        }

        // The Full Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            GisMapCanvas(
                route = activeRoute,
                faultResult = faultResult,
                isSatellite = isSatellite
            )
        }

        // Bottom Info & Quick Navigation Card
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚠️ CUT: ${faultResult.clampedMeters}m (${faultResult.direction.uppercase()})",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            color = FaultRed
                        )
                        Text(
                            text = "ಹತ್ತಿರದ ಪಾಯಿಂಟ್: ${faultResult.refLandmark.label} (${faultResult.metersFromLandmark}m away)",
                            fontSize = 11.sp,
                            color = TextWhite
                        )
                    }
                    Text(
                        text = "${faultResult.lat}, ${faultResult.lng}",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberCyan
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val uri = Uri.parse(OtdrCalculator.buildGoogleMapsUrl(faultResult.lat, faultResult.lng))
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        modifier = Modifier.weight(1f).height(40.dp).testTag("gis_map_open_google_maps"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald)
                    ) {
                        Text("📍 Maps", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    Button(
                        onClick = {
                            val uri = Uri.parse(OtdrCalculator.buildStreetViewUrl(faultResult.lat, faultResult.lng, faultResult.heading))
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        },
                        modifier = Modifier.weight(1f).height(40.dp).testTag("gis_map_open_street_view"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                    ) {
                        Text("🧍 StreetView", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    OutlinedButton(
                        onClick = { onNavigate(AppScreen.DISPATCH_LINKS) },
                        modifier = Modifier.weight(1f).height(40.dp).testTag("gis_map_open_links"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                    ) {
                        Text("📋 Share", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(color)
        )
        Text(text = label, fontSize = 10.sp, color = TextMuted)
    }
}
