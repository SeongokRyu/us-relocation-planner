package com.seongokryu.relocationplanner.ui.screens.expense

import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Expense
import org.junit.Assert.assertEquals
import org.junit.Test

class ExpenseCalculationTest {
    @Test
    fun should_calculate_total_by_currency() {
        val expenses =
            listOf(
                Expense(title = "A", amount = 100000.0, currency = "KRW", category = Category.DOCUMENTS),
                Expense(title = "B", amount = 200000.0, currency = "KRW", category = Category.VISA),
                Expense(title = "C", amount = 1500.0, currency = "USD", category = Category.HOUSING),
                Expense(title = "D", amount = 500.0, currency = "USD", category = Category.TRANSPORT),
            )

        val krwTotal = expenses.filter { it.currency == "KRW" }.sumOf { it.amount }
        val usdTotal = expenses.filter { it.currency == "USD" }.sumOf { it.amount }

        assertEquals(300000.0, krwTotal, 0.01)
        assertEquals(2000.0, usdTotal, 0.01)
    }

    @Test
    fun should_group_expenses_by_category() {
        val expenses =
            listOf(
                Expense(title = "A", amount = 100000.0, currency = "KRW", category = Category.DOCUMENTS),
                Expense(title = "B", amount = 50000.0, currency = "KRW", category = Category.DOCUMENTS),
                Expense(title = "C", amount = 1500.0, currency = "USD", category = Category.HOUSING),
            )

        val grouped =
            expenses.groupBy { it.category to it.currency }
                .map { (key, items) ->
                    CategoryTotal(
                        category = key.first,
                        amount = items.sumOf { it.amount },
                        currency = key.second,
                    )
                }

        assertEquals(2, grouped.size)
        val docs = grouped.find { it.category == Category.DOCUMENTS }!!
        assertEquals(150000.0, docs.amount, 0.01)
        assertEquals("KRW", docs.currency)
    }

    @Test
    fun should_format_krw_correctly() {
        assertEquals("₩350,000", ExpenseViewModel.formatKrw(350000.0))
        assertEquals("₩0", ExpenseViewModel.formatKrw(0.0))
    }

    @Test
    fun should_format_usd_correctly() {
        assertEquals("$1,500.00", ExpenseViewModel.formatUsd(1500.0))
        assertEquals("$0.00", ExpenseViewModel.formatUsd(0.0))
    }
}
