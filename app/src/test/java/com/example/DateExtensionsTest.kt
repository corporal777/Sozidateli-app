package com.example

import com.example.common.formatToEventDatesInterval
import org.junit.Assert.assertEquals
import org.junit.Test

class DateExtensionsTest {

    @Test
    fun `different dates interval has two dates`() {
        val startDate = "2000-01-01"
        val finishDate = "2001-01-01"
        val result = "01 января 2000 - 01 января 2001"

        val interval = startDate.formatToEventDatesInterval(finishDate)

        assertEquals(interval, result)
    }

    @Test
    fun `same dates interval has one date`() {
        val startDate = "2000-01-01"
        val finishDate = "2000-01-01"
        val result = "01 января 2000"

        val interval = startDate.formatToEventDatesInterval(finishDate)

        assertEquals(interval, result)
    }
}