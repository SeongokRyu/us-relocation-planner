package com.seongokryu.relocationplanner.data.repository

import com.seongokryu.relocationplanner.data.local.dao.ContactDao
import com.seongokryu.relocationplanner.data.local.entity.ContactEntity
import com.seongokryu.relocationplanner.domain.model.Contact
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContactRepository
    @Inject
    constructor(
        private val contactDao: ContactDao,
    ) {
        fun getAllContacts(): Flow<List<Contact>> = contactDao.getAllContacts().map { entities -> entities.map { it.toDomain() } }

        suspend fun addContact(contact: Contact): Long {
            val now = Instant.now().toString()
            return contactDao.insert(ContactEntity.fromDomain(contact.copy(createdAt = now)))
        }

        suspend fun deleteContact(contact: Contact) {
            contactDao.delete(ContactEntity.fromDomain(contact))
        }
    }
