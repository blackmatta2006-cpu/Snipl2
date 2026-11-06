package com.example

import com.example.data.DefaultData
import com.example.data.OtdrCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testParseSplicerMessages() {
        val parsed1 = OtdrCalculator.parseMessage("1450 meter cut agide")
        assertEquals(1450, parsed1.distanceMeters)

        val parsed2 = OtdrCalculator.parseMessage("OTDR 820m fault")
        assertEquals(820, parsed2.distanceMeters)

        val parsed3 = OtdrCalculator.parseMessage("2.65 km cut from OLT")
        assertEquals(2650, parsed3.distanceMeters)

        val parsed4 = OtdrCalculator.parseMessage("Reverse 950m cut")
        assertEquals(950, parsed4.distanceMeters)
        assertEquals("reverse", parsed4.detectedDirection)

        val parsed5 = OtdrCalculator.parseMessage("Route 2 1.2km")
        assertEquals(1200, parsed5.distanceMeters)
        assertEquals("route-2", parsed5.detectedRouteId)
    }

    @Test
    fun testFaultLocalizationMath() {
        val route = DefaultData.routes.first()
        val fault = OtdrCalculator.locateFault(route, 1450, "forward", "optical", 3.0)

        assertEquals(1450, fault.clampedMeters)
        assertTrue("Fault lat should be near Bangalore route", fault.lat in 12.9..13.1)
        assertTrue("Fault lng should be near Bangalore route", fault.lng in 77.5..77.6)
        assertNotNull(fault.refLandmark)
        assertTrue("Intact path should have points", fault.intactPoints.isNotEmpty())
        assertTrue("Broken path should have points", fault.brokenPoints.isNotEmpty())
    }

    @Test
    fun testBearingAndCompass() {
        val bearingNorth = OtdrCalculator.calculateBearing(12.0, 77.0, 13.0, 77.0)
        val compass = OtdrCalculator.bearingToCompass(bearingNorth)
        assertEquals("N", compass)
    }
}
