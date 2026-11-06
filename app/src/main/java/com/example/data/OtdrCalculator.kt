package com.example.data

import com.example.model.FaultResult
import com.example.model.OfcRoute
import com.example.model.PointType
import com.example.model.RouteAnalysis
import com.example.model.RoutePoint
import com.example.model.RouteSegment
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.math.sqrt

object OtdrCalculator {

    fun haversineMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }

    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Int {
        val dLon = Math.toRadians(lon2 - lon1)
        val rLat1 = Math.toRadians(lat1)
        val rLat2 = Math.toRadians(lat2)
        val y = sin(dLon) * cos(rLat2)
        val x = cos(rLat1) * sin(rLat2) - sin(rLat1) * cos(rLat2) * cos(dLon)
        val deg = Math.toDegrees(atan2(y, x))
        return ((deg + 360) % 360).roundToInt()
    }

    fun bearingToCompass(deg: Int): String {
        val dirs = arrayOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val idx = ((deg + 22.5) / 45.0).toInt() % 8
        return dirs[idx]
    }

    fun buildGoogleMapsUrl(lat: Double, lng: Double): String {
        return "https://www.google.com/maps?q=$lat,$lng"
    }

    fun buildStreetViewUrl(lat: Double, lng: Double, heading: Int = 0): String {
        return "https://www.google.com/maps/@?api=1&map_action=pano&viewpoint=$lat,$lng&heading=$heading&pitch=0"
    }

    fun analyzeRoute(
        route: OfcRoute,
        direction: String = "forward",
        slackMode: String = "optical",
        cableSlackPct: Double = 3.0
    ): RouteAnalysis {
        val rawPts = if (direction == "reverse") route.points.reversed() else route.points
        val helixMultiplier = if (slackMode == "optical") 1.0 + (cableSlackPct / 100.0) else 1.0

        val segments = mutableListOf<RouteSegment>()
        var totalGroundMeters = 0.0
        var totalOpticalMeters = 0.0

        for (i in rawPts.indices) {
            val pt = rawPts[i]
            val nodeSlack = if (slackMode == "optical") pt.slackMeters.toDouble() else 0.0

            if (i == 0) {
                totalOpticalMeters += nodeSlack
                segments.add(
                    RouteSegment(
                        index = 0,
                        fromPoint = null,
                        point = pt,
                        segGround = 0.0,
                        spanOptical = nodeSlack,
                        nodeSlack = nodeSlack,
                        cumGroundStart = 0.0,
                        cumGroundEnd = 0.0,
                        cumOpticalStart = 0.0,
                        cumOpticalEnd = totalOpticalMeters
                    )
                )
            } else {
                val prev = rawPts[i - 1]
                val gDist = haversineMeters(prev.lat, prev.lng, pt.lat, pt.lng)
                val spanOptical = gDist * helixMultiplier
                val cumGStart = totalGroundMeters
                val cumOStart = totalOpticalMeters

                totalGroundMeters += gDist
                totalOpticalMeters += spanOptical + nodeSlack

                segments.add(
                    RouteSegment(
                        index = i,
                        fromPoint = prev,
                        point = pt,
                        segGround = gDist,
                        spanOptical = spanOptical,
                        nodeSlack = nodeSlack,
                        cumGroundStart = cumGStart,
                        cumGroundEnd = totalGroundMeters,
                        cumOpticalStart = cumOStart,
                        cumOpticalEnd = totalOpticalMeters
                    )
                )
            }
        }

        return RouteAnalysis(
            orderedPoints = rawPts,
            segments = segments,
            totalGroundMeters = totalGroundMeters,
            totalOpticalMeters = totalOpticalMeters
        )
    }

    fun locateFault(
        route: OfcRoute,
        targetMeters: Int,
        direction: String = "forward",
        slackMode: String = "optical",
        cableSlackPct: Double = 3.0
    ): FaultResult {
        val analysis = analyzeRoute(route, direction, slackMode, cableSlackPct)
        val maxDist = if (slackMode == "optical") analysis.totalOpticalMeters else analysis.totalGroundMeters
        val clampedDist = max(0.0, min(targetMeters.toDouble(), maxDist))

        var faultLat = analysis.orderedPoints.first().lat
        var faultLng = analysis.orderedPoints.first().lng
        var splitSegmentIndex = 0
        var refLandmark = analysis.orderedPoints.first()
        var metersFromLandmark = 0
        var insideLoopClosure = false

        for (i in 1 until analysis.segments.size) {
            val seg = analysis.segments[i]
            val startVal = if (slackMode == "optical") seg.cumOpticalStart else seg.cumGroundStart
            val endVal = if (slackMode == "optical") seg.cumOpticalEnd else seg.cumGroundEnd

            if (clampedDist <= endVal || i == analysis.segments.lastIndex) {
                splitSegmentIndex = i
                val intoSeg = max(0.0, clampedDist - startVal)
                val spanLen = if (slackMode == "optical") seg.spanOptical else seg.segGround

                if (intoSeg >= spanLen && seg.nodeSlack > 0) {
                    faultLat = seg.point.lat
                    faultLng = seg.point.lng
                    refLandmark = seg.point
                    metersFromLandmark = 0
                    insideLoopClosure = true
                } else {
                    val ratio = if (spanLen > 0) min(1.0, intoSeg / spanLen) else 0.0
                    val prev = seg.fromPoint ?: seg.point
                    faultLat = prev.lat + (seg.point.lat - prev.lat) * ratio
                    faultLng = prev.lng + (seg.point.lng - prev.lng) * ratio

                    val distFromPrevGround = seg.segGround * ratio
                    val distToNextGround = seg.segGround * (1.0 - ratio)
                    if (distFromPrevGround <= distToNextGround) {
                        refLandmark = prev
                        metersFromLandmark = distFromPrevGround.roundToInt()
                    } else {
                        refLandmark = seg.point
                        metersFromLandmark = distToNextGround.roundToInt()
                    }
                }
                break
            }
        }

        val intactPts = mutableListOf<Pair<Double, Double>>()
        val brokenPts = mutableListOf<Pair<Double, Double>>()

        for (i in 0..splitSegmentIndex) {
            if (i < splitSegmentIndex) {
                intactPts.add(Pair(analysis.orderedPoints[i].lat, analysis.orderedPoints[i].lng))
            } else {
                intactPts.add(Pair(faultLat, faultLng))
            }
        }
        brokenPts.add(Pair(faultLat, faultLng))
        for (i in splitSegmentIndex until analysis.orderedPoints.size) {
            brokenPts.add(Pair(analysis.orderedPoints[i].lat, analysis.orderedPoints[i].lng))
        }

        val segForHeading = analysis.segments.getOrNull(max(1, splitSegmentIndex)) ?: analysis.segments[0]
        val faultHeading = if (segForHeading.fromPoint != null) {
            calculateBearing(
                segForHeading.fromPoint.lat,
                segForHeading.fromPoint.lng,
                segForHeading.point.lat,
                segForHeading.point.lng
            )
        } else {
            0
        }

        return FaultResult(
            requestedMeters = targetMeters,
            clampedMeters = clampedDist.roundToInt(),
            maxRouteMeters = maxDist.roundToInt(),
            totalGroundMeters = analysis.totalGroundMeters.roundToInt(),
            totalOpticalMeters = analysis.totalOpticalMeters.roundToInt(),
            lat = ((faultLat * 1_000_000.0).roundToInt()) / 1_000_000.0,
            lng = ((faultLng * 1_000_000.0).roundToInt()) / 1_000_000.0,
            refLandmark = refLandmark,
            metersFromLandmark = metersFromLandmark,
            insideLoopClosure = insideLoopClosure,
            heading = faultHeading,
            compassDir = bearingToCompass(faultHeading),
            direction = direction,
            slackMode = slackMode,
            intactPoints = intactPts,
            brokenPoints = brokenPts,
            analysis = analysis
        )
    }

    data class ParsedMessage(
        val distanceMeters: Int?,
        val detectedRouteId: String?,
        val detectedDirection: String?
    )

    fun parseMessage(text: String): ParsedMessage {
        val clean = text.trim()
        val lower = clean.lowercase()

        var detectedRouteId: String? = null
        if (Regex("route\\s*1|r1|ರೂಟ್\\s*1|majestic|rajajinagar|yeshwanthpur", RegexOption.IGNORE_CASE).containsMatchIn(clean)) {
            detectedRouteId = "route-1"
        } else if (Regex("route\\s*2|r2|ರೂಟ್\\s*2|indiranagar|domlur|koramangala", RegexOption.IGNORE_CASE).containsMatchIn(clean)) {
            detectedRouteId = "route-2"
        } else if (Regex("route\\s*3|r3|ರೂಟ್\\s*3|peenya|jalahalli|dasarahalli", RegexOption.IGNORE_CASE).containsMatchIn(clean)) {
            detectedRouteId = "route-3"
        }

        var detectedDirection: String? = null
        if (Regex("reverse|ultha|ulta|end\\s*inda|from\\s*end|ಉಲ್ಟಾ|ಕೊನೆಯಿಂದ", RegexOption.IGNORE_CASE).containsMatchIn(clean)) {
            detectedDirection = "reverse"
        } else if (Regex("from\\s*olt|olt\\s*inda|forward|start", RegexOption.IGNORE_CASE).containsMatchIn(clean)) {
            detectedDirection = "forward"
        }

        val stripped = lower.replace(Regex("route\\s*\\d+|r\\d+|ರೂಟ್\\s*\\d+"), " ")
        val kmMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*(?:km|kilometer|kilometre|ಕಿ\\.?ಮೀ|ಕಿಲೋಮೀಟರ್)").find(stripped)
        val mMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*(?:m|mtr|mtrs|meter|meters|metre|ಮೀ|ಮೀಟರ್)?").find(stripped)

        var distanceMeters: Int? = null
        if (kmMatch != null) {
            val kmVal = kmMatch.groupValues[1].toDoubleOrNull()
            if (kmVal != null) distanceMeters = (kmVal * 1000.0).roundToInt()
        } else if (mMatch != null) {
            val valStr = mMatch.groupValues[1]
            val parsedVal = valStr.toDoubleOrNull()
            if (parsedVal != null) {
                distanceMeters = if (stripped.contains(".") && parsedVal < 25 && !Regex("\\b(m|meter|mtr)\\b").containsMatchIn(stripped)) {
                    (parsedVal * 1000.0).roundToInt()
                } else {
                    parsedVal.roundToInt()
                }
            }
        }

        return ParsedMessage(
            distanceMeters = distanceMeters,
            detectedRouteId = detectedRouteId,
            detectedDirection = detectedDirection
        )
    }
}
