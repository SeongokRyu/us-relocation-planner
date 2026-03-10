package com.seongokryu.relocationplanner.domain.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.ChronoUnit

enum class UrgencyLevel {
    OVERDUE,
    TODAY,
    APPROACHING,
    NORMAL,
}

object DueDateUtil {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

    fun daysUntil(
        dueDate: String?,
        today: String,
    ): Int? {
        if (dueDate.isNullOrBlank()) return null
        return try {
            val due = LocalDate.parse(dueDate, formatter)
            val now = LocalDate.parse(today, formatter)
            ChronoUnit.DAYS.between(now, due).toInt()
        } catch (_: DateTimeParseException) {
            null
        }
    }

    fun formatDDay(days: Int): String =
        when {
            days > 0 -> "D-$days"
            days == 0 -> "D-Day"
            else -> "D+${-days}"
        }

    fun urgencyLevel(days: Int): UrgencyLevel =
        when {
            days < 0 -> UrgencyLevel.OVERDUE
            days == 0 -> UrgencyLevel.TODAY
            days <= 3 -> UrgencyLevel.APPROACHING
            else -> UrgencyLevel.NORMAL
        }
}
