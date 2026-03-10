package com.seongokryu.relocationplanner.domain.model

data class Expense(
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val currency: String = "KRW",
    val category: Category,
    val date: String = "",
    val note: String = "",
    val createdAt: String = "",
)
