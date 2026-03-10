package com.seongokryu.relocationplanner.data.repository

import com.seongokryu.relocationplanner.data.local.dao.ExpenseDao
import com.seongokryu.relocationplanner.data.local.entity.ExpenseEntity
import com.seongokryu.relocationplanner.domain.model.Expense
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExpenseRepository
    @Inject
    constructor(
        private val expenseDao: ExpenseDao,
    ) {
        fun getAllExpenses(): Flow<List<Expense>> = expenseDao.getAllExpenses().map { entities -> entities.map { it.toDomain() } }

        suspend fun addExpense(expense: Expense): Long {
            val now = Instant.now().toString()
            return expenseDao.insert(ExpenseEntity.fromDomain(expense.copy(createdAt = now)))
        }

        suspend fun deleteExpense(expense: Expense) {
            expenseDao.delete(ExpenseEntity.fromDomain(expense))
        }
    }
