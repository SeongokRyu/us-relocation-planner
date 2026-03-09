package com.seongokryu.relocationplanner.ui.screens.dashboard

import com.seongokryu.relocationplanner.data.local.dao.CategoryStat
import kotlin.test.Test
import kotlin.test.assertEquals

class ProgressCalculationTest {
    @Test
    fun should_calculate_progress_percentage_correctly() {
        val done = 48
        val total = 66
        val percentage = if (total > 0) (done.toFloat() / total * 100).toInt() else 0

        assertEquals(72, percentage)
    }

    @Test
    fun should_return_zero_when_total_is_zero() {
        val done = 0
        val total = 0
        val percentage = if (total > 0) (done.toFloat() / total * 100).toInt() else 0

        assertEquals(0, percentage)
    }

    @Test
    fun should_return_100_when_all_done() {
        val done = 10
        val total = 10
        val percentage = if (total > 0) (done.toFloat() / total * 100).toInt() else 0

        assertEquals(100, percentage)
    }

    @Test
    fun should_sum_category_stats_correctly() {
        val stats =
            listOf(
                CategoryStat("VISA", 12, 8),
                CategoryStat("HOUSING", 10, 5),
                CategoryStat("FINANCE", 8, 3),
            )

        val total = stats.sumOf { it.total }
        val done = stats.sumOf { it.done }

        assertEquals(30, total)
        assertEquals(16, done)
    }
}
