package com.seongokryu.relocationplanner.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seongokryu.relocationplanner.data.local.dao.CategoryStat
import com.seongokryu.relocationplanner.data.repository.TaskRepository
import com.seongokryu.relocationplanner.domain.model.Priority
import com.seongokryu.relocationplanner.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
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

        val isSearchActive = MutableStateFlow(false)
        val searchQuery = MutableStateFlow("")

        val searchResults: StateFlow<List<Task>> =
            searchQuery.flatMapLatest { query ->
                if (query.isBlank()) flowOf(emptyList()) else repository.searchTasks(query)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        init {
            viewModelScope.launch {
                repository.seedDefaultsIfEmpty()
            }
        }

        fun onSearchQueryChanged(query: String) {
            searchQuery.value = query
        }

        fun toggleSearch() {
            val newActive = !isSearchActive.value
            isSearchActive.value = newActive
            if (!newActive) searchQuery.value = ""
        }

        fun getHighPriorityPending(tasks: List<Task>): List<Task> = tasks.filter { !it.isDone && it.priority == Priority.HIGH }

        fun totalProgress(stats: List<CategoryStat>): Pair<Int, Int> {
            val total = stats.sumOf { it.total }
            val done = stats.sumOf { it.done }
            return done to total
        }
    }
