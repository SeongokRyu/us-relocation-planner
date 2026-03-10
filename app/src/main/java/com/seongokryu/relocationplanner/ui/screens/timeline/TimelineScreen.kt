package com.seongokryu.relocationplanner.ui.screens.timeline

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seongokryu.relocationplanner.domain.model.DueDateUtil
import com.seongokryu.relocationplanner.domain.model.Task
import com.seongokryu.relocationplanner.domain.model.UrgencyLevel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TimelineScreen(viewModel: TimelineViewModel = hiltViewModel()) {
    val groups by viewModel.timelineGroups.collectAsStateWithLifecycle()
    val undated by viewModel.undatedTasks.collectAsStateWithLifecycle()

    if (groups.isEmpty() && undated.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("등록된 할 일이 없습니다.", style = MaterialTheme.typography.bodyLarge)
        }
        return
    }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        groups.forEach { group ->
            item {
                MonthHeader(label = group.label)
            }
            items(group.tasks, key = { "dated-${it.id}" }) { task ->
                TimelineTaskRow(task = task)
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        if (undated.isNotEmpty()) {
            item {
                MonthHeader(label = "미정")
            }
            items(undated, key = { "undated-${it.id}" }) { task ->
                TimelineTaskRow(task = task)
            }
        }
    }
}

@Composable
private fun MonthHeader(label: String) {
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider()
    Text(
        label,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp),
    )
}

@Composable
private fun TimelineTaskRow(task: Task) {
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    val days = DueDateUtil.daysUntil(task.dueDate, today)

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Date column
        Text(
            task.dueDate?.takeLast(5) ?: "",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Checkbox(
            checked = task.isDone,
            onCheckedChange = null,
            enabled = false,
        )

        Text(
            "${task.category.icon} ${task.title}",
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                ),
            modifier = Modifier.weight(1f),
            color =
                if (task.isDone) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurface
                },
        )

        if (!task.isDone && days != null) {
            val dDay = DueDateUtil.formatDDay(days)
            val urgencyColor =
                when (DueDateUtil.urgencyLevel(days)) {
                    UrgencyLevel.OVERDUE -> Color(0xFFE53935)
                    UrgencyLevel.TODAY -> Color(0xFFFF9800)
                    UrgencyLevel.APPROACHING -> Color(0xFFFFC107)
                    UrgencyLevel.NORMAL -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            Text(
                dDay,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = urgencyColor,
            )
        }
    }
}
