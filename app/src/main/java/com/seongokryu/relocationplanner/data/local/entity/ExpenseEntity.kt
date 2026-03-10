package com.seongokryu.relocationplanner.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Expense

@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val currency: String = "KRW",
    val category: String,
    val date: String = "",
    val note: String = "",
    @ColumnInfo(name = "created_at")
    val createdAt: String = "",
) {
    fun toDomain(): Expense =
        Expense(
            id = id,
            title = title,
            amount = amount,
            currency = currency,
            category = Category.valueOf(category),
            date = date,
            note = note,
            createdAt = createdAt,
        )

    companion object {
        fun fromDomain(expense: Expense): ExpenseEntity =
            ExpenseEntity(
                id = expense.id,
                title = expense.title,
                amount = expense.amount,
                currency = expense.currency,
                category = expense.category.name,
                date = expense.date,
                note = expense.note,
                createdAt = expense.createdAt,
            )
    }
}
