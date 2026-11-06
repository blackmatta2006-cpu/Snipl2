package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DefaultData
import com.example.data.OtdrCalculator
import com.example.model.AppScreen
import com.example.model.ChatMessage
import com.example.model.FaultResult
import com.example.model.FaultTicket
import com.example.model.OfcRoute
import com.example.model.PointType
import com.example.model.RoutePoint
import com.example.model.StaffMember
import com.example.model.TicketStatus
import com.example.model.UserRole
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class FiberGridViewModel : ViewModel() {

    private val _currentScreen = MutableStateFlow(AppScreen.HUB)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentRole = MutableStateFlow(UserRole.SPLICER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _routes = MutableStateFlow(DefaultData.routes)
    val routes: StateFlow<List<OfcRoute>> = _routes.asStateFlow()

    private val _activeRouteId = MutableStateFlow("route-1")
    val activeRouteId: StateFlow<String> = _activeRouteId.asStateFlow()

    private val _currentFaultDistance = MutableStateFlow(1450)
    val currentFaultDistance: StateFlow<Int> = _currentFaultDistance.asStateFlow()

    private val _direction = MutableStateFlow("forward")
    val direction: StateFlow<String> = _direction.asStateFlow()

    private val _slackMode = MutableStateFlow("optical")
    val slackMode: StateFlow<String> = _slackMode.asStateFlow()

    private val _cableSlackPct = MutableStateFlow(3.0)
    val cableSlackPct: StateFlow<Double> = _cableSlackPct.asStateFlow()

    private val _faultResult = MutableStateFlow(
        OtdrCalculator.locateFault(
            DefaultData.routes.first(),
            1450,
            "forward",
            "optical",
            3.0
        )
    )
    val faultResult: StateFlow<FaultResult> = _faultResult.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _faultTickets = MutableStateFlow(DefaultData.faultTickets)
    val faultTickets: StateFlow<List<FaultTicket>> = _faultTickets.asStateFlow()

    private val _staffMembers = MutableStateFlow(DefaultData.staffMembers)
    val staffMembers: StateFlow<List<StaffMember>> = _staffMembers.asStateFlow()

    // Auto-Drive state
    private val _isDriving = MutableStateFlow(false)
    val isDriving: StateFlow<Boolean> = _isDriving.asStateFlow()

    private val _isRealGps = MutableStateFlow(true)
    val isRealGps: StateFlow<Boolean> = _isRealGps.asStateFlow()

    private val _gpsStatusText = MutableStateFlow("Standby (Ready to Drive)")
    val gpsStatusText: StateFlow<String> = _gpsStatusText.asStateFlow()

    private val _gpsAccuracy = MutableStateFlow(0f)
    val gpsAccuracy: StateFlow<Float> = _gpsAccuracy.asStateFlow()

    private val _autoIntervalMeters = MutableStateFlow(15)
    val autoIntervalMeters: StateFlow<Int> = _autoIntervalMeters.asStateFlow()

    private val _currentGpsLat = MutableStateFlow<Double?>(null)
    val currentGpsLat: StateFlow<Double?> = _currentGpsLat.asStateFlow()

    private val _currentGpsLng = MutableStateFlow<Double?>(null)
    val currentGpsLng: StateFlow<Double?> = _currentGpsLng.asStateFlow()

    private val _drivePoints = MutableStateFlow<List<RoutePoint>>(emptyList())
    val drivePoints: StateFlow<List<RoutePoint>> = _drivePoints.asStateFlow()

    private val _driveOdometer = MutableStateFlow(0.0)
    val driveOdometer: StateFlow<Double> = _driveOdometer.asStateFlow()

    private val _driveSpeedKmh = MutableStateFlow(0.0)
    val driveSpeedKmh: StateFlow<Double> = _driveSpeedKmh.asStateFlow()

    private var driveSimJob: Job? = null

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        // Initial bot greeting
        val initialResult = _faultResult.value
        _chatMessages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                isUser = false,
                text = "ನಮಸ್ಕಾರ! 👋 FiberGrid OTDR Bot ರೆಡಿ ಇದೆ. ಕೇಬಲ್ ಎಷ್ಟು ಮೀಟರ್ ಕಟ್ ಆಗಿದೆ ಎಂದು ಇಲ್ಲಿ ಟೈಪ್ ಮಾಡಿ (ಉದಾ: \"1450 meter cut agide\" ಅಥವಾ \"2.65 km fault\"). ತಕ್ಷಣವೇ Google Maps ಮತ್ತು 360° Street View ಲಿಂಕ್ ಸಿಗುತ್ತದೆ.",
                timestamp = currentTimeFormatted(),
                faultResult = initialResult
            )
        )
    }

    private fun currentTimeFormatted(): String {
        return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    fun getActiveRoute(): OfcRoute {
        return _routes.value.find { it.id == _activeRouteId.value } ?: _routes.value.first()
    }

    fun recalculateFault(meters: Int = _currentFaultDistance.value) {
        val route = getActiveRoute()
        val result = OtdrCalculator.locateFault(
            route = route,
            targetMeters = meters,
            direction = _direction.value,
            slackMode = _slackMode.value,
            cableSlackPct = _cableSlackPct.value
        )
        _currentFaultDistance.value = result.clampedMeters
        _faultResult.value = result
    }

    fun setFaultDistance(meters: Int) {
        recalculateFault(meters)
    }

    fun setActiveRoute(routeId: String) {
        _activeRouteId.value = routeId
        recalculateFault(_currentFaultDistance.value)
    }

    fun setDirection(dir: String) {
        _direction.value = dir
        recalculateFault(_currentFaultDistance.value)
    }

    fun setSlackMode(mode: String) {
        _slackMode.value = mode
        recalculateFault(_currentFaultDistance.value)
    }

    fun setCableSlackPct(pct: Double) {
        _cableSlackPct.value = pct
        recalculateFault(_currentFaultDistance.value)
    }

    fun setUserRole(role: UserRole) {
        _currentRole.value = role
        viewModelScope.launch {
            _toastEvent.emit("${role.title} ಸಕ್ರಿಯವಾಗಿದೆ!")
        }
    }

    fun navigateTo(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setAutoInterval(meters: Int) {
        _autoIntervalMeters.value = meters
    }

    fun setIsRealGps(real: Boolean) {
        _isRealGps.value = real
    }

    fun setGpsStatus(status: String) {
        _gpsStatusText.value = status
    }

    // Real GPS location update received from device sensor
    fun onRealLocationUpdate(lat: Double, lng: Double, speedKmh: Double, accuracy: Float) {
        _currentGpsLat.value = lat
        _currentGpsLng.value = lng
        _gpsAccuracy.value = accuracy

        if (!_isDriving.value) return

        _driveSpeedKmh.value = speedKmh
        val pts = _drivePoints.value

        if (pts.isEmpty()) {
            // First point: Create start OLT node
            val startNode = RoutePoint(
                lat = lat,
                lng = lng,
                label = "Driven OLT Start POP",
                type = PointType.OLT,
                slackMeters = 20
            )
            _drivePoints.value = listOf(startNode)
            _gpsStatusText.value = "🟢 GPS Locked (Start Point Marked)"
            viewModelScope.launch {
                _toastEvent.emit("🟢 ಮೊದಲ GPS ಪಾಯಿಂಟ್ ದಾಖಲಾಗಿದೆ! ವಾಹನ ಚಲಿಸಿ...")
            }
        } else {
            val last = pts.last()
            val dist = OtdrCalculator.haversineMeters(last.lat, last.lng, lat, lng)
            val threshold = _autoIntervalMeters.value.toDouble()

            if (dist >= threshold) {
                val nextIndex = pts.size + 1
                val newPole = RoutePoint(
                    lat = lat,
                    lng = lng,
                    label = "Auto GPS Pole #$nextIndex",
                    type = PointType.POLE,
                    slackMeters = 0
                )
                _drivePoints.update { it + newPole }
                _driveOdometer.update { it + dist }
                _gpsStatusText.value = "🟢 GPS Active • Driven: ${_driveOdometer.value.toInt()}m (${_drivePoints.value.size} pts)"
            }
        }
    }

    fun startRealGpsDrive() {
        stopDrive()
        _isRealGps.value = true
        _isDriving.value = true
        _drivePoints.value = emptyList()
        _driveOdometer.value = 0.0
        _driveSpeedKmh.value = 0.0
        _gpsStatusText.value = "🛰️ Acquiring Real Road GPS..."
        viewModelScope.launch {
            _toastEvent.emit("🛰️ Real GPS Drive ಸಕ್ರಿಯವಾಗಿದೆ! ರಸ್ತೆಯಲ್ಲಿ ಚಲಿಸಿದಂತೆ ರೂಟ್ ಆಟೋ-ಡ್ರಾ ಆಗುತ್ತದೆ.")
        }
    }

    // Auto-Drive GPS Simulation & Recording (for testing/indoor)
    fun startDriveSimulation() {
        stopDrive()
        _isRealGps.value = false
        _isDriving.value = true
        _drivePoints.value = emptyList()
        _driveOdometer.value = 0.0
        _driveSpeedKmh.value = 28.0
        _gpsStatusText.value = "🚙 Simulated Road Drive Active"

        val simWaypoints = listOf(
            Pair(12.9756, 77.6066),
            Pair(12.9772, 77.6031),
            Pair(12.9791, 77.5993),
            Pair(12.9818, 77.5956),
            Pair(12.9852, 77.5924),
            Pair(12.9891, 77.5895),
            Pair(12.9935, 77.5876),
            Pair(12.9982, 77.5858),
            Pair(13.0031, 77.5842),
            Pair(13.0085, 77.5829)
        )

        driveSimJob = viewModelScope.launch {
            _toastEvent.emit("Auto-Drive Simulation ಪ್ರಾರಂಭವಾಗಿದೆ! ವಾಹನ ಚಲಿಸಿದಂತೆ ರೂಟ್ ಆಟೋ-ಡ್ರಾ ಆಗುತ್ತದೆ...")
            for (i in simWaypoints.indices) {
                val (lat, lng) = simWaypoints[i]
                val isJc = i == 3 || i == 6
                val type = when {
                    i == 0 -> PointType.OLT
                    isJc -> PointType.JC
                    else -> PointType.POLE
                }
                val label = when {
                    i == 0 -> "MG Road OLT (Start)"
                    isJc -> "JC-0${i} Highway Junction (+30m Loop)"
                    else -> "Auto GPS Pole #$i"
                }
                val slack = if (isJc) 30 else if (i == 0) 20 else 0

                recordPointInternal(lat, lng, label, type, slack, 32.0 + (i % 3) * 4)
                delay(1200)
            }
            _toastEvent.emit("ರೂಟ್ ತಲುಪಿದೆ! 'Finish & Save Route' ಕ್ಲಿಕ್ ಮಾಡಿ.")
        }
    }

    fun recordManualPoint(type: PointType, slack: Int = type.defaultSlack) {
        val currentLat = _currentGpsLat.value
        val currentLng = _currentGpsLng.value
        val last = _drivePoints.value.lastOrNull()

        val lat = currentLat ?: (last?.lat ?: 12.9800) + 0.0006
        val lng = currentLng ?: (last?.lng ?: 77.5700) + 0.0006

        val count = _drivePoints.value.size + 1
        val label = when (type) {
            PointType.OLT -> "OLT Node #$count"
            PointType.JC -> "Field JC #$count (+${slack}m Loop)"
            PointType.CHAMBER -> "Chamber #$count (+${slack}m Loop)"
            PointType.POLE -> "Pole #$count"
            PointType.END -> "End Node"
        }
        recordPointInternal(lat, lng, label, type, slack, _driveSpeedKmh.value)
        viewModelScope.launch {
            _toastEvent.emit("📍 $label ಸೇರಿಸಲಾಗಿದೆ!")
        }
    }

    private fun recordPointInternal(
        lat: Double,
        lng: Double,
        label: String,
        type: PointType,
        slack: Int,
        speedKmh: Double
    ) {
        val currentPts = _drivePoints.value
        if (currentPts.isNotEmpty()) {
            val prev = currentPts.last()
            val dist = OtdrCalculator.haversineMeters(prev.lat, prev.lng, lat, lng)
            _driveOdometer.update { it + dist }
        }
        _driveSpeedKmh.value = speedKmh
        val newPt = RoutePoint(lat, lng, label, type, slack)
        _drivePoints.update { it + newPt }
    }

    fun stopDrive() {
        driveSimJob?.cancel()
        driveSimJob = null
        _isDriving.value = false
        _driveSpeedKmh.value = 0.0
        _gpsStatusText.value = "Standby (Paused)"
    }

    fun finishAndSaveDrivenRoute(routeName: String) {
        stopDrive()
        val pts = _drivePoints.value
        if (pts.size < 2) {
            viewModelScope.launch {
                _toastEvent.emit("ಕನಿಷ್ಠ 2 GPS ಪಾಯಿಂಟ್ಸ್ ಬೇಕು!")
            }
            return
        }

        val finalName = if (routeName.isNotBlank()) routeName else "Driven Road Route (${_driveOdometer.value.toInt()}m)"
        val updatedPts = pts.toMutableList()
        val lastIdx = updatedPts.lastIndex
        updatedPts[lastIdx] = updatedPts[lastIdx].copy(type = PointType.END, label = "$finalName End Terminal")

        val newRoute = OfcRoute(
            id = "route-drive-${System.currentTimeMillis()}",
            name = finalName,
            fiberType = "24F Real Survey OFC",
            points = updatedPts
        )

        _routes.update { listOf(newRoute) + it }
        _activeRouteId.value = newRoute.id
        recalculateFault(1000)
        _currentScreen.value = AppScreen.GIS_MAP
        viewModelScope.launch {
            _toastEvent.emit("✅ \"$finalName\" ಯಶಸ್ವಿಯಾಗಿ ಸೇವ್ ಆಗಿದೆ ಮತ್ತು ಮ್ಯಾಪ್‌ನಲ್ಲಿ ತೆರೆಯಲಾಗಿದೆ!")
        }
    }

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = true,
            text = userText,
            timestamp = currentTimeFormatted()
        )
        _chatMessages.update { it + userMsg }

        val parsed = OtdrCalculator.parseMessage(userText)

        if (parsed.detectedRouteId != null && parsed.detectedRouteId != _activeRouteId.value) {
            _activeRouteId.value = parsed.detectedRouteId
        }

        if (parsed.detectedDirection != null) {
            _direction.value = parsed.detectedDirection
        }

        if (parsed.distanceMeters == null) {
            val helpMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                isUser = false,
                text = "⚠️ ದಯವಿಟ್ಟು ಕಟ್ ಆದ ಮೀಟರ್ ಅಥವಾ ಕಿ.ಮೀ ತಿಳಿಸಿ (ಉದಾ: \"1450 meter cut agide\" ಅಥವಾ \"850m fault\").",
                timestamp = currentTimeFormatted()
            )
            _chatMessages.update { it + helpMsg }
            return
        }

        recalculateFault(parsed.distanceMeters)
        val res = _faultResult.value

        val newTicket = FaultTicket(
            id = "TKT-${(100..999).random()}",
            routeName = getActiveRoute().name,
            otdrMeters = res.clampedMeters,
            landmark = res.refLandmark.label,
            metersAway = res.metersFromLandmark,
            lat = res.lat,
            lng = res.lng,
            heading = res.heading,
            status = TicketStatus.OPEN_CUT,
            reportedBy = "${_currentRole.value.title} ($userText)",
            time = currentTimeFormatted()
        )
        _faultTickets.update { listOf(newTicket) + it }

        val botReply = ChatMessage(
            id = UUID.randomUUID().toString(),
            isUser = false,
            text = "✅ ಕಟ್ ಆದ ಜಾಗ ಪತ್ತೆಯಾಗಿದೆ! ${res.refLandmark.label} ಇಂದ ${res.metersFromLandmark} ಮೀಟರ್ ಅಂತರದಲ್ಲಿದೆ. OTDR: ${res.clampedMeters}m (${res.direction.uppercase()}).",
            timestamp = currentTimeFormatted(),
            faultResult = res
        )
        _chatMessages.update { it + botReply }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                id = UUID.randomUUID().toString(),
                isUser = false,
                text = "ಚಾಟ್ ಕ್ಲಿಯರ್ ಆಗಿದೆ. ಹೊಸ OTDR ಮೀಟರ್ ರೀಡಿಂಗ್ ಟೈಪ್ ಮಾಡಿ ಕಳಿಸಿ (ಉದಾ: 950m cut).",
                timestamp = currentTimeFormatted()
            )
        )
    }

    fun toggleTicketStatus(ticketId: String) {
        _faultTickets.update { list ->
            list.map { tkt ->
                if (tkt.id == ticketId) {
                    val nextStatus = when (tkt.status) {
                        TicketStatus.OPEN_CUT -> TicketStatus.IN_PROGRESS
                        TicketStatus.IN_PROGRESS -> TicketStatus.RESOLVED
                        TicketStatus.RESOLVED -> TicketStatus.OPEN_CUT
                    }
                    tkt.copy(status = nextStatus)
                } else {
                    tkt
                }
            }
        }
    }

    fun addStaffMember(name: String, phone: String, role: String) {
        val newStaff = StaffMember(
            id = "st-${System.currentTimeMillis()}",
            name = name,
            phone = phone,
            role = role,
            status = "Active"
        )
        _staffMembers.update { listOf(newStaff) + it }
        viewModelScope.launch {
            _toastEvent.emit("ಸಿಬ್ಬಂದಿ \"$name\" ($role) ಸೇರಿಸಲಾಗಿದೆ!")
        }
    }

    fun importKmlOrCoordinates(rawText: String): Boolean {
        return try {
            val coordsPattern = Regex("<coordinates>([\\s\\S]*?)</coordinates>", RegexOption.IGNORE_CASE)
            val match = coordsPattern.find(rawText)
            val coordText = match?.groupValues?.get(1) ?: rawText

            val lines = coordText.trim().split(Regex("[\\s\n\r]+"))
            val pts = mutableListOf<RoutePoint>()

            for (i in lines.indices) {
                val parts = lines[i].split(",")
                if (parts.size >= 2) {
                    val lng = parts[0].toDoubleOrNull() ?: continue
                    val lat = parts[1].toDoubleOrNull() ?: continue
                    val type = when (i) {
                        0 -> PointType.OLT
                        lines.lastIndex -> PointType.END
                        else -> if (i % 3 == 0) PointType.JC else PointType.POLE
                    }
                    val label = when (type) {
                        PointType.OLT -> "Imported KML OLT"
                        PointType.END -> "Imported KML End"
                        PointType.JC -> "KML Joint Closure #$i"
                        else -> "KML Pole #$i"
                    }
                    val slack = if (type == PointType.JC) 30 else if (type == PointType.OLT) 20 else 0
                    pts.add(RoutePoint(lat, lng, label, type, slack))
                }
            }

            if (pts.size >= 2) {
                val importedRoute = OfcRoute(
                    id = "route-kml-${System.currentTimeMillis()}",
                    name = "Imported KML Route (${pts.size} Nodes)",
                    fiberType = "Imported GIS KML OFC",
                    points = pts
                )
                _routes.update { listOf(importedRoute) + it }
                _activeRouteId.value = importedRoute.id
                recalculateFault(500)
                viewModelScope.launch {
                    _toastEvent.emit("KML ರೂಟ್ ಯಶಸ್ವಿಯಾಗಿ ಆಮದಾಗಿದೆ (${pts.size} ಪಾಯಿಂಟ್ಸ್)!")
                }
                true
            } else {
                viewModelScope.launch {
                    _toastEvent.emit("ಕನಿಷ್ಠ 2 ಮಾನ್ಯವಾದ ಕೋಆರ್ಡಿನೇಟ್ಸ್ ಇರಬೇಕು.")
                }
                false
            }
        } catch (e: Exception) {
            viewModelScope.launch {
                _toastEvent.emit("ದೋಷ: ${e.localizedMessage}")
            }
            false
        }
    }
}
