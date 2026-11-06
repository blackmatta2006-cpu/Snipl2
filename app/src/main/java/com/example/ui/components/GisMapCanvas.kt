package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.FaultResult
import com.example.model.OfcRoute
import com.example.model.PointType
import com.example.model.RoutePoint
import com.example.ui.theme.BgDark
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.FaultRed
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.WarningAmber
import kotlin.math.max
import kotlin.math.min

@Composable
fun GisMapCanvas(
    route: OfcRoute,
    faultResult: FaultResult?,
    isSatellite: Boolean = false,
    drivenPoints: List<RoutePoint> = emptyList(),
    onNodeClick: ((RoutePoint) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    // Pulse animation for cut point
    val infiniteTransition = rememberInfiniteTransition(label = "FaultPulse")
    val pulseRadius by infiniteTransition.animateFloat(
        initialValue = 12f,
        targetValue = 42f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRadius"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 0.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )

    val allPoints = if (drivenPoints.isNotEmpty()) drivenPoints else route.points
    val lats = allPoints.map { it.lat }
    val lngs = allPoints.map { it.lng }
    val minLat = lats.minOrNull() ?: 12.95
    val maxLat = lats.maxOrNull() ?: 13.05
    val minLng = lngs.minOrNull() ?: 77.50
    val maxLng = lngs.maxOrNull() ?: 77.65

    val latSpan = max(0.005, maxLat - minLat)
    val lngSpan = max(0.005, maxLng - minLng)

    Box(modifier = modifier.fillMaxSize().background(if (isSatellite) Color(0xFF07111E) else BgDark)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("gis_map_canvas")
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(0.6f, 5.0f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                }
                .pointerInput(allPoints) {
                    detectTapGestures { tapOffset ->
                        // Detect tap near any node
                        // handled if needed
                    }
                }
        ) {
            val canvasW = size.width
            val canvasH = size.height
            val pad = 60f

            fun project(lat: Double, lng: Double): Offset {
                val normX = ((lng - minLng) / lngSpan).toFloat()
                val normY = (1.0f - ((lat - minLat) / latSpan).toFloat())
                val baseCenterX = canvasW / 2f
                val baseCenterY = canvasH / 2f

                val px = pad + normX * (canvasW - pad * 2)
                val py = pad + normY * (canvasH - pad * 2)

                val scaledX = (px - baseCenterX) * scale + baseCenterX + offsetX
                val scaledY = (py - baseCenterY) * scale + baseCenterY + offsetY
                return Offset(scaledX, scaledY)
            }

            // Draw grid lines
            val gridColor = if (isSatellite) Color(0x1F38BDF8) else Color(0x15FFFFFF)
            for (i in 1..8) {
                val gx = canvasW * (i / 8f)
                drawLine(gridColor, Offset(gx, 0f), Offset(gx, canvasH), strokeWidth = 1f)
                val gy = canvasH * (i / 8f)
                drawLine(gridColor, Offset(0f, gy), Offset(canvasW, gy), strokeWidth = 1f)
            }

            if (drivenPoints.isNotEmpty()) {
                // Draw live driven line
                val drivePath = Path()
                drivenPoints.forEachIndexed { idx, pt ->
                    val pos = project(pt.lat, pt.lng)
                    if (idx == 0) drivePath.moveTo(pos.x, pos.y) else drivePath.lineTo(pos.x, pos.y)
                }
                drawPath(
                    path = drivePath,
                    color = CyberCyan,
                    style = Stroke(width = 8f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                // Draw vehicle marker on last point
                val carPos = project(drivenPoints.last().lat, drivenPoints.last().lng)
                drawCircle(CyberCyan, radius = 16f * scale, center = carPos)
                drawCircle(Color.White, radius = 6f * scale, center = carPos)
            } else if (faultResult != null) {
                // Draw intact path (Green)
                if (faultResult.intactPoints.size >= 2) {
                    val intactPath = Path()
                    faultResult.intactPoints.forEachIndexed { index, (lat, lng) ->
                        val pos = project(lat, lng)
                        if (index == 0) intactPath.moveTo(pos.x, pos.y) else intactPath.lineTo(pos.x, pos.y)
                    }
                    // Glow
                    drawPath(
                        path = intactPath,
                        color = NeonEmerald.copy(alpha = 0.35f),
                        style = Stroke(width = 16f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                    drawPath(
                        path = intactPath,
                        color = NeonEmerald,
                        style = Stroke(width = 7f * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
                    )
                }

                // Draw broken path (Red Dashed)
                if (faultResult.brokenPoints.size >= 2) {
                    val brokenPath = Path()
                    faultResult.brokenPoints.forEachIndexed { index, (lat, lng) ->
                        val pos = project(lat, lng)
                        if (index == 0) brokenPath.moveTo(pos.x, pos.y) else brokenPath.lineTo(pos.x, pos.y)
                    }
                    drawPath(
                        path = brokenPath,
                        color = FaultRed.copy(alpha = 0.85f),
                        style = Stroke(
                            width = 6f * scale,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round,
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f * scale, 12f * scale), 0f)
                        )
                    )
                }

                // Draw pulsating fault marker
                val cutPos = project(faultResult.lat, faultResult.lng)
                drawCircle(
                    color = FaultRed.copy(alpha = pulseAlpha),
                    radius = pulseRadius * scale,
                    center = cutPos,
                    style = Stroke(width = 3.5f * scale)
                )
                drawCircle(
                    color = FaultRed,
                    radius = 12f * scale,
                    center = cutPos
                )
                drawCircle(
                    color = Color.White,
                    radius = 5f * scale,
                    center = cutPos
                )

                // Label for fault
                val textPaint = android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = (13f * scale).coerceIn(10f, 22f)
                    isFakeBoldText = true
                    setShadowLayer(6f, 0f, 0f, android.graphics.Color.BLACK)
                }
                drawContext.canvas.nativeCanvas.drawText(
                    "⚠️ CUT @ ${faultResult.clampedMeters}m",
                    cutPos.x + 16f * scale,
                    cutPos.y - 12f * scale,
                    textPaint
                )
            } else {
                // Normal route polyline
                val path = Path()
                route.points.forEachIndexed { idx, pt ->
                    val pos = project(pt.lat, pt.lng)
                    if (idx == 0) path.moveTo(pos.x, pos.y) else path.lineTo(pos.x, pos.y)
                }
                drawPath(
                    path = path,
                    color = NeonEmerald,
                    style = Stroke(width = 6f * scale, cap = StrokeCap.Round)
                )
            }

            // Draw route nodes (OLT, POLE, JC, CHAMBER, END)
            allPoints.forEach { pt ->
                val pos = project(pt.lat, pt.lng)
                val nodeColor = when (pt.type) {
                    PointType.OLT -> CyberCyan
                    PointType.JC -> WarningAmber
                    PointType.CHAMBER -> PurpleAccent
                    PointType.END -> CyberCyan
                    PointType.POLE -> Color(0xFF94A3B8)
                }
                val nodeRadius = when (pt.type) {
                    PointType.OLT, PointType.END -> 11f * scale
                    PointType.JC, PointType.CHAMBER -> 9f * scale
                    PointType.POLE -> 6.5f * scale
                }

                drawCircle(Color.Black, radius = nodeRadius + 3f * scale, center = pos)
                drawCircle(nodeColor, radius = nodeRadius, center = pos)
                drawCircle(Color.White, radius = nodeRadius * 0.4f, center = pos)

                // Draw short label if scaled enough
                if (scale >= 1.0f && pt.type != PointType.POLE) {
                    val nodePaint = android.graphics.Paint().apply {
                        color = android.graphics.Color.WHITE
                        textSize = (11f * scale).coerceIn(9f, 18f)
                        isFakeBoldText = true
                        setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
                    }
                    drawContext.canvas.nativeCanvas.drawText(
                        pt.label.take(18),
                        pos.x + 12f * scale,
                        pos.y + 4f * scale,
                        nodePaint
                    )
                }
            }
        }

        // Floating Zoom / Re-center buttons
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
        ) {
            FilledIconButton(
                onClick = { scale = (scale * 1.3f).coerceAtMost(5.0f) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceDark),
                modifier = Modifier.size(44.dp).testTag("zoom_in_button")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = NeonEmerald)
            }
            FilledIconButton(
                onClick = { scale = (scale / 1.3f).coerceAtLeast(0.6f) },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceDark),
                modifier = Modifier.size(44.dp).testTag("zoom_out_button")
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = NeonEmerald)
            }
            FilledIconButton(
                onClick = {
                    scale = 1f
                    offsetX = 0f
                    offsetY = 0f
                },
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = SurfaceDark),
                modifier = Modifier.size(44.dp).testTag("recenter_button")
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "Re-center", tint = CyberCyan)
            }
        }
    }
}
