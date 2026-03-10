package com.seongokryu.relocationplanner.data.local.entity

import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Priority
import com.seongokryu.relocationplanner.domain.model.Task
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TaskEntityMappingTest {
    @Test
    fun should_convert_entity_to_domain_correctly() {
        val entity =
            TaskEntity(
                id = 1L,
                title = "비자 신청",
                description = "H-1B 비자 준비",
                category = "VISA",
                priority = "HIGH",
                isDone = false,
                assignee = "둘 다",
                dueDate = "2026-06-01",
                guide = "1. 비자 종류 결정\n2. 변호사 상담",
                referenceUrl = "https://example.com",
                createdAt = "2026-01-01T00:00:00Z",
                updatedAt = "2026-01-01T00:00:00Z",
            )

        val task = entity.toDomain()

        assertEquals(1L, task.id)
        assertEquals("비자 신청", task.title)
        assertEquals("H-1B 비자 준비", task.description)
        assertEquals(Category.VISA, task.category)
        assertEquals(Priority.HIGH, task.priority)
        assertEquals(false, task.isDone)
        assertEquals("둘 다", task.assignee)
        assertEquals("2026-06-01", task.dueDate)
        assertEquals("1. 비자 종류 결정\n2. 변호사 상담", task.guide)
        assertEquals("https://example.com", task.referenceUrl)
    }

    @Test
    fun should_convert_domain_to_entity_correctly() {
        val task =
            Task(
                id = 2L,
                title = "집 계약",
                description = "임대 계약서 서명",
                category = Category.HOUSING,
                priority = Priority.MEDIUM,
                isDone = true,
                assignee = "본인",
                dueDate = "2026-07-01",
                guide = "Zillow에서 검색",
                referenceUrl = "https://zillow.com",
                createdAt = "2026-01-01T00:00:00Z",
                updatedAt = "2026-01-02T00:00:00Z",
            )

        val entity = TaskEntity.fromDomain(task)

        assertEquals(2L, entity.id)
        assertEquals("집 계약", entity.title)
        assertEquals("HOUSING", entity.category)
        assertEquals("MEDIUM", entity.priority)
        assertEquals(true, entity.isDone)
        assertEquals("본인", entity.assignee)
        assertEquals("2026-07-01", entity.dueDate)
        assertEquals("Zillow에서 검색", entity.guide)
        assertEquals("https://zillow.com", entity.referenceUrl)
    }

    @Test
    fun should_roundtrip_entity_domain_entity() {
        val original =
            TaskEntity(
                id = 3L,
                title = "건강검진",
                description = "",
                category = "MEDICAL",
                priority = "LOW",
                isDone = false,
                assignee = "",
                dueDate = null,
                createdAt = "2026-01-01T00:00:00Z",
                updatedAt = "2026-01-01T00:00:00Z",
            )

        val roundTripped = TaskEntity.fromDomain(original.toDomain())

        assertEquals(original, roundTripped)
    }

    @Test
    fun should_handle_null_due_date() {
        val entity =
            TaskEntity(
                id = 4L,
                title = "테스트",
                category = "FINANCE",
                dueDate = null,
            )

        val task = entity.toDomain()

        assertNull(task.dueDate)
        assertEquals(task.dueDate, TaskEntity.fromDomain(task).dueDate)
    }

    @Test
    fun should_map_all_categories() {
        Category.entries.forEach { category ->
            val entity =
                TaskEntity(
                    title = "test",
                    category = category.name,
                )
            assertEquals(category, entity.toDomain().category)
        }
    }

    @Test
    fun should_map_all_priorities() {
        Priority.entries.forEach { priority ->
            val entity =
                TaskEntity(
                    title = "test",
                    category = "VISA",
                    priority = priority.name,
                )
            assertEquals(priority, entity.toDomain().priority)
        }
    }
}
