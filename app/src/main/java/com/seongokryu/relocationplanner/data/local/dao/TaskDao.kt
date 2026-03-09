package com.seongokryu.relocationplanner.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.seongokryu.relocationplanner.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY priority ASC, created_at ASC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE category = :category ORDER BY priority ASC, created_at ASC")
    fun getTasksByCategory(category: String): Flow<List<TaskEntity>>

    @Query(
        """
        SELECT category,
               COUNT(*) as total,
               SUM(CASE WHEN is_done = 1 THEN 1 ELSE 0 END) as done
        FROM tasks GROUP BY category
    """,
    )
    fun getCategoryStats(): Flow<List<CategoryStat>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Update
    suspend fun update(task: TaskEntity)

    @Query("UPDATE tasks SET is_done = NOT is_done, updated_at = :updatedAt WHERE id = :taskId")
    suspend fun toggleDone(
        taskId: Long,
        updatedAt: String,
    )

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query(
        """
        SELECT * FROM tasks
        WHERE title LIKE '%' || :query || '%'
           OR description LIKE '%' || :query || '%'
        ORDER BY priority ASC, created_at ASC
    """,
    )
    fun searchTasks(query: String): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getCount(): Int
}

data class CategoryStat(
    val category: String,
    val total: Int,
    val done: Int,
)
