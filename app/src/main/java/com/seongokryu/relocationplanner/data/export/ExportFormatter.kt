package com.seongokryu.relocationplanner.data.export

import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Task

data class ExportStats(
    val total: Int,
    val done: Int,
    val progressPercent: Int,
)

data class CategoryExportData(
    val category: Category,
    val tasks: List<Task>,
    val done: Int,
    val total: Int,
)

object ExportFormatter {
    fun formatTaskForExport(task: Task): String {
        val check = if (task.isDone) "[x]" else "[ ]"
        val priority = "(${task.priority.label})"
        val dueDate = task.dueDate?.let { " ~$it" } ?: ""
        return "$check $priority ${task.title}$dueDate"
    }

    fun calculateExportStats(tasks: List<Task>): ExportStats {
        val total = tasks.size
        val done = tasks.count { it.isDone }
        val percent = if (total > 0) (done * 100) / total else 0
        return ExportStats(total = total, done = done, progressPercent = percent)
    }

    fun groupByCategory(tasks: List<Task>): List<CategoryExportData> =
        Category.entries.mapNotNull { category ->
            val catTasks = tasks.filter { it.category == category }
            if (catTasks.isEmpty()) {
                null
            } else {
                CategoryExportData(
                    category = category,
                    tasks = catTasks,
                    done = catTasks.count { it.isDone },
                    total = catTasks.size,
                )
            }
        }
}
