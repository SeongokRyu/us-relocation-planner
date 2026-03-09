package com.seongokryu.relocationplanner.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.seongokryu.relocationplanner.data.local.AppDatabase
import com.seongokryu.relocationplanner.data.local.entity.TaskEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class TaskDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database =
            Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
                .allowMainThreadQueries()
                .build()
        dao = database.taskDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun should_insert_and_retrieve_task() =
        runTest {
            val entity =
                TaskEntity(
                    title = "비자 신청",
                    category = "VISA",
                    priority = "HIGH",
                )

            val id = dao.insert(entity)
            val tasks = dao.getAllTasks().first()

            assertEquals(1, tasks.size)
            assertEquals("비자 신청", tasks[0].title)
            assertEquals(id, tasks[0].id)
        }

    @Test
    fun should_filter_tasks_by_category() =
        runTest {
            dao.insert(TaskEntity(title = "비자 항목", category = "VISA", priority = "HIGH"))
            dao.insert(TaskEntity(title = "주거 항목", category = "HOUSING", priority = "MEDIUM"))
            dao.insert(TaskEntity(title = "비자 항목2", category = "VISA", priority = "LOW"))

            val visaTasks = dao.getTasksByCategory("VISA").first()
            val housingTasks = dao.getTasksByCategory("HOUSING").first()

            assertEquals(2, visaTasks.size)
            assertEquals(1, housingTasks.size)
        }

    @Test
    fun should_toggle_done_status() =
        runTest {
            val id =
                dao.insert(
                    TaskEntity(title = "테스트", category = "VISA", priority = "HIGH"),
                )

            val beforeToggle = dao.getAllTasks().first()[0]
            assertFalse(beforeToggle.isDone)

            dao.toggleDone(id, "2026-01-01T00:00:00Z")

            val afterToggle = dao.getAllTasks().first()[0]
            assertTrue(afterToggle.isDone)
        }

    @Test
    fun should_update_task() =
        runTest {
            val id =
                dao.insert(
                    TaskEntity(title = "원래 제목", category = "VISA", priority = "MEDIUM"),
                )

            val task = dao.getAllTasks().first()[0]
            dao.update(task.copy(title = "수정된 제목", priority = "HIGH"))

            val updated = dao.getAllTasks().first()[0]
            assertEquals("수정된 제목", updated.title)
            assertEquals("HIGH", updated.priority)
        }

    @Test
    fun should_delete_task() =
        runTest {
            dao.insert(TaskEntity(title = "삭제할 항목", category = "VISA", priority = "LOW"))
            assertEquals(1, dao.getCount())

            val task = dao.getAllTasks().first()[0]
            dao.delete(task)

            assertEquals(0, dao.getCount())
        }

    @Test
    fun should_return_category_stats() =
        runTest {
            dao.insert(TaskEntity(title = "A", category = "VISA", priority = "HIGH"))
            dao.insert(TaskEntity(title = "B", category = "VISA", priority = "MEDIUM", isDone = true))
            dao.insert(TaskEntity(title = "C", category = "HOUSING", priority = "LOW"))

            val stats = dao.getCategoryStats().first()

            val visaStat = stats.find { it.category == "VISA" }
            assertEquals(2, visaStat?.total)
            assertEquals(1, visaStat?.done)

            val housingStat = stats.find { it.category == "HOUSING" }
            assertEquals(1, housingStat?.total)
            assertEquals(0, housingStat?.done)
        }

    @Test
    fun should_insert_all_tasks() =
        runTest {
            val entities =
                listOf(
                    TaskEntity(title = "항목1", category = "VISA", priority = "HIGH"),
                    TaskEntity(title = "항목2", category = "HOUSING", priority = "MEDIUM"),
                    TaskEntity(title = "항목3", category = "FINANCE", priority = "LOW"),
                )

            dao.insertAll(entities)

            assertEquals(3, dao.getCount())
        }

    @Test
    fun should_order_by_priority_then_created_at() =
        runTest {
            dao.insert(
                TaskEntity(title = "낮음", category = "VISA", priority = "LOW", createdAt = "2026-01-01"),
            )
            dao.insert(
                TaskEntity(title = "높음", category = "VISA", priority = "HIGH", createdAt = "2026-01-02"),
            )
            dao.insert(
                TaskEntity(title = "보통", category = "VISA", priority = "MEDIUM", createdAt = "2026-01-03"),
            )

            val tasks = dao.getAllTasks().first()

            assertEquals("높음", tasks[0].title)
            assertEquals("보통", tasks[1].title)
            assertEquals("낮음", tasks[2].title)
        }
}
