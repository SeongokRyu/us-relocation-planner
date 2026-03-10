package com.seongokryu.relocationplanner.ui.screens.timeline

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seongokryu.relocationplanner.data.repository.TaskRepository
import com.seongokryu.relocationplanner.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class MonthGroup(
    val label: String,
    val tasks: List<Task>,
)

@HiltViewModel
class TimelineViewModel
    @Inject
    constructor(
        repository: TaskRepository,
    ) : ViewModel() {
        private val allTasks: StateFlow<List<Task>> =
            repository.getAllTasks()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val timelineGroups: StateFlow<List<MonthGroup>> =
            allTasks.map { tasks -> groupByMonth(tasks) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val undatedTasks: StateFlow<List<Task>> =
            allTasks.map { tasks -> tasks.filter { it.dueDate.isNullOrBlank() } }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

internal fun groupByMonth(tasks: List<Task>): List<MonthGroup> {
    return tasks
        .filter { !it.dueDate.isNullOrBlank() }
        .sortedBy { it.dueDate }
        .groupBy { task ->
            val parts = task.dueDate!!.split("-")
            if (parts.size >= 2) "${parts[0]}년 ${parts[1].toIntOrNull() ?: 0}월" else "기타"
        }
        .map { (label, groupTasks) -> MonthGroup(label, groupTasks) }
}
