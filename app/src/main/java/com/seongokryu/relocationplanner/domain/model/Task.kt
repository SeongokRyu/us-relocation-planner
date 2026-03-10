package com.seongokryu.relocationplanner.domain.model

enum class Category(val label: String, val icon: String) {
    VISA("비자 & 이민", "🛂"),
    DOCUMENTS("서류 & 행정", "📋"),
    HOUSING("주거", "🏠"),
    FINANCE("재정 & 세금", "💰"),
    CAREER("커리어 & 구직", "💼"),
    MEDICAL("의료 & 건강", "🏥"),
    TRANSPORT("교통 & 자동차", "🚗"),
    KOREA_DEPARTURE("한국 정리 & 출국", "🇰🇷"),
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
    val guide: String = "",
    val referenceUrl: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
)
