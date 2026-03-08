package com.seongokryu.relocationplanner.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seongokryu.relocationplanner.data.local.dao.CategoryStat
import com.seongokryu.relocationplanner.data.repository.TaskRepository
import com.seongokryu.relocationplanner.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel
    @Inject
    constructor(
        private val repository: TaskRepository,
    ) : ViewModel() {
        val categoryStats: StateFlow<List<CategoryStat>> =
            repository.getCategoryStats()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val allTasks: StateFlow<List<Task>> =
            repository.getAllTasks()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        init {
            viewModelScope.launch {
                repository.seedDefaultsIfEmpty()
            }
        }

        fun getHighPriorityPending(tasks: List<Task>): List<Task> =
            tasks.filter { !it.isDone && it.priority == com.seongokryu.relocationplanner.domain.model.Priority.HIGH }

        fun totalProgress(stats: List<CategoryStat>): Pair<Int, Int> {
            val total = stats.sumOf { it.total }
            val done = stats.sumOf { it.done }
            return done to total
        }
    }
