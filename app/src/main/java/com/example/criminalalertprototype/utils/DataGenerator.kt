package com.example.criminalalertprototype.utils

import com.example.criminalalertprototype.models.AlertModel

object DataGenerator {

    fun generateAlerts(): List<AlertModel> {
        return listOf(
            AlertModel(
                id = "1",
                title = "Suspicious Activity",
                description = "Silver sedan idling with headlights off near the north gate. Driver appeared to be filming houses.",
                type = "Suspicious",
                timeAgo = "5m ago",
                urgency = "Medium",
                location = "North Gate Area"
            ),
            AlertModel(
                id = "2",
                title = "Theft Reported",
                description = "Bicycle stolen from porch on Elm Street. Lock was cut. Security camera may have captured footage.",
                type = "Theft",
                timeAgo = "2h ago",
                urgency = "High",
                location = "Elm Street",
                isVerified = true
            ),
            AlertModel(
                id = "3",
                title = "Fire Incident",
                description = "Small trash fire contained near community park. Fire department has been notified and is on scene.",
                type = "Fire",
                timeAgo = "Yesterday",
                urgency = "High",
                location = "Community Park",
                isVerified = true
            ),
            AlertModel(
                id = "4",
                title = "Vandalism",
                description = "Graffiti reported on wall of abandoned building at the corner of Oak and 5th. Camera footage available from nearby store.",
                type = "Vandalism",
                timeAgo = "3h ago",
                urgency = "Low",
                location = "Oak Street"
            ),
            AlertModel(
                id = "5",
                title = "Suspicious Person",
                description = "Individual looking into car windows in parking lot near the mall entrance. Wearing dark hoodie and jeans.",
                type = "Suspicious",
                timeAgo = "1h ago",
                urgency = "Medium",
                location = "Downtown Parking Lot"
            ),
            AlertModel(
                id = "6",
                title = "Gas Leak Report",
                description = "Residents report strong gas smell near the intersection. Utility company en route.",
                type = "Fire",
                timeAgo = "30m ago",
                urgency = "High",
                location = "Main & 2nd Street",
                isVerified = true
            ),
            AlertModel(
                id = "7",
                title = "Package Theft",
                description = "Doorbell camera captured someone taking packages from multiple porches on Maple Drive.",
                type = "Theft",
                timeAgo = "4h ago",
                urgency = "Medium",
                location = "Maple Drive"
            )
        )
    }

    fun getCategories(): List<String> = listOf("All", "Theft", "Suspicious", "Fire", "Vandalism")
}