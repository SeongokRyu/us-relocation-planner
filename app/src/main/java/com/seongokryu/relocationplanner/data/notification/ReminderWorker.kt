package com.seongokryu.relocationplanner.data.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.seongokryu.relocationplanner.R
import com.seongokryu.relocationplanner.data.local.dao.TaskDao
import com.seongokryu.relocationplanner.domain.model.DueDateUtil
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@HiltWorker
class ReminderWorker
    @AssistedInject
    constructor(
        @Assisted context: Context,
        @Assisted params: WorkerParameters,
        private val taskDao: TaskDao,
    ) : CoroutineWorker(context, params) {
        override suspend fun doWork(): Result {
            createNotificationChannel()

            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            val tasks = taskDao.getAllTasks().first()

            val upcomingTasks =
                tasks
                    .filter { !it.isDone && !it.dueDate.isNullOrBlank() }
                    .mapNotNull { entity ->
                        val days = DueDateUtil.daysUntil(entity.dueDate, today)
                        if (days != null && days in 0..1) entity to days else null
                    }

            upcomingTasks.forEach { (entity, days) ->
                val dDay = DueDateUtil.formatDDay(days)
                sendNotification(
                    id = entity.id.toInt(),
                    title = "$dDay ${entity.title}",
                    body = "마감일: ${entity.dueDate}",
                )
            }

            return Result.success()
        }

        private fun createNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel =
                    NotificationChannel(
                        CHANNEL_ID,
                        "마감일 리마인더",
                        NotificationManager.IMPORTANCE_DEFAULT,
                    ).apply {
                        description = "마감일이 임박한 할 일 알림"
                    }
                val manager =
                    applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager.createNotificationChannel(channel)
            }
        }

        private fun sendNotification(
            id: Int,
            title: String,
            body: String,
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permission =
                    ContextCompat.checkSelfPermission(
                        applicationContext,
                        Manifest.permission.POST_NOTIFICATIONS,
                    )
                if (permission != PackageManager.PERMISSION_GRANTED) return
            }

            val notification =
                NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .build()

            NotificationManagerCompat.from(applicationContext).notify(id, notification)
        }

        companion object {
            const val CHANNEL_ID = "reminder_channel"
        }
    }
