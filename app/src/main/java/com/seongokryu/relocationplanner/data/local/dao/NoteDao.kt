package com.seongokryu.relocationplanner.data.local.dao

import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.seongokryu.relocationplanner.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

data class NoteCount(
    @ColumnInfo(name = "task_id") val taskId: Long,
    val count: Int,
)

@Dao
interface NoteDao {
    @Query("SELECT * FROM notes WHERE task_id = :taskId ORDER BY created_at DESC")
    fun getNotesByTaskId(taskId: Long): Flow<List<NoteEntity>>

    @Query("SELECT task_id, COUNT(*) as count FROM notes GROUP BY task_id")
    fun getNoteCountsByTask(): Flow<List<NoteCount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Delete
    suspend fun delete(note: NoteEntity)
}
