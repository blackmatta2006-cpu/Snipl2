package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import com.example.model.FaultResult
import com.example.ui.theme.FaultRed
import com.example.ui.theme.NeonEmerald
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

@Composable
fun OtdrTraceCanvas(
    faultResult: FaultResult,
    onScrubDistance: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.background(Color(0xFF030712))) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("otdr_trace_canvas")
                .pointerInput(faultResult.maxRouteMeters) {
                    detectTapGestures { tapOffset ->
                        val leftPad = 60f
                        val rightPad = 40f
                        val graphW = size.width - leftPad - rightPad
                        if (tapOffset.x in leftPad..(leftPad + graphW)) {
                            val ratio = (tapOffset.x - leftPad) / graphW
                            val dist = (ratio * faultResult.maxRouteMeters).toInt()
                            onScrubDistance?.invoke(dist.coerceIn(0, faultResult.maxRouteMeters))
                        }
                    }
                }
        ) {
            val W = size.width
            val H = size.height

            val leftPad = 60f
            val rightPad = 40f
            val topPad = 30f
            val bottomPad = 45f

            val graphW = W - leftPad - rightPad
            val graphH = H - topPad - bottomPad

            // Grid lines
            val gridColor = Color(0xFF1E293B)
            val vSteps = 6
            for (i in 0..vSteps) {
                val x = leftPad + (graphW * (i / vSteps.toFloat()))
                drawLine(gridColor, Offset(x, topPad), Offset(x, topPad + graphH), strokeWidth = 1f)
                val mVal = ((faultResult.maxRouteMeters * (i / vSteps.toFloat())).toInt())
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                drawContext.canvas.nativeCanvas.drawText("${mVal}m", x, H - 12f, paint)
            }

            val hSteps = 5
            for (j in 0..hSteps) {
                val y = topPad + (graphH * (j / hSteps.toFloat()))
                drawLine(gridColor, Offset(leftPad, y), Offset(leftPad + graphW, y), strokeWidth = 1f)
                val dbVal = -(j * 6)
                val paint = android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 24f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
                drawContext.canvas.nativeCanvas.drawText("${dbVal}dB", leftPad - 10f, y + 8f, paint)
            }

            val maxMeters = max(100, faultResult.maxRouteMeters)
            val cutRatio = min(1f, faultResult.clampedMeters / maxMeters.toFloat())
            val cutX = leftPad + graphW * cutRatio

            // Waveform path (Rayleigh Backscatter + Connector Peak)
            val tracePath = Path()
            val startY = topPad + 40f
            tracePath.moveTo(leftPad, startY)
            // Initial Fresnel connector peak
            tracePath.lineTo(leftPad + 8f, topPad + 8f)
            tracePath.lineTo(leftPad + 16f, startY + 6f)

            // Attenuation slope to cut point
            val slopeDrop = 65f * cutRatio
            val preCutY = (startY + 6f) + slopeDrop
            tracePath.lineTo(cutX, preCutY)

            // Fault reflection spike
            val spikePeakY = max(topPad + 12f, preCutY - 55f)
            tracePath.lineTo(cutX + 6f, spikePeakY)

            // Sudden loss cliff
            val noiseFloorY = topPad + graphH - 15f
            tracePath.lineTo(cutX + 14f, noiseFloorY)

            // Draw intact section
            drawPath(
                path = tracePath,
                color = NeonEmerald,
                style = Stroke(width = 4.5f, cap = StrokeCap.Round)
            )

            // Draw noise floor after cut
            val noisePath = Path()
            noisePath.moveTo(cutX + 14f, noiseFloorY)
            var currX = cutX + 18f
            var step = 0
            while (currX <= leftPad + graphW) {
                val noiseY = noiseFloorY + (sin(step * 0.9f) * 6f)
                noisePath.lineTo(currX, noiseY)
                currX += 10f
                step++
            }
            drawPath(
                path = noisePath,
                color = FaultRed.copy(alpha = 0.85f),
                style = Stroke(width = 2.5f, cap = StrokeCap.Round)
            )

            // Red dot on the cut spike
            drawCircle(FaultRed, radius = 9f, center = Offset(cutX + 6f, spikePeakY))
            drawCircle(Color.White, radius = 4f, center = Offset(cutX + 6f, spikePeakY))

            // Cut Marker Tag text
            val textPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 30f
                isFakeBoldText = true
            }
            val tagText = "⚡ FAULT CUT @ ${faultResult.clampedMeters}m"
            val textX = (cutX - 80f).coerceIn(leftPad, W - 280f)
            drawContext.canvas.nativeCanvas.drawText(tagText, textX, max(topPad + 28f, spikePeakY - 14f), textPaint)
        }
    }
}
