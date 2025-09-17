package no.uio.ifi.in2000_gruppe3.data.date

import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Date
import java.util.Locale

enum class Weekdays {
    Mandag, Tirsdag, Onsdag, Torsdag, Fredag, Lørdag, Søndag;

    companion object {
        fun indexOf(dayName: String): Int = entries.toTypedArray().indexOfFirst { it.name == dayName }
    }
}

// Returns a list of all weekdays, starting on todays day
fun getOrderedWeekdays(todaysDay: String): List<Weekdays> {
    val startIndex = Weekdays.indexOf(todaysDay)
    val weekdaysList = Weekdays.entries.toList()
    return weekdaysList.drop(startIndex) + weekdaysList.take(startIndex)
}

// Returns date on format "YYYY-MM-DD"
fun getTodaysDate(): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date())
}

// Returns date on format "dd. MMMM"
fun getDateFormatted(date: String): String {
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val outputFormatter = SimpleDateFormat("dd. MMMM", Locale("no", "NO"))

    val parsedDate = inputFormatter.parse(date)

    return outputFormatter.format(parsedDate!!)
}

// Returns current time on format "HH:mm:ss"
fun getCurrentTime(): String {
    val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formatter.format(Date())
}

// Returns today's day
fun getTodaysDay(): String {
    val dayOfWeek = LocalDate.now().dayOfWeek
    val daysMap = mapOf(
        DayOfWeek.MONDAY to "Mandag",
        DayOfWeek.TUESDAY to "Tirsdag",
        DayOfWeek.WEDNESDAY to "Onsdag",
        DayOfWeek.THURSDAY to "Torsdag",
        DayOfWeek.FRIDAY to "Fredag",
        DayOfWeek.SATURDAY to "Lørdag",
        DayOfWeek.SUNDAY to "Søndag"
    )
    return daysMap[dayOfWeek] ?: "Ukjent dag"
}

// Returns the number of days ahead from today's day to the selected day
fun calculateDaysAhead(todaysDay: String, selectedDay: String): Int {
    val todayIndex = Weekdays.indexOf(todaysDay)
    val selectedIndex = Weekdays.indexOf(selectedDay)

    return if (selectedIndex >= todayIndex) {
        selectedIndex - todayIndex
    } else {
        7 - todayIndex + selectedIndex
    }
}