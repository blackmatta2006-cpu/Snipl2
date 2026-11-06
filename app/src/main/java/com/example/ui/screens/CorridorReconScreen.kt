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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.components.StreetViewCorridorCanvas
import com.example.ui.theme.BorderDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.FaultRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WarningAmber

@Composable
fun CorridorReconScreen(
    activeRoute: OfcRoute,
    faultResult: FaultResult,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val svUrl = OtdrCalculator.buildStreetViewUrl(faultResult.lat, faultResult.lng, faultResult.heading)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF030712))
            .padding(14.dp)
            .testTag("corridor_recon_screen")
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
                        text = "🧍 360° Street View & Recon",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TextWhite
                    )
                    Text(
                        text = "ರಸ್ತೆ ನೋಟ, ಕ್ಯಾಮೆರಾ ದಿಕ್ಕು ಮತ್ತು ಪೋಲ್ ಅಂತರ",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(WarningAmber.copy(alpha = 0.2f))
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
        }

        Spacer(modifier = Modifier.height(12.dp))

        // In-App Perspective Corridor Canvas
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF090D16))
                .border(1.dp, BorderDark, RoundedCornerShape(20.dp))
        ) {
            StreetViewCorridorCanvas(faultResult = faultResult)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Landmark & Splicer Guidance
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SurfaceDark)
                .border(1.dp, BorderDark, RoundedCornerShape(18.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "📍 ಸ್ಪ್ಲೈಸರ್ ಗುರುತು (Field Landmark):",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${faultResult.refLandmark.label} ಇಂದ ${faultResult.metersFromLandmark} ಮೀಟರ್ ದೂರದಲ್ಲಿದೆ",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ಕ್ಯಾಮೆರಾ ದಿಕ್ಕು: ${faultResult.heading}° (${faultResult.compassDir}) • GPS: ${faultResult.lat}, ${faultResult.lng}",
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = CyberCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(svUrl))
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1.3f).height(46.dp).testTag("open_external_street_view_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WarningAmber)
                    ) {
                        Text("🧍 360° Street View ↗", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.Black)
                    }

                    OutlinedButton(
                        onClick = { clipboardManager.setText(AnnotatedString(svUrl)) },
                        modifier = Modifier.weight(1f).height(46.dp).testTag("copy_sv_url_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                    ) {
                        Text("📋 Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { onNavigate(AppScreen.GIS_MAP) },
                        modifier = Modifier.weight(1f).height(46.dp).testTag("corridor_to_map_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan)
                    ) {
                        Text("🗺️ Map", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
