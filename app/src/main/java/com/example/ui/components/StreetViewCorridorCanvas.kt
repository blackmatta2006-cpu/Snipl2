package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.testTag
import com.example.model.FaultResult
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.FaultRed
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.WarningAmber

@Composable
fun StreetViewCorridorCanvas(
    faultResult: FaultResult,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(Color(0xFF090D16))) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("corridor_canvas")
        ) {
            val W = size.width
            val H = size.height

            // Sky gradient
            val skyBrush = Brush.verticalGradient(
                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B)),
                startY = 0f,
                endY = H * 0.52f
            )
            drawRect(brush = skyBrush, size = androidx.compose.ui.geometry.Size(W, H * 0.52f))

            // Ground / Road asphalt
            drawRect(
                color = Color(0xFF0B0F19),
                topLeft = Offset(0f, H * 0.52f),
                size = androidx.compose.ui.geometry.Size(W, H * 0.48f)
            )

            // Perspective Road
            val vpX = W * 0.5f
            val vpY = H * 0.5f
            val roadPath = Path().apply {
                moveTo(vpX - 24f, vpY)
                lineTo(vpX + 24f, vpY)
                lineTo(W * 0.88f, H)
                lineTo(W * 0.12f, H)
                close()
            }
            drawPath(roadPath, color = Color(0xFF1A2234))

            // Center dashed line
            drawLine(
                color = Color(0xFF475569),
                start = Offset(vpX, vpY),
                end = Offset(vpX, H),
                strokeWidth = 3f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 16f), 0f)
            )

            // Utility Poles along the roadside
            data class PoleSpec(val x: Float, val baseY: Float, val topY: Float)
            val poles = listOf(
                PoleSpec(W * 0.22f, H * 0.94f, H * 0.18f),
                PoleSpec(W * 0.35f, H * 0.74f, H * 0.28f),
                PoleSpec(W * 0.44f, H * 0.60f, H * 0.38f)
            )

            // Draw pole posts and crossbars
            poles.forEach { p ->
                drawLine(
                    color = Color(0xFF94A3B8),
                    start = Offset(p.x, p.baseY),
                    end = Offset(p.x, p.topY),
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )
                // Cross arm
                drawLine(
                    color = Color(0xFF64748B),
                    start = Offset(p.x - 20f, p.topY + 12f),
                    end = Offset(p.x + 20f, p.topY + 12f),
                    strokeWidth = 3.5f
                )
            }

            // Aerial OFC Cable Span
            val p0 = poles[0]
            val p1 = poles[1]
            val p2 = poles[2]

            // Cut point is between p0 and p1
            val cutX = (p0.x + p1.x) / 2f
            val cutY = (p0.topY + p1.topY) / 2f + 8f

            // Intact Cable portion (Green)
            drawLine(
                color = NeonEmerald,
                start = Offset(p0.x, p0.topY + 10f),
                end = Offset(cutX, cutY),
                strokeWidth = 4.5f,
                cap = StrokeCap.Round
            )

            // Severed / Broken Cable portion (Dashed Red)
            drawLine(
                color = FaultRed,
                start = Offset(cutX, cutY + 4f),
                end = Offset(p1.x, p1.topY + 10f),
                strokeWidth = 3.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
            )
            drawLine(
                color = FaultRed.copy(alpha = 0.5f),
                start = Offset(p1.x, p1.topY + 10f),
                end = Offset(p2.x, p2.topY + 10f),
                strokeWidth = 2.5f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
            )

            // Fault point marker
            drawCircle(FaultRed, radius = 10f, center = Offset(cutX, cutY))
            drawCircle(Color.White, radius = 4f, center = Offset(cutX, cutY))

            // Text labels
            val tagPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 28f
                isFakeBoldText = true
            }
            drawContext.canvas.nativeCanvas.drawText("⚠️ CUT @ ${faultResult.clampedMeters}m", cutX + 16f, cutY + 6f, tagPaint)

            val infoPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#38BDF8")
                textSize = 24f
            }
            drawContext.canvas.nativeCanvas.drawText("🧭 Heading: ${faultResult.heading}° (${faultResult.compassDir})", 24f, 40f, infoPaint)

            val landmarkPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#FCD34D")
                textSize = 22f
            }
            drawContext.canvas.nativeCanvas.drawText("📍 Landmark: ${faultResult.refLandmark.label} (${faultResult.metersFromLandmark}m away)", 24f, H - 24f, landmarkPaint)
        }
    }
}
