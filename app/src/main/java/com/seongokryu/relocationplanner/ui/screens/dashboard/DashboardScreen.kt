package com.seongokryu.relocationplanner.ui.screens.dashboard

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seongokryu.relocationplanner.data.export.PdfExporter
import com.seongokryu.relocationplanner.domain.model.Category
import com.seongokryu.relocationplanner.domain.model.DueDateUtil
import com.seongokryu.relocationplanner.domain.model.Task
import com.seongokryu.relocationplanner.domain.model.UrgencyLevel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DashboardScreen(
    onCategoryClick: (Category) -> Unit,
    onTimelineClick: () -> Unit = {},
    onExpenseClick: () -> Unit = {},
    onContactClick: () -> Unit = {},
    onExchangeClick: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val stats by viewModel.categoryStats.collectAsStateWithLifecycle()
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val isSearchActive by viewModel.isSearchActive.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val (done, total) = viewModel.totalProgress(stats)
    val highPriority = viewModel.getHighPriorityPending(tasks)
    val upcomingDeadlines = viewModel.getUpcomingDeadlines(tasks)

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
    ) {
        if (isSearchActive) {
            SearchResultsSection(
                results = searchResults,
                onTaskClick = { task -> onCategoryClick(task.category) },
            )
        } else {
            DashboardContent(
                done = done,
                total = total,
                stats = stats,
                highPriority = highPriority,
                upcomingDeadlines = upcomingDeadlines,
                onCategoryClick = onCategoryClick,
                onTimelineClick = onTimelineClick,
                onExpenseClick = onExpenseClick,
                onContactClick = onContactClick,
                onExchangeClick = onExchangeClick,
                allTasks = tasks,
            )
        }
    }
}

@Composable
private fun DashboardContent(
    done: Int,
    total: Int,
    stats: List<com.seongokryu.relocationplanner.data.local.dao.CategoryStat>,
    highPriority: List<Task>,
    upcomingDeadlines: List<Pair<Task, Int>>,
    onCategoryClick: (Category) -> Unit,
    onTimelineClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onContactClick: () -> Unit,
    onExchangeClick: () -> Unit,
    allTasks: List<Task>,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Circular progress
    Text("전체 진행률", style = MaterialTheme.typography.titleLarge)
    Spacer(modifier = Modifier.height(16.dp))

    if (total > 0) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            CircularProgressChart(done = done, total = total)
        }
    } else {
        Text("아직 등록된 할 일이 없습니다.")
    }

    Spacer(modifier = Modifier.height(12.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
    ) {
        Button(onClick = onTimelineClick) {
            Text("타임라인")
        }
        Button(
            onClick = onExpenseClick,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                ),
        ) {
            Text("비용")
        }
        Button(
            onClick = onContactClick,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                ),
        ) {
            Text("연락처")
        }
        Button(
            onClick = onExchangeClick,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                ),
        ) {
            Text("환율")
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        FilledTonalButton(
            onClick = {
                scope.launch {
                    val file =
                        withContext(Dispatchers.IO) {
                            PdfExporter.exportToPdf(context, allTasks)
                        }
                    val uri =
                        FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            file,
                        )
                    val shareIntent =
                        Intent(Intent.ACTION_SEND).apply {
                            type = "application/pdf"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    context.startActivity(
                        Intent.createChooser(shareIntent, "체크리스트 내보내기"),
                    )
                }
            },
        ) {
            Text("PDF 내보내기")
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Per-category cards
    Text("카테고리별 현황", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    Category.entries.forEach { category ->
        val stat = stats.find { it.category == category.name }
        val catTotal = stat?.total ?: 0
        val catDone = stat?.done ?: 0

        CategoryCard(
            category = category,
            done = catDone,
            total = catTotal,
            onClick = { onCategoryClick(category) },
        )
    }

    // Upcoming deadlines
    if (upcomingDeadlines.isNotEmpty()) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "마감 임박 (${upcomingDeadlines.size})",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))

        upcomingDeadlines.forEach { (task, days) ->
            val urgency = DueDateUtil.urgencyLevel(days)
            val dDay = DueDateUtil.formatDDay(days)
            val urgencyColor =
                when (urgency) {
                    UrgencyLevel.OVERDUE -> Color(0xFFE53935)
                    UrgencyLevel.TODAY -> Color(0xFFFF9800)
                    UrgencyLevel.APPROACHING -> Color(0xFFFFC107)
                    UrgencyLevel.NORMAL -> MaterialTheme.colorScheme.onSurface
                }

            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .clickable { onCategoryClick(task.category) },
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        dDay,
                        color = urgencyColor,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "${task.category.icon} ${task.title}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // High priority pending
    Text("미완료 고우선순위", style = MaterialTheme.typography.titleMedium)
    Spacer(modifier = Modifier.height(8.dp))

    if (highPriority.isEmpty()) {
        Text("모든 고우선순위 항목 완료!", style = MaterialTheme.typography.bodyMedium)
    } else {
        highPriority.forEach { task ->
            Text(
                "${task.category.icon} ${task.title}",
                modifier = Modifier.padding(vertical = 2.dp),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: Category,
    done: Int,
    total: Int,
    onClick: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        category.icon,
                        fontSize = 24.sp,
                    )
                    Text(
                        category.label,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium,
                    )
                }
                Text(
                    "$done / $total",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            if (total > 0) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { done.toFloat() / total },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun SearchResultsSection(
    results: List<Task>,
    onTaskClick: (Task) -> Unit,
) {
    if (results.isEmpty()) {
        Text(
            "검색 결과가 없습니다.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    } else {
        Text(
            "검색 결과 (${results.size}건)",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.height(8.dp))

        results.forEach { task ->
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { onTaskClick(task) },
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            task.title,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f),
                        )
                        Text(
                            task.priority.label,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                    if (task.description.isNotBlank()) {
                        Text(
                            task.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                    }
                    Text(
                        "${task.category.icon} ${task.category.label}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
