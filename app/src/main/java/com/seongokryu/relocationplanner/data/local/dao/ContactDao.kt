package com.seongokryu.relocationplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seongokryu.relocationplanner.data.local.entity.ContactEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {
    @Query("SELECT * FROM contacts ORDER BY created_at DESC")
    fun getAllContacts(): Flow<List<ContactEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contact: ContactEntity): Long

    @Delete
    suspend fun delete(contact: ContactEntity)
}
