package com.example.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat

class RealLocationTracker(
    private val context: Context,
    private val onLocationChanged: (lat: Double, lng: Double, speedKmh: Double, accuracy: Float) -> Unit,
    private val onStatusChanged: (status: String) -> Unit
) {
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
    private var isListening = false

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val speedKmh = if (location.hasSpeed()) location.speed * 3.6 else 0.0
            onLocationChanged(location.latitude, location.longitude, speedKmh, location.accuracy)
        }

        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {
            onStatusChanged("🟢 GPS Provider Enabled ($provider)")
        }
        override fun onProviderDisabled(provider: String) {
            onStatusChanged("⚠️ GPS Provider Disabled ($provider)")
        }
    }

    fun hasPermissions(): Boolean {
        val fine = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (!hasPermissions()) {
            onStatusChanged("⚠️ Location Permission Required")
            return
        }

        if (locationManager == null) {
            onStatusChanged("⚠️ GPS Location Service Unavailable")
            return
        }

        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            onStatusChanged("⚠️ ದಯವಿಟ್ಟು ಫೋನ್‌ನಲ್ಲಿ GPS / Location On ಮಾಡಿ")
            return
        }

        // Check last known location immediately
        val lastGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        val lastNet = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        val bestLast = lastGps ?: lastNet
        if (bestLast != null) {
            val speedKmh = if (bestLast.hasSpeed()) bestLast.speed * 3.6 else 0.0
            onLocationChanged(bestLast.latitude, bestLast.longitude, speedKmh, bestLast.accuracy)
        }

        try {
            if (isGpsEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    1000L, // 1 second
                    2.0f,  // 2 meters
                    locationListener
                )
            }
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    1500L,
                    3.0f,
                    locationListener
                )
            }
            isListening = true
            onStatusChanged("🛰️ Real GPS Tracking Active (ಡ್ರೈವ್ ಮಾಡಿ...)")
        } catch (e: SecurityException) {
            onStatusChanged("⚠️ Permission Denied: ${e.message}")
        }
    }

    fun stopTracking() {
        if (isListening && locationManager != null) {
            try {
                locationManager.removeUpdates(locationListener)
            } catch (e: Exception) {
                // ignore
            }
            isListening = false
            onStatusChanged("Standby (GPS Paused)")
        }
    }
}
