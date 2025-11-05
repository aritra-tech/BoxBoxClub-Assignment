package com.aritradas.boxboxclubassignment.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object Utils {

    fun formatSessionTime(timestamp: Long): String {
        val date = Date(timestamp * 1000)
        val format = SimpleDateFormat("h:mm a", Locale.getDefault())
        return format.format(date)
    }

    fun formatCircuitName(circuitId: String): String {
        return circuitId.split("_")
            .joinToString(" ") { it.replaceFirstChar { char -> char.uppercaseChar() } }
            .replace("Sao", "São")
    }

    fun formatRaceDates(startTime: Long, endTime: Long): String {
        val startDate = Date(startTime * 1000)
        val endDate = Date(endTime * 1000)

        val startCal = Calendar.getInstance().apply { time = startDate }
        val endCal = Calendar.getInstance().apply { time = endDate }

        val sameMonth = startCal.get(Calendar.MONTH) == endCal.get(Calendar.MONTH)
        val sameYear = startCal.get(Calendar.YEAR) == endCal.get(Calendar.YEAR)

        return if (sameMonth && sameYear) {
            val dayStart = startCal.get(Calendar.DAY_OF_MONTH)
            val dayEnd = endCal.get(Calendar.DAY_OF_MONTH)
            val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(startDate)
            "$dayStart - $dayEnd $monthName"
        } else if (sameYear) {
            val fmt = SimpleDateFormat("d MMM", Locale.getDefault())
            "${fmt.format(startDate)} - ${fmt.format(endDate)}"
        } else {
            val fmt = SimpleDateFormat("d MMM yyyy", Locale.getDefault())
            "${fmt.format(startDate)} - ${fmt.format(endDate)}"
        }
    }

    fun getCircuitDescription(circuitId: String): String {
        return when (circuitId) {
            "sao_paulo" -> "The São Paulo Grand Prix takes place at the Interlagos circuit, one of the most challenging tracks in Formula 1. Known for its elevation changes and technical corners, it provides exciting racing action."
            "sakhir" -> "The Bahrain International Circuit, located in Sakhir, was designed by Hermann Tilke. Originally a camel farm, it features a 5.412 km layout with 15 corners, 3 DRS Zones, and 57 laps. The circuit has 6 alternative layouts."
            else -> "This circuit is one of the premier venues in Formula 1, featuring challenging corners and high-speed sections that test both drivers and cars to their limits."
        }
    }

    fun formatRaceName(raceName: String): String {
        return raceName.replace("Grand Prix", "GP").trim()
    }
}