package com.seongokryu.relocationplanner.data.local.entity

import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Expense
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpenseEntityMappingTest {
    @Test
    fun should_map_expense_entity_to_domain() {
        val entity =
            ExpenseEntity(
                id = 1,
                title = "비자 신청비",
                amount = 350000.0,
                currency = "KRW",
                category = "DOCUMENTS",
                date = "2026-03-05",
                note = "영사관",
                createdAt = "2026-03-01T10:00:00Z",
            )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("비자 신청비", domain.title)
        assertEquals(350000.0, domain.amount, 0.01)
        assertEquals("KRW", domain.currency)
        assertEquals(Category.DOCUMENTS, domain.category)
        assertEquals("2026-03-05", domain.date)
        assertEquals("영사관", domain.note)
    }

    @Test
    fun should_map_expense_domain_to_entity() {
        val domain =
            Expense(
                id = 2,
                title = "Apartment deposit",
                amount = 2400.0,
                currency = "USD",
                category = Category.HOUSING,
                date = "2026-04-01",
                note = "",
                createdAt = "2026-03-10T12:00:00Z",
            )

        val entity = ExpenseEntity.fromDomain(domain)

        assertEquals(2L, entity.id)
        assertEquals("Apartment deposit", entity.title)
        assertEquals(2400.0, entity.amount, 0.01)
        assertEquals("USD", entity.currency)
        assertEquals("HOUSING", entity.category)
        assertEquals("2026-04-01", entity.date)
    }

    @Test
    fun should_roundtrip_expense() {
        val original =
            Expense(
                id = 3,
                title = "항공권",
                amount = 1500000.0,
                currency = "KRW",
                category = Category.TRANSPORT,
                date = "2026-05-15",
                note = "편도",
                createdAt = "2026-03-10",
            )

        val roundtripped = ExpenseEntity.fromDomain(original).toDomain()

        assertEquals(original, roundtripped)
    }
}
