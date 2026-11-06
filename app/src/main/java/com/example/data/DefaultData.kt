package com.example.data

import com.example.model.FaultTicket
import com.example.model.OfcRoute
import com.example.model.PointType
import com.example.model.RoutePoint
import com.example.model.StaffMember
import com.example.model.TicketStatus

object DefaultData {

    val routes = listOf(
        OfcRoute(
            id = "route-1",
            name = "Route 1: Majestic OLT → Rajajinagar → Yeshwanthpur",
            fiberType = "24F Armored OFC",
            points = listOf(
                RoutePoint(12.9767, 77.5713, "Majestic OLT (Start POP)", PointType.OLT, 20),
                RoutePoint(12.9815, 77.5658, "Okalipuram Junction Pole #08", PointType.POLE, 0),
                RoutePoint(12.9882, 77.5585, "JC-01 Bashyam Circle (Rajajinagar)", PointType.JC, 30),
                RoutePoint(12.9938, 77.5552, "Rajajinagar 1st Block Pole #21", PointType.POLE, 10),
                RoutePoint(13.0012, 77.5540, "JC-02 Navarang Circle Chamber", PointType.CHAMBER, 35),
                RoutePoint(13.0108, 77.5548, "JC-03 Mahalakshmi Layout Metro", PointType.JC, 25),
                RoutePoint(13.0185, 77.5515, "Sandal Soap Factory Pole #44", PointType.POLE, 10),
                RoutePoint(13.0236, 77.5472, "Yeshwanthpur TTMC Node (End)", PointType.END, 20)
            )
        ),
        OfcRoute(
            id = "route-2",
            name = "Route 2: Indiranagar OLT → Domlur → Koramangala",
            fiberType = "48F Underground Duct OFC",
            points = listOf(
                RoutePoint(12.9784, 77.6408, "Indiranagar 100ft Rd OLT", PointType.OLT, 25),
                RoutePoint(12.9718, 77.6412, "CMH Road Signal Pole #11", PointType.POLE, 0),
                RoutePoint(12.9610, 77.6387, "JC-01 Domlur Flyover Chamber", PointType.CHAMBER, 40),
                RoutePoint(12.9532, 77.6345, "Intermediate Ring Rd Pole #29", PointType.POLE, 15),
                RoutePoint(12.9442, 77.6288, "JC-02 Ejipura Signal Junction", PointType.JC, 30),
                RoutePoint(12.9362, 77.6245, "Koramangala Sony World Node (End)", PointType.END, 20)
            )
        ),
        OfcRoute(
            id = "route-3",
            name = "Route 3: Peenya Cross OLT → Jalahalli → Dasarahalli",
            fiberType = "12F Aerial ADSS OFC",
            points = listOf(
                RoutePoint(13.0285, 77.5390, "Peenya 1st Stage OLT", PointType.OLT, 15),
                RoutePoint(13.0342, 77.5295, "JC-01 SRS Junction Pole", PointType.JC, 25),
                RoutePoint(13.0395, 77.5198, "Jalahalli Cross Chamber #02", PointType.CHAMBER, 30),
                RoutePoint(13.0438, 77.5122, "Dasarahalli Metro Pillar #312", PointType.POLE, 10),
                RoutePoint(13.0485, 77.5030, "8th Mile Hub Node (End)", PointType.END, 20)
            )
        )
    )

    val staffMembers = listOf(
        StaffMember("st-1", "Manjunath K", "+919845011223", "Admin / Manager", "Online"),
        StaffMember("st-2", "Darshan Gowda", "+919900122334", "Field Splicer", "On Field"),
        StaffMember("st-3", "Pradeep Kumar", "+919741233445", "GPS Surveyor", "Driving GPS"),
        StaffMember("st-4", "Chethan R", "+919611455667", "Field Splicer", "Available")
    )

    val faultTickets = listOf(
        FaultTicket(
            id = "TKT-101",
            routeName = "Route 1: Majestic OLT → Rajajinagar → Yeshwanthpur",
            otdrMeters = 1450,
            landmark = "JC-01 Bashyam Circle (Rajajinagar)",
            metersAway = 112,
            lat = 12.9891,
            lng = 77.5579,
            heading = 335,
            status = TicketStatus.OPEN_CUT,
            reportedBy = "Darshan Gowda (Splicer)",
            time = "14:32 PM"
        ),
        FaultTicket(
            id = "TKT-102",
            routeName = "Route 2: Indiranagar OLT → Domlur → Koramangala",
            otdrMeters = 820,
            landmark = "CMH Road Signal Pole #11",
            metersAway = 85,
            lat = 12.9723,
            lng = 77.6410,
            heading = 190,
            status = TicketStatus.OPEN_CUT,
            reportedBy = "Chethan R (Splicer)",
            time = "16:15 PM"
        )
    )
}
