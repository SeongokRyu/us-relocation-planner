package com.seongokryu.relocationplanner.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seongokryu.relocationplanner.data.repository.NoteRepository
import com.seongokryu.relocationplanner.data.repository.TaskRepository
import com.seongokryu.relocationplanner.domain.model.Note
import com.seongokryu.relocationplanner.domain.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val taskRepository: TaskRepository,
        private val noteRepository: NoteRepository,
    ) : ViewModel() {
        private val taskId: Long = checkNotNull(savedStateHandle["taskId"])

        val task: StateFlow<Task?> =
            taskRepository.getAllTasks()
                .map { tasks -> tasks.find { it.id == taskId } }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        val notes: StateFlow<List<Note>> =
            noteRepository.getNotesByTaskId(taskId)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        fun addNote(content: String) {
            if (content.isBlank()) return
            viewModelScope.launch {
                noteRepository.addNote(Note(taskId = taskId, content = content))
            }
        }

        fun deleteNote(note: Note) {
            viewModelScope.launch {
                noteRepository.deleteNote(note)
            }
        }

        fun toggleTask() {
            viewModelScope.launch {
                taskRepository.toggleTask(taskId)
            }
        }
    }
