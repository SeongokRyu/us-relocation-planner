package com.seongokryu.relocationplanner.domain.model

data class Note(
    val id: Long = 0,
    val taskId: Long,
    val content: String,
    val createdAt: String = "",
)
