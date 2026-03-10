package com.seongokryu.relocationplanner.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DueDateUtilTest {
    private val today = "2026-03-10"

    @Test
    fun should_return_negative_when_overdue() {
        val days = DueDateUtil.daysUntil("2026-03-08", today)
        assertEquals(-2, days)
    }

    @Test
    fun should_return_zero_on_due_date() {
        val days = DueDateUtil.daysUntil("2026-03-10", today)
        assertEquals(0, days)
    }

    @Test
    fun should_return_positive_when_future() {
        val days = DueDateUtil.daysUntil("2026-03-13", today)
        assertEquals(3, days)
    }

    @Test
    fun should_return_null_when_no_due_date() {
        assertNull(DueDateUtil.daysUntil(null, today))
        assertNull(DueDateUtil.daysUntil("", today))
    }

    @Test
    fun should_return_null_when_invalid_format() {
        assertNull(DueDateUtil.daysUntil("invalid-date", today))
    }

    @Test
    fun should_format_d_day_correctly() {
        assertEquals("D+2", DueDateUtil.formatDDay(-2))
        assertEquals("D-Day", DueDateUtil.formatDDay(0))
        assertEquals("D-3", DueDateUtil.formatDDay(3))
        assertEquals("D-10", DueDateUtil.formatDDay(10))
    }

    @Test
    fun should_classify_overdue() {
        assertEquals(UrgencyLevel.OVERDUE, DueDateUtil.urgencyLevel(-1))
        assertEquals(UrgencyLevel.OVERDUE, DueDateUtil.urgencyLevel(-5))
    }

    @Test
    fun should_classify_today() {
        assertEquals(UrgencyLevel.TODAY, DueDateUtil.urgencyLevel(0))
    }

    @Test
    fun should_classify_approaching() {
        assertEquals(UrgencyLevel.APPROACHING, DueDateUtil.urgencyLevel(1))
        assertEquals(UrgencyLevel.APPROACHING, DueDateUtil.urgencyLevel(3))
    }

    @Test
    fun should_classify_normal() {
        assertEquals(UrgencyLevel.NORMAL, DueDateUtil.urgencyLevel(4))
        assertEquals(UrgencyLevel.NORMAL, DueDateUtil.urgencyLevel(30))
    }
}
