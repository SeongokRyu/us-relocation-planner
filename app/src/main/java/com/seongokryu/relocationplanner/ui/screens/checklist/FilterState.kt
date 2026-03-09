package com.seongokryu.relocationplanner.ui.screens.checklist

enum class StatusFilter(val label: String) {
    ALL("전체"),
    PENDING("미완료"),
    DONE("완료"),
}

enum class PriorityFilter(val label: String) {
    ALL("전체"),
    HIGH("높음"),
    MEDIUM("보통"),
    LOW("낮음"),
}

data class FilterState(
    val statusFilter: StatusFilter = StatusFilter.ALL,
    val priorityFilter: PriorityFilter = PriorityFilter.ALL,
    val assigneeFilter: String = "",
)
