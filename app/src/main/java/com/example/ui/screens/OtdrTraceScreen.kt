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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.OtdrCalculator
import com.example.model.AppScreen
import com.example.model.FaultResult
import com.example.model.OfcRoute
import com.example.ui.components.OtdrTraceCanvas
import com.example.ui.components.StreetViewCorridorCanvas
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
fun OtdrTraceScreen(
    activeRoute: OfcRoute,
    faultResult: FaultResult,
    onDistanceScrub: (Int) -> Unit,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val mapsUrl = OtdrCalculator.buildGoogleMapsUrl(faultResult.lat, faultResult.lng)
    val streetViewUrl = OtdrCalculator.buildStreetViewUrl(faultResult.lat, faultResult.lng, faultResult.heading)
    var showInAppCorridor by remember { mutableStateOf(false) }

    val dispatchText = """
🚨 *OFC CUT DETECTED — OTDR TRACE*
Route: ${activeRoute.name}
Distance: ${faultResult.clampedMeters}m (${(faultResult.clampedMeters / 1000.0).let { "%.2f".format(it) }} km)
Nearest: ${faultResult.refLandmark.label} (${faultResult.metersFromLandmark}m away)
GPS: ${faultResult.lat}, ${faultResult.lng} (${faultResult.heading}° ${faultResult.compassDir})
📍 Maps: $mapsUrl
🧍‍♂️ 360° Street View: $streetViewUrl
    """.trimIndent()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .padding(14.dp)
            .verticalScroll(rememberScrollState())
            .testTag("otdr_trace_screen")
    ) {
        // Trace Header Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "📈 OTDR Reflectometer Trace",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                        Text(
                            text = activeRoute.name,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF070B14))
                            .border(1.dp, FaultRed, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "CUT @ ${faultResult.clampedMeters}m",
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = FaultRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TraceBadge(label = "Laser", value = "1550 nm", color = CyberCyan, modifier = Modifier.weight(1f))
                    TraceBadge(label = "Loss Coeff", value = "0.21 dB/km", color = NeonEmerald, modifier = Modifier.weight(1f))
                    TraceBadge(label = "Fiber Index", value = "1.4682", color = WarningAmber, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Large Waveform Canvas (Fixed height in scroll view)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF030712))
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
        ) {
            OtdrTraceCanvas(
                faultResult = faultResult,
                onScrubDistance = onDistanceScrub
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Scrubber Slider
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(16.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Scrub OTDR Distance (ಮೀಟರ್ ಸ್ಲೈಡರ್):", fontSize = 11.sp, color = TextMuted)
                Text(
                    text = "${faultResult.clampedMeters} m (${(faultResult.clampedMeters / 1000.0).let { "%.2f".format(it) }} km)",
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    color = NeonEmerald
                )
            }
            Slider(
                value = faultResult.clampedMeters.toFloat(),
                onValueChange = { onDistanceScrub(it.toInt()) },
                valueRange = 0f..faultResult.maxRouteMeters.toFloat().coerceAtLeast(100f),
                colors = SliderDefaults.colors(
                    thumbColor = NeonEmerald,
                    activeTrackColor = NeonEmerald,
                    inactiveTrackColor = BorderDark
                ),
                modifier = Modifier.testTag("trace_slider")
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // =================================================================
        // DEDICATED STREET VIEW & CUT PROXIMITY CARD (Direct from Trace!)
        // =================================================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(SurfaceDark)
                .border(1.dp, WarningAmber.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(14.dp)
                .testTag("trace_street_view_card")
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📍 ಹತ್ತಿರದ ಪಾಯಿಂಟ್ (Nearest Landmark):",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = faultResult.refLandmark.label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "ಇಲ್ಲಿಂದ ಕೇವಲ ${faultResult.metersFromLandmark} ಮೀಟರ್ ಅಂತರದಲ್ಲಿ ಕಟ್ ಆಗಿದೆ",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonEmerald
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(WarningAmber.copy(alpha = 0.2f))
                            .border(1.dp, WarningAmber.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "${faultResult.heading}° ${faultResult.compassDir}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = WarningAmber
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // GPS Coordinate pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GPS: ${faultResult.lat}, ${faultResult.lng}",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        color = CyberCyan
                    )
                    Text(
                        text = if (showInAppCorridor) "▲ Hide Recon" else "▼ 360° Preview",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningAmber,
                        modifier = Modifier
                            .clickable { showInAppCorridor = !showInAppCorridor }
                            .padding(4.dp)
                    )
                }

                // In-App Perspective Corridor Preview
                if (showInAppCorridor) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF090D16))
                            .border(1.dp, BorderDark, RoundedCornerShape(14.dp))
                    ) {
                        StreetViewCorridorCanvas(faultResult = faultResult)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action buttons right under the trace
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(streetViewUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1.3f).height(44.dp).testTag("trace_open_streetview_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                    ) {
                        Text("🧍‍♂️ 360° Street View ↗", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1.1f).height(44.dp).testTag("trace_open_maps_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald)
                    ) {
                        Text("📍 Maps ↗", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    }

                    OutlinedButton(
                        onClick = { clipboardManager.setText(AnnotatedString(streetViewUrl)) },
                        modifier = Modifier.weight(1f).height(44.dp).testTag("trace_copy_sv_url_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                    ) {
                        Text("📋 Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // WhatsApp Share Button
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, dispatchText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share Cut Alert via WhatsApp"))
                    },
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("trace_whatsapp_share_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Text("💬 WhatsApp Splicer ಗೆ ಕಳಿಸಿ (Share Alert)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onNavigate(AppScreen.GIS_MAP) },
                modifier = Modifier.weight(1f).height(44.dp).testTag("trace_view_map_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CyberCyan)
            ) {
                Text("🗺️ View on Map", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }

            OutlinedButton(
                onClick = { onNavigate(AppScreen.BOT) },
                modifier = Modifier.weight(1f).height(44.dp).testTag("trace_view_bot_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NeonEmerald)
            ) {
                Text("💬 OTDR Bot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TraceBadge(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceCard)
            .padding(horizontal = 6.dp, vertical = 4.dp)
    ) {
        Column {
            Text(text = label, fontSize = 9.sp, color = TextMuted)
            Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}
