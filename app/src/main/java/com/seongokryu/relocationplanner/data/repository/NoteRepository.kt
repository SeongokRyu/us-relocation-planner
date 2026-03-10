package com.seongokryu.relocationplanner.data.repository

import com.seongokryu.relocationplanner.data.local.dao.NoteCount
import com.seongokryu.relocationplanner.data.local.dao.NoteDao
import com.seongokryu.relocationplanner.data.local.entity.NoteEntity
import com.seongokryu.relocationplanner.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepository
    @Inject
    constructor(
        private val noteDao: NoteDao,
    ) {
        fun getNotesByTaskId(taskId: Long): Flow<List<Note>> =
            noteDao.getNotesByTaskId(taskId).map { entities -> entities.map { it.toDomain() } }

        fun getNoteCountsByTask(): Flow<List<NoteCount>> = noteDao.getNoteCountsByTask()

        suspend fun addNote(note: Note): Long {
            val now = Instant.now().toString()
            return noteDao.insert(NoteEntity.fromDomain(note.copy(createdAt = now)))
        }

        suspend fun deleteNote(note: Note) {
            noteDao.delete(NoteEntity.fromDomain(note))
        }
    }
