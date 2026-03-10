package com.seongokryu.relocationplanner.ui.screens.timeline

import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TimelineGroupingTest {
    private fun task(
        title: String,
        dueDate: String? = null,
    ) = Task(title = title, category = Category.VISA, dueDate = dueDate)

    @Test
    fun should_group_tasks_by_month() {
        val tasks =
            listOf(
                task("A", "2026-03-10"),
                task("B", "2026-03-20"),
                task("C", "2026-04-05"),
            )
        val groups = groupByMonth(tasks)

        assertEquals(2, groups.size)
        assertEquals("2026년 3월", groups[0].label)
        assertEquals(2, groups[0].tasks.size)
        assertEquals("2026년 4월", groups[1].label)
        assertEquals(1, groups[1].tasks.size)
    }

    @Test
    fun should_sort_tasks_within_month_by_date() {
        val tasks =
            listOf(
                task("Later", "2026-03-25"),
                task("Earlier", "2026-03-05"),
                task("Middle", "2026-03-15"),
            )
        val groups = groupByMonth(tasks)

        assertEquals(1, groups.size)
        assertEquals("Earlier", groups[0].tasks[0].title)
        assertEquals("Middle", groups[0].tasks[1].title)
        assertEquals("Later", groups[0].tasks[2].title)
    }

    @Test
    fun should_exclude_undated_tasks() {
        val tasks =
            listOf(
                task("Dated", "2026-03-10"),
                task("Undated", null),
                task("Blank", ""),
            )
        val groups = groupByMonth(tasks)

        assertEquals(1, groups.size)
        assertEquals(1, groups[0].tasks.size)
        assertEquals("Dated", groups[0].tasks[0].title)
    }

    @Test
    fun should_return_empty_when_no_dated_tasks() {
        val tasks = listOf(task("A"), task("B"))
        val groups = groupByMonth(tasks)

        assertTrue(groups.isEmpty())
    }
}
