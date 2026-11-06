package com.example.model

enum class PointType(val label: String, val defaultSlack: Int) {
    OLT("OLT Start POP", 20),
    POLE("Utility Pole", 0),
    JC("Joint Closure (JC)", 30),
    CHAMBER("Underground Chamber", 40),
    END("Terminal Node", 20)
}

data class RoutePoint(
    val lat: Double,
    val lng: Double,
    val label: String,
    val type: PointType,
    val slackMeters: Int = type.defaultSlack
)

data class OfcRoute(
    val id: String,
    val name: String,
    val fiberType: String,
    val points: List<RoutePoint>
)

data class RouteSegment(
    val index: Int,
    val fromPoint: RoutePoint?,
    val point: RoutePoint,
    val segGround: Double,
    val spanOptical: Double,
    val nodeSlack: Double,
    val cumGroundStart: Double,
    val cumGroundEnd: Double,
    val cumOpticalStart: Double,
    val cumOpticalEnd: Double
)

data class RouteAnalysis(
    val orderedPoints: List<RoutePoint>,
    val segments: List<RouteSegment>,
    val totalGroundMeters: Double,
    val totalOpticalMeters: Double
)

data class FaultResult(
    val requestedMeters: Int,
    val clampedMeters: Int,
    val maxRouteMeters: Int,
    val totalGroundMeters: Int,
    val totalOpticalMeters: Int,
    val lat: Double,
    val lng: Double,
    val refLandmark: RoutePoint,
    val metersFromLandmark: Int,
    val insideLoopClosure: Boolean,
    val heading: Int,
    val compassDir: String,
    val direction: String, // "forward" or "reverse"
    val slackMode: String, // "optical" or "ground"
    val intactPoints: List<Pair<Double, Double>>,
    val brokenPoints: List<Pair<Double, Double>>,
    val analysis: RouteAnalysis
)

data class ChatMessage(
    val id: String,
    val isUser: Boolean,
    val text: String,
    val timestamp: String,
    val faultResult: FaultResult? = null
)

enum class TicketStatus {
    OPEN_CUT,
    IN_PROGRESS,
    RESOLVED
}

data class FaultTicket(
    val id: String,
    val routeName: String,
    val otdrMeters: Int,
    val landmark: String,
    val metersAway: Int,
    val lat: Double,
    val lng: Double,
    val heading: Int,
    val status: TicketStatus,
    val reportedBy: String,
    val time: String
)

data class StaffMember(
    val id: String,
    val name: String,
    val phone: String,
    val role: String,
    val status: String
)

enum class UserRole(val title: String, val badge: String) {
    SPLICER("User / Field Splicer", "🔧 Splicer Panel"),
    ADMIN("Super Admin", "🛡️ Admin Panel"),
    SURVEYOR("GPS Field Surveyor", "🚗 Drive GPS")
}

enum class AppScreen {
    HUB,
    BOT,
    GIS_MAP,
    AUTO_DRIVE,
    TRACE,
    DISPATCH_LINKS,
    CORRIDOR_RECON,
    ROUTES_REPO,
    ADMIN_PANEL
}
