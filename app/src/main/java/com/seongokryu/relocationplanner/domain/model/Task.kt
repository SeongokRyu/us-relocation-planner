package com.seongokryu.relocationplanner.domain.model

enum class Category(val label: String, val icon: String) {
    VISA("비자 & 이민", "🛂"),
    HOUSING("주거", "🏠"),
    FINANCE("재정 & 은행", "💰"),
    CAREER("커리어 & 구직", "💼"),
    LOGISTICS("이사 & 생활", "📦"),
}

enum class Priority(val label: String, val level: Int) {
    HIGH("높음", 0),
    MEDIUM("보통", 1),
    LOW("낮음", 2),
}

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val category: Category,
    val priority: Priority = Priority.MEDIUM,
    val isDone: Boolean = false,
    val assignee: String = "",
    val dueDate: String? = null,
    val createdAt: String = "",
    val updatedAt: String = "",
)
