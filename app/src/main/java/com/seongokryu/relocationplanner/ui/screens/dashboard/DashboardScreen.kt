package com.seongokryu.relocationplanner.ui.screens.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seongokryu.relocationplanner.domain.model.Category

@Composable
fun DashboardScreen(
    onCategoryClick: (Category) -> Unit,
    viewModel: DashboardViewModel = hiltViewModel(),
) {
    val stats by viewModel.categoryStats.collectAsStateWithLifecycle()
    val tasks by viewModel.allTasks.collectAsStateWithLifecycle()
    val (done, total) = viewModel.totalProgress(stats)
    val highPriority = viewModel.getHighPriorityPending(tasks)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        // Overall progress
        Text("전체 진행률", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        if (total > 0) {
            val progress = done.toFloat() / total
            Text("$done / $total (${(progress * 100).toInt()}%)")
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
            )
        } else {
            Text("아직 등록된 할 일이 없습니다.")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Per-category cards
        Text("카테고리별 현황", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Category.entries.forEach { category ->
            val stat = stats.find { it.category == category.name }
            val catTotal = stat?.total ?: 0
            val catDone = stat?.done ?: 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onCategoryClick(category) },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("${category.icon} ${category.label}")
                    Text(
                        "$catDone / $catTotal",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
                if (catTotal > 0) {
                    LinearProgressIndicator(
                        progress = { catDone.toFloat() / catTotal },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 12.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // High priority pending
        Text("🔴 미완료 고우선순위", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        if (highPriority.isEmpty()) {
            Text("모든 고우선순위 항목 완료! 🎉", style = MaterialTheme.typography.bodyMedium)
        } else {
            highPriority.forEach { task ->
                Text(
                    "• ${task.category.icon} ${task.title}",
                    modifier = Modifier.padding(vertical = 2.dp),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
