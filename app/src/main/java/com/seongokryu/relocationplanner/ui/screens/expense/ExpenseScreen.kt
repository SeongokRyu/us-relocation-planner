package com.seongokryu.relocationplanner.ui.screens.expense

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.Expense
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ExpenseScreen(viewModel: ExpenseViewModel = hiltViewModel()) {
    val expenses by viewModel.expenses.collectAsStateWithLifecycle()
    val total by viewModel.currencyTotal.collectAsStateWithLifecycle()
    val categoryTotals by viewModel.categoryTotals.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "지출 추가")
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Total summary
            item {
                TotalSummaryCard(total)
            }

            // Category breakdown
            if (categoryTotals.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "카테고리별 합계",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
                items(categoryTotals) { ct ->
                    CategoryTotalRow(ct)
                }
            }

            // Recent expenses
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "최근 지출 (${expenses.size})",
                    style = MaterialTheme.typography.titleMedium,
                )
            }

            if (expenses.isEmpty()) {
                item {
                    Text(
                        "아직 등록된 지출이 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseCard(
                        expense = expense,
                        onDelete = { viewModel.deleteExpense(expense) },
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddExpenseDialog(
            onDismiss = { showDialog = false },
            onConfirm = { expense ->
                viewModel.addExpense(expense)
                showDialog = false
            },
        )
    }
}

@Composable
private fun TotalSummaryCard(total: CurrencyTotal) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "총 지출",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (total.krw > 0) {
                Text(
                    ExpenseViewModel.formatKrw(total.krw),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            if (total.usd > 0) {
                Text(
                    ExpenseViewModel.formatUsd(total.usd),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
            if (total.krw == 0.0 && total.usd == 0.0) {
                Text(
                    "₩0",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun CategoryTotalRow(ct: CategoryTotal) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "${ct.category.icon} ${ct.category.label}",
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            if (ct.currency == "KRW") {
                ExpenseViewModel.formatKrw(ct.amount)
            } else {
                ExpenseViewModel.formatUsd(ct.amount)
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ExpenseCard(
    expense: Expense,
    onDelete: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    expense.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        "${expense.category.icon} ${expense.category.label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    if (expense.date.isNotBlank()) {
                        Text(
                            expense.date,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            Text(
                if (expense.currency == "KRW") {
                    ExpenseViewModel.formatKrw(expense.amount)
                } else {
                    ExpenseViewModel.formatUsd(expense.amount)
                },
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "삭제",
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onConfirm: (Expense) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var currency by remember { mutableStateOf("KRW") }
    var selectedCategory by remember { mutableStateOf(Category.DOCUMENTS) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var currencyExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("지출 추가") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("항목명") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { newVal ->
                            if (newVal.isEmpty() || newVal.toDoubleOrNull() != null) {
                                amount = newVal
                            }
                        },
                        label = { Text("금액") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )

                    ExposedDropdownMenuBox(
                        expanded = currencyExpanded,
                        onExpandedChange = { currencyExpanded = it },
                        modifier = Modifier.width(120.dp),
                    ) {
                        OutlinedTextField(
                            value = currency,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("통화") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded)
                            },
                            modifier =
                                Modifier.menuAnchor(
                                    type = MenuAnchorType.PrimaryNotEditable,
                                ),
                        )
                        ExposedDropdownMenu(
                            expanded = currencyExpanded,
                            onDismissRequest = { currencyExpanded = false },
                        ) {
                            listOf("KRW", "USD").forEach { cur ->
                                DropdownMenuItem(
                                    text = { Text(cur) },
                                    onClick = {
                                        currency = cur
                                        currencyExpanded = false
                                    },
                                )
                            }
                        }
                    }
                }

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it },
                ) {
                    OutlinedTextField(
                        value = "${selectedCategory.icon} ${selectedCategory.label}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("카테고리") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                        },
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .menuAnchor(type = MenuAnchorType.PrimaryNotEditable),
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false },
                    ) {
                        Category.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.icon} ${cat.label}") },
                                onClick = {
                                    selectedCategory = cat
                                    categoryExpanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val parsedAmount = amount.toDoubleOrNull() ?: return@TextButton
                    if (title.isBlank()) return@TextButton
                    onConfirm(
                        Expense(
                            title = title,
                            amount = parsedAmount,
                            currency = currency,
                            category = selectedCategory,
                            date =
                                LocalDate.now()
                                    .format(DateTimeFormatter.ISO_LOCAL_DATE),
                        ),
                    )
                },
            ) {
                Text("추가")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        },
    )
}
