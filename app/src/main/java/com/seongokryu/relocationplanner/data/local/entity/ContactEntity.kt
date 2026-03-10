package com.seongokryu.relocationplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.seongokryu.relocationplanner.domain.model.Contact

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val role: String = "",
    val phone: String = "",
    val email: String = "",
    val note: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
) {
    fun toDomain(): Contact =
        Contact(
            id = id,
            name = name,
            role = role,
            phone = phone,
            email = email,
            note = note,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(contact: Contact): ContactEntity =
            ContactEntity(
                id = contact.id,
                name = contact.name,
                role = contact.role,
                phone = contact.phone,
                email = contact.email,
                note = contact.note,
                createdAt = contact.createdAt,
            )
    }
}
