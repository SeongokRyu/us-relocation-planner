package com.seongokryu.relocationplanner.data.export

import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Priority
import com.seongokryu.relocationplanner.domain.model.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExportFormatterTest {
    @Test
    fun should_format_task_for_export_done() {
        val task =
            Task(
                title = "비자 신청",
                category = Category.VISA,
                priority = Priority.HIGH,
                isDone = true,
                dueDate = "2026-04-01",
            )

        val result = ExportFormatter.formatTaskForExport(task)

        assertEquals("[x] (높음) 비자 신청 ~2026-04-01", result)
    }

    @Test
    fun should_format_task_for_export_pending() {
        val task =
            Task(
                title = "집 구하기",
                category = Category.HOUSING,
                priority = Priority.MEDIUM,
                isDone = false,
            )

        val result = ExportFormatter.formatTaskForExport(task)

        assertEquals("[ ] (보통) 집 구하기", result)
    }

    @Test
    fun should_calculate_export_stats() {
        val tasks =
            listOf(
                Task(title = "A", category = Category.VISA, isDone = true),
                Task(title = "B", category = Category.VISA, isDone = true),
                Task(title = "C", category = Category.VISA, isDone = false),
                Task(title = "D", category = Category.HOUSING, isDone = false),
            )

        val stats = ExportFormatter.calculateExportStats(tasks)

        assertEquals(4, stats.total)
        assertEquals(2, stats.done)
        assertEquals(50, stats.progressPercent)
    }

    @Test
    fun should_calculate_export_stats_empty() {
        val stats = ExportFormatter.calculateExportStats(emptyList())

        assertEquals(0, stats.total)
        assertEquals(0, stats.done)
        assertEquals(0, stats.progressPercent)
    }

    @Test
    fun should_group_by_category() {
        val tasks =
            listOf(
                Task(title = "A", category = Category.VISA, isDone = true),
                Task(title = "B", category = Category.VISA, isDone = false),
                Task(title = "C", category = Category.HOUSING, isDone = false),
            )

        val groups = ExportFormatter.groupByCategory(tasks)

        assertEquals(2, groups.size)
        val visaGroup = groups.find { it.category == Category.VISA }!!
        assertEquals(2, visaGroup.total)
        assertEquals(1, visaGroup.done)

        val housingGroup = groups.find { it.category == Category.HOUSING }!!
        assertEquals(1, housingGroup.total)
        assertEquals(0, housingGroup.done)
    }

    @Test
    fun should_skip_empty_categories() {
        val tasks =
            listOf(
                Task(title = "A", category = Category.VISA, isDone = true),
            )

        val groups = ExportFormatter.groupByCategory(tasks)

        assertEquals(1, groups.size)
        assertTrue(groups.all { it.category == Category.VISA })
    }
}
