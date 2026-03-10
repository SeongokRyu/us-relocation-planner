package com.seongokryu.relocationplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.seongokryu.relocationplanner.domain.model.Note

@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("task_id")],
)
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "task_id")
    val taskId: Long,
    val content: String,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
) {
    fun toDomain(): Note =
        Note(
            id = id,
            taskId = taskId,
            content = content,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(note: Note): NoteEntity =
            NoteEntity(
                id = note.id,
                taskId = note.taskId,
                content = note.content,
                createdAt = note.createdAt,
            )
    }
}
