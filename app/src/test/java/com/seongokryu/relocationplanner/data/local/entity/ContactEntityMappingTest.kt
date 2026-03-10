package com.seongokryu.relocationplanner.data.local.entity

import com.seongokryu.relocationplanner.domain.model.Contact
import org.junit.Assert.assertEquals
import org.junit.Test

class ContactEntityMappingTest {
    @Test
    fun should_map_contact_entity_to_domain() {
        val entity =
            ContactEntity(
                id = 1,
                name = "김변호사",
                role = "이민 변호사",
                phone = "010-1234-5678",
                email = "kim@law.com",
                note = "비자 상담",
                createdAt = "2026-03-01T10:00:00Z",
            )

        val domain = entity.toDomain()

        assertEquals(1L, domain.id)
        assertEquals("김변호사", domain.name)
        assertEquals("이민 변호사", domain.role)
        assertEquals("010-1234-5678", domain.phone)
        assertEquals("kim@law.com", domain.email)
        assertEquals("비자 상담", domain.note)
    }

    @Test
    fun should_map_contact_domain_to_entity() {
        val domain =
            Contact(
                id = 2,
                name = "John Smith",
                role = "부동산 에이전트",
                phone = "555-1234",
                email = "john@realty.com",
                note = "",
                createdAt = "2026-03-10T12:00:00Z",
            )

        val entity = ContactEntity.fromDomain(domain)

        assertEquals(2L, entity.id)
        assertEquals("John Smith", entity.name)
        assertEquals("부동산 에이전트", entity.role)
        assertEquals("555-1234", entity.phone)
        assertEquals("john@realty.com", entity.email)
    }

    @Test
    fun should_roundtrip_contact() {
        val original =
            Contact(
                id = 3,
                name = "Jane Doe",
                role = "은행 담당자",
                phone = "555-9876",
                email = "jane@bank.com",
                note = "계좌 개설",
                createdAt = "2026-03-10",
            )

        val roundtripped = ContactEntity.fromDomain(original).toDomain()

        assertEquals(original, roundtripped)
    }
}
