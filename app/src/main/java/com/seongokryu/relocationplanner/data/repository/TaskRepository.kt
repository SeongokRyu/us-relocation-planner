package com.seongokryu.relocationplanner.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.seongokryu.relocationplanner.data.local.dao.CategoryStat
import com.seongokryu.relocationplanner.data.local.dao.TaskDao
import com.seongokryu.relocationplanner.data.local.entity.TaskEntity
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository
    @Inject
    constructor(
        private val taskDao: TaskDao,
        @ApplicationContext private val context: Context,
    ) {
        fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks().map { entities -> entities.map { it.toDomain() } }

        fun getTasksByCategory(category: Category): Flow<List<Task>> =
            taskDao.getTasksByCategory(category.name).map { entities -> entities.map { it.toDomain() } }

        fun getCategoryStats(): Flow<List<CategoryStat>> = taskDao.getCategoryStats()

        fun searchTasks(query: String): Flow<List<Task>> = taskDao.searchTasks(query).map { entities -> entities.map { it.toDomain() } }

        suspend fun addTask(task: Task): Long {
            val now = Instant.now().toString()
            val entity = TaskEntity.fromDomain(task.copy(createdAt = now, updatedAt = now))
            return taskDao.insert(entity)
        }

        suspend fun updateTask(task: Task) {
            val now = Instant.now().toString()
            taskDao.update(TaskEntity.fromDomain(task.copy(updatedAt = now)))
        }

        suspend fun toggleTask(taskId: Long) {
            taskDao.toggleDone(taskId, Instant.now().toString())
        }

        suspend fun deleteTask(task: Task) {
            taskDao.delete(TaskEntity.fromDomain(task))
        }

        suspend fun seedDefaultsIfEmpty() {
            if (taskDao.getCount() > 0) return

            val json =
                context.assets.open("default_tasks.json")
                    .bufferedReader().use { it.readText() }

            val type = object : TypeToken<List<SeedTask>>() {}.type
            val seedTasks: List<SeedTask> = Gson().fromJson(json, type)
            val now = Instant.now().toString()

            val entities =
                seedTasks.map { seed ->
                    TaskEntity(
                        title = seed.title,
                        description = seed.description.orEmpty(),
                        category = seed.category.uppercase(),
                        priority = (seed.priority ?: "medium").uppercase(),
                        isDone = false,
                        assignee = seed.assignee.orEmpty(),
                        dueDate = seed.due_date,
                        guide = seed.guide.orEmpty(),
                        referenceUrl = seed.reference_url.orEmpty(),
                        createdAt = now,
                        updatedAt = now,
                    )
                }
            taskDao.insertAll(entities)
        }
    }

private data class SeedTask(
    val title: String,
    val description: String?,
    val category: String,
    val priority: String?,
    val assignee: String?,
    val due_date: String?,
    val guide: String?,
    val reference_url: String?,
)
