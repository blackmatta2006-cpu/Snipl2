package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun DispatchLinksScreen(
    activeRoute: OfcRoute,
    faultResult: FaultResult,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val mapsUrl = OtdrCalculator.buildGoogleMapsUrl(faultResult.lat, faultResult.lng)
    val streetViewUrl = OtdrCalculator.buildStreetViewUrl(faultResult.lat, faultResult.lng, faultResult.heading)

    val dispatchMessage = """
🚨 *OFC CABLE FAULT ALERT — FiberGrid Pro*
━━━━━━━━━━━━━━━━━━━━
🛣️ *ರೂಟ್ (Route):* ${activeRoute.name}
⚡ *OTDR Cut Reading:* ${faultResult.clampedMeters}m (${(faultResult.clampedMeters / 1000.0).let { "%.2f".format(it) }} km)
📍 *ಹತ್ತಿರದ ಪಾಯಿಂಟ್:* ${faultResult.refLandmark.label} (${faultResult.metersFromLandmark}m away)
🧭 *Bearing:* ${faultResult.heading}° (${faultResult.compassDir})
📌 *GPS Coords:* ${faultResult.lat}, ${faultResult.lng}

🗺️ *Google Maps Link:*
$mapsUrl

🧍‍♂️ *360° Street View Link:*
$streetViewUrl
━━━━━━━━━━━━━━━━━━━━
Field Splicer Dispatched.
    """.trimIndent()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .testTag("dispatch_links_screen")
    ) {
        // Cut Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SurfaceDark)
                .border(1.dp, FaultRed.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "🚨 OFC CUT LOCATION DETECTED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = FaultRed,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${faultResult.refLandmark.label}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TextWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(FaultRed.copy(alpha = 0.15f))
                            .border(1.dp, FaultRed, RoundedCornerShape(14.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${faultResult.clampedMeters}m",
                            fontSize = 16.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            color = FaultRed
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "ಇಲ್ಲಿಂದ ${faultResult.metersFromLandmark} ಮೀಟರ್ ದೂರದಲ್ಲಿ ಕಟ್ ಆಗಿದೆ • ದಿಕ್ಕು: ${faultResult.heading}° ${faultResult.compassDir}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Coordinates & Maps Link Box
        LinkCard(
            title = "1. Google Maps Navigation Link",
            url = mapsUrl,
            accentColor = NeonEmerald,
            onOpen = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(mapsUrl))
                context.startActivity(intent)
            },
            onCopy = { clipboardManager.setText(AnnotatedString(mapsUrl)) },
            testTag = "maps_link_card"
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Street View Link Box
        LinkCard(
            title = "2. 360° Google Street View Panorama Link",
            url = streetViewUrl,
            accentColor = WarningAmber,
            onOpen = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(streetViewUrl))
                context.startActivity(intent)
            },
            onCopy = { clipboardManager.setText(AnnotatedString(streetViewUrl)) },
            testTag = "sv_link_card"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Big WhatsApp / Share Action Button
        Button(
            onClick = {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, dispatchMessage)
                }
                context.startActivity(Intent.createChooser(intent, "Share OFC Cut Details via WhatsApp"))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("whatsapp_share_large_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
        ) {
            Text(
                text = "💬 WhatsApp ಗೆ ಕಳಿಸಿ (Send to Splicer)",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { onNavigate(AppScreen.CORRIDOR_RECON) },
                modifier = Modifier.weight(1f).height(46.dp).testTag("recon_preview_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = WarningAmber)
            ) {
                Text("🧍 360° Recon View", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { onNavigate(AppScreen.GIS_MAP) },
                modifier = Modifier.weight(1f).height(46.dp).testTag("view_on_gis_map_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
            ) {
                Text("🗺️ Giant Map", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun LinkCard(
    title: String,
    url: String,
    accentColor: Color,
    onOpen: () -> Unit,
    onCopy: () -> Unit,
    testTag: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceDark)
            .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
            .padding(14.dp)
            .testTag(testTag)
    ) {
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF070B14))
                    .padding(10.dp)
            ) {
                Text(
                    text = url,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    color = accentColor,
                    lineHeight = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onOpen,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor)
                ) {
                    Text("Open ↗", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
                OutlinedButton(
                    onClick = onCopy,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                ) {
                    Text("Copy Link 📋", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
