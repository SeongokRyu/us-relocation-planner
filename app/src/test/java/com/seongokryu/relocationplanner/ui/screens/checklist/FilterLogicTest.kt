package com.seongokryu.relocationplanner.ui.screens.checklist

import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Priority
import com.seongokryu.relocationplanner.domain.model.Task
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FilterLogicTest {
    private val pendingHighTask =
        Task(
            id = 1L,
            title = "높은 우선순위 미완료",
            category = Category.VISA,
            priority = Priority.HIGH,
            isDone = false,
        )

    private val doneMediumTask =
        Task(
            id = 2L,
            title = "보통 우선순위 완료",
            category = Category.VISA,
            priority = Priority.MEDIUM,
            isDone = true,
        )

    private val pendingLowTask =
        Task(
            id = 3L,
            title = "낮은 우선순위 미완료",
            category = Category.VISA,
            priority = Priority.LOW,
            isDone = false,
        )

    // --- StatusFilter tests ---

    @Test
    fun should_show_all_tasks_when_status_filter_is_ALL() {
        assertTrue(matchesStatus(pendingHighTask, StatusFilter.ALL))
        assertTrue(matchesStatus(doneMediumTask, StatusFilter.ALL))
    }

    @Test
    fun should_show_only_pending_tasks_when_status_filter_is_PENDING() {
        assertTrue(matchesStatus(pendingHighTask, StatusFilter.PENDING))
        assertFalse(matchesStatus(doneMediumTask, StatusFilter.PENDING))
    }

    @Test
    fun should_show_only_done_tasks_when_status_filter_is_DONE() {
        assertFalse(matchesStatus(pendingHighTask, StatusFilter.DONE))
        assertTrue(matchesStatus(doneMediumTask, StatusFilter.DONE))
    }

    // --- PriorityFilter tests ---

    @Test
    fun should_show_all_tasks_when_priority_filter_is_ALL() {
        assertTrue(matchesPriority(pendingHighTask, PriorityFilter.ALL))
        assertTrue(matchesPriority(doneMediumTask, PriorityFilter.ALL))
        assertTrue(matchesPriority(pendingLowTask, PriorityFilter.ALL))
    }

    @Test
    fun should_show_only_high_priority_when_priority_filter_is_HIGH() {
        assertTrue(matchesPriority(pendingHighTask, PriorityFilter.HIGH))
        assertFalse(matchesPriority(doneMediumTask, PriorityFilter.HIGH))
        assertFalse(matchesPriority(pendingLowTask, PriorityFilter.HIGH))
    }

    @Test
    fun should_show_only_medium_priority_when_priority_filter_is_MEDIUM() {
        assertFalse(matchesPriority(pendingHighTask, PriorityFilter.MEDIUM))
        assertTrue(matchesPriority(doneMediumTask, PriorityFilter.MEDIUM))
        assertFalse(matchesPriority(pendingLowTask, PriorityFilter.MEDIUM))
    }

    @Test
    fun should_show_only_low_priority_when_priority_filter_is_LOW() {
        assertFalse(matchesPriority(pendingHighTask, PriorityFilter.LOW))
        assertFalse(matchesPriority(doneMediumTask, PriorityFilter.LOW))
        assertTrue(matchesPriority(pendingLowTask, PriorityFilter.LOW))
    }

    // --- Combined filter tests ---

    @Test
    fun should_show_all_tasks_when_both_filters_are_ALL() {
        val tasks = listOf(pendingHighTask, doneMediumTask, pendingLowTask)
        val filter = FilterState(StatusFilter.ALL, PriorityFilter.ALL)

        val filtered =
            tasks.filter {
                matchesStatus(it, filter.statusFilter) && matchesPriority(it, filter.priorityFilter)
            }

        assertTrue(filtered.size == 3)
    }

    @Test
    fun should_filter_pending_high_priority_only() {
        val tasks = listOf(pendingHighTask, doneMediumTask, pendingLowTask)
        val filter = FilterState(StatusFilter.PENDING, PriorityFilter.HIGH)

        val filtered =
            tasks.filter {
                matchesStatus(it, filter.statusFilter) && matchesPriority(it, filter.priorityFilter)
            }

        assertTrue(filtered.size == 1)
        assertTrue(filtered[0] == pendingHighTask)
    }

    @Test
    fun should_return_empty_when_no_tasks_match() {
        val tasks = listOf(pendingHighTask, pendingLowTask)
        val filter = FilterState(StatusFilter.DONE, PriorityFilter.HIGH)

        val filtered =
            tasks.filter {
                matchesStatus(it, filter.statusFilter) && matchesPriority(it, filter.priorityFilter)
            }

        assertTrue(filtered.isEmpty())
    }
}
