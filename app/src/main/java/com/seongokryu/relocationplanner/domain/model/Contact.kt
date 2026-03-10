package com.seongokryu.relocationplanner.domain.model

data class Contact(
    val id: Long = 0,
    val name: String,
    val role: String = "",
    val phone: String = "",
    val email: String = "",
    val note: String = "",
    val createdAt: String = "",
)
