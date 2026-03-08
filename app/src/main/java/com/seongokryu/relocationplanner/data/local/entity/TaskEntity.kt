package com.seongokryu.relocationplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Priority
import com.seongokryu.relocationplanner.domain.model.Task

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: String,
    val priority: String = "MEDIUM",
    @ColumnInfo(name = "is_done")
    val isDone: Boolean = false,
    val assignee: String = "",
    @ColumnInfo(name = "due_date")
    val dueDate: String? = null,
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
    @ColumnInfo(name = "updated_at")
    val updatedAt: String = "",
) {
    fun toDomain(): Task = Task(
        id = id,
        title = title,
        description = description,
        category = Category.valueOf(category),
        priority = Priority.valueOf(priority),
        isDone = isDone,
        assignee = assignee,
        dueDate = dueDate,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    companion object {
        fun fromDomain(task: Task): TaskEntity = TaskEntity(
            id = task.id,
            title = task.title,
            description = task.description,
            category = task.category.name,
            priority = task.priority.name,
            isDone = task.isDone,
            assignee = task.assignee,
            dueDate = task.dueDate,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt,
        )
    }
}
