package com.seongokryu.relocationplanner.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seongokryu.relocationplanner.data.repository.ExpenseRepository
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Expense
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

data class CurrencyTotal(
    val krw: Double = 0.0,
    val usd: Double = 0.0,
)

data class CategoryTotal(
    val category: Category,
    val amount: Double,
    val currency: String,
)

@HiltViewModel
class ExpenseViewModel
    @Inject
    constructor(
        private val repository: ExpenseRepository,
    ) : ViewModel() {
        val expenses: StateFlow<List<Expense>> =
            repository.getAllExpenses()
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        val currencyTotal: StateFlow<CurrencyTotal> =
            expenses.map { list ->
                CurrencyTotal(
                    krw = list.filter { it.currency == "KRW" }.sumOf { it.amount },
                    usd = list.filter { it.currency == "USD" }.sumOf { it.amount },
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CurrencyTotal())

        val categoryTotals: StateFlow<List<CategoryTotal>> =
            expenses.map { list ->
                list.groupBy { it.category to it.currency }
                    .map { (key, items) ->
                        CategoryTotal(
                            category = key.first,
                            amount = items.sumOf { it.amount },
                            currency = key.second,
                        )
                    }
                    .sortedBy { it.category.ordinal }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        fun addExpense(expense: Expense) {
            viewModelScope.launch {
                repository.addExpense(expense)
            }
        }

        fun deleteExpense(expense: Expense) {
            viewModelScope.launch {
                repository.deleteExpense(expense)
            }
        }

        companion object {
            fun formatKrw(amount: Double): String {
                val format = NumberFormat.getNumberInstance(Locale.KOREA)
                return "₩${format.format(amount.toLong())}"
            }

            fun formatUsd(amount: Double): String {
                val format = NumberFormat.getNumberInstance(Locale.US)
                format.minimumFractionDigits = 2
                format.maximumFractionDigits = 2
                return "$${format.format(amount)}"
            }
        }
    }
