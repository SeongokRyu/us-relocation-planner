package com.seongokryu.relocationplanner.data.local.entity

import com.seongokryu.relocationplanner.domain.model.Note
import kotlin.test.Test
import kotlin.test.assertEquals

class NoteEntityMappingTest {
    @Test
    fun should_map_note_entity_to_domain() {
        val entity = NoteEntity(id = 1, taskId = 10, content = "test memo", createdAt = "2026-03-10")
        val note = entity.toDomain()

        assertEquals(1L, note.id)
        assertEquals(10L, note.taskId)
        assertEquals("test memo", note.content)
        assertEquals("2026-03-10", note.createdAt)
    }

    @Test
    fun should_map_note_domain_to_entity() {
        val note = Note(id = 2, taskId = 5, content = "another memo", createdAt = "2026-03-11")
        val entity = NoteEntity.fromDomain(note)

        assertEquals(2L, entity.id)
        assertEquals(5L, entity.taskId)
        assertEquals("another memo", entity.content)
        assertEquals("2026-03-11", entity.createdAt)
    }

    @Test
    fun should_roundtrip_note_entity_domain_entity() {
        val original = NoteEntity(id = 3, taskId = 7, content = "roundtrip", createdAt = "2026-03-12")
        val converted = NoteEntity.fromDomain(original.toDomain())

        assertEquals(original, converted)
    }
}
