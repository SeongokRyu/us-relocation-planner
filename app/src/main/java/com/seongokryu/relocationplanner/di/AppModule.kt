package com.seongokryu.relocationplanner.di

import android.content.Context
import androidx.room.Room
import com.seongokryu.relocationplanner.data.local.AppDatabase
import com.seongokryu.relocationplanner.data.local.dao.TaskDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "relocation_planner.db",
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao = database.taskDao()
}
