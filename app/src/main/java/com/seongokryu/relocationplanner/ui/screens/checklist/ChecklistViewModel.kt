package com.seongokryu.relocationplanner.ui.screens.checklist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seongokryu.relocationplanner.data.repository.TaskRepository
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Priority
import com.seongokryu.relocationplanner.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChecklistViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val repository: TaskRepository,
    ) : ViewModel() {
        val category: Category =
            Category.valueOf(
                savedStateHandle.get<String>("category") ?: Category.VISA.name,
            )

        private val allTasks: StateFlow<List<Task>> =
            repository.getTasksByCategory(category)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val filterState = MutableStateFlow(FilterState())

        val filteredTasks: StateFlow<List<Task>> =
            combine(allTasks, filterState) { tasks, filter ->
                tasks.filter { task ->
                    matchesStatus(task, filter.statusFilter) &&
                        matchesPriority(task, filter.priorityFilter)
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        fun updateFilter(newFilter: FilterState) {
            filterState.value = newFilter
        }

        fun toggleTask(taskId: Long) {
            viewModelScope.launch { repository.toggleTask(taskId) }
        }

        fun addTask(task: Task) {
            viewModelScope.launch { repository.addTask(task) }
        }

        fun deleteTask(task: Task) {
            viewModelScope.launch { repository.deleteTask(task) }
        }

        fun updateTask(task: Task) {
            viewModelScope.launch { repository.updateTask(task) }
        }
    }

internal fun matchesStatus(
    task: Task,
    filter: StatusFilter,
): Boolean =
    when (filter) {
        StatusFilter.ALL -> true
        StatusFilter.PENDING -> !task.isDone
        StatusFilter.DONE -> task.isDone
    }

internal fun matchesPriority(
    task: Task,
    filter: PriorityFilter,
): Boolean =
    when (filter) {
        PriorityFilter.ALL -> true
        PriorityFilter.HIGH -> task.priority == Priority.HIGH
        PriorityFilter.MEDIUM -> task.priority == Priority.MEDIUM
        PriorityFilter.LOW -> task.priority == Priority.LOW
    }
