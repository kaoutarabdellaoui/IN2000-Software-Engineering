package no.uio.ifi.in2000_gruppe3

import no.uio.ifi.in2000_gruppe3.data.date.Weekdays
import no.uio.ifi.in2000_gruppe3.data.date.calculateDaysAhead
import no.uio.ifi.in2000_gruppe3.data.date.getCurrentTime
import no.uio.ifi.in2000_gruppe3.data.date.getDateFormatted
import no.uio.ifi.in2000_gruppe3.data.date.getOrderedWeekdays
import no.uio.ifi.in2000_gruppe3.data.date.getTodaysDate
import no.uio.ifi.in2000_gruppe3.data.date.getTodaysDay
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DateFormatUnitTest {
    @Test
    fun testWeekdaysIndexOf() {
        assertEquals(0, Weekdays.indexOf("Mandag"))
        assertEquals(2, Weekdays.indexOf("Onsdag"))
        assertEquals(6, Weekdays.indexOf("Søndag"))
        assertEquals(-1, Weekdays.indexOf("NonExistentDay"))
    }

    @Test
    fun testGetOrderedWeekdays() {
        val orderedFromWednesday = getOrderedWeekdays("Onsdag")
        assertEquals(Weekdays.Onsdag, orderedFromWednesday[0])
        assertEquals(Weekdays.Torsdag, orderedFromWednesday[1])
        assertEquals(Weekdays.Tirsdag, orderedFromWednesday[6])

        val orderedFromSunday = getOrderedWeekdays("Søndag")
        assertEquals(Weekdays.Søndag, orderedFromSunday[0])
        assertEquals(Weekdays.Mandag, orderedFromSunday[1])
        assertEquals(Weekdays.Lørdag, orderedFromSunday[6])
    }

    @Test
    fun testGetTodaysDate() {
        val todaysDate = getTodaysDate()
        val expectedFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedCurrentDate = expectedFormat.format(Date())

        assertEquals(formattedCurrentDate, todaysDate)
    }

    @Test
    fun testGetDateFormatted() {
        // Testing a specific date
        val formatted = getDateFormatted("2023-05-17")
        assertEquals("17. mai", formatted.lowercase())

        val anotherFormatted = getDateFormatted("2022-12-24")
        assertEquals("24. desember", anotherFormatted.lowercase())
    }

    @Test
    fun testGetCurrentTime() {
        val currentTime = getCurrentTime()
        assertTrue(currentTime.matches(Regex("\\d{2}:\\d{2}:\\d{2}")))
    }

    @Test
    fun testGetTodaysDay() {
        val todaysDay = getTodaysDay()
        val validDays =
            arrayOf("Mandag", "Tirsdag", "Onsdag", "Torsdag", "Fredag", "Lørdag", "Søndag")
        assertTrue(validDays.contains(todaysDay))
    }

    @Test
    fun testCalculateDaysAhead() {
        assertEquals(0, calculateDaysAhead("Mandag", "Mandag"))

        assertEquals(2, calculateDaysAhead("Mandag", "Onsdag"))
        assertEquals(6, calculateDaysAhead("Mandag", "Søndag"))

        assertEquals(3, calculateDaysAhead("Fredag", "Mandag"))
        assertEquals(1, calculateDaysAhead("Søndag", "Mandag"))
        assertEquals(3, calculateDaysAhead("Torsdag", "Søndag"))
    }
}