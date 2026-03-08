package com.seongokryu.relocationplanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seongokryu.relocationplanner.data.local.dao.TaskDao
import com.seongokryu.relocationplanner.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
