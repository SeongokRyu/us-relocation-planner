package com.seongokryu.relocationplanner.ui.screens.detail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seongokryu.relocationplanner.domain.model.DueDateUtil
import com.seongokryu.relocationplanner.domain.model.Note
import com.seongokryu.relocationplanner.domain.model.Task
import com.seongokryu.relocationplanner.domain.model.UrgencyLevel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun TaskDetailScreen(viewModel: TaskDetailViewModel = hiltViewModel()) {
    val task by viewModel.task.collectAsStateWithLifecycle()
    val notes by viewModel.notes.collectAsStateWithLifecycle()

    task?.let { t ->
        TaskDetailContent(
            task = t,
            notes = notes,
            onToggle = { viewModel.toggleTask() },
            onAddNote = { viewModel.addNote(it) },
            onDeleteNote = { viewModel.deleteNote(it) },
        )
    }
}

@Composable
private fun TaskDetailContent(
    task: Task,
    notes: List<Note>,
    onToggle: () -> Unit,
    onAddNote: (String) -> Unit,
    onDeleteNote: (Note) -> Unit,
) {
    var noteInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        item {
            TaskInfoSection(task = task, onToggle = onToggle)
        }

        if (task.guide.isNotBlank()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))
                GuideSection(guide = task.guide, referenceUrl = task.referenceUrl)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "메모 (${notes.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(notes, key = { it.id }) { note ->
            NoteCard(note = note, onDelete = { onDeleteNote(note) })
        }

        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    placeholder = { Text("메모 입력...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                )
                IconButton(
                    onClick = {
                        onAddNote(noteInput)
                        noteInput = ""
                    },
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "추가")
                }
            }
        }
    }
}

@Composable
private fun TaskInfoSection(
    task: Task,
    onToggle: () -> Unit,
) {
    val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
    val days = DueDateUtil.daysUntil(task.dueDate, today)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(checked = task.isDone, onCheckedChange = { onToggle() })
        Text(
            if (task.isDone) "완료" else "미완료",
            style = MaterialTheme.typography.bodyMedium,
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    InfoRow(label = "카테고리", value = "${task.category.icon} ${task.category.label}")
    InfoRow(label = "우선순위", value = task.priority.label)
    if (task.assignee.isNotBlank()) {
        InfoRow(label = "담당자", value = task.assignee)
    }
    if (!task.dueDate.isNullOrBlank()) {
        val dDay = days?.let { " (${DueDateUtil.formatDDay(it)})" } ?: ""
        val dueDateColor =
            if (!task.isDone && days != null) {
                when (DueDateUtil.urgencyLevel(days)) {
                    UrgencyLevel.OVERDUE -> Color(0xFFE53935)
                    UrgencyLevel.TODAY -> Color(0xFFFF9800)
                    UrgencyLevel.APPROACHING -> Color(0xFFFFC107)
                    UrgencyLevel.NORMAL -> MaterialTheme.colorScheme.onSurface
                }
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        InfoRow(label = "마감일", value = "${task.dueDate}$dDay", valueColor = dueDateColor)
    }
    if (task.description.isNotBlank()) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            task.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
) {
    Row(
        modifier = Modifier.padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
        )
    }
}

@Composable
private fun GuideSection(
    guide: String,
    referenceUrl: String,
) {
    val context = LocalContext.current

    Text(
        "수행 가이드",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
            ),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                guide,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer,
            )
            if (referenceUrl.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "참고 링크 열기",
                    style =
                        MaterialTheme.typography.bodyMedium.copy(
                            textDecoration = TextDecoration.Underline,
                        ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier =
                        Modifier.clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(referenceUrl))
                            context.startActivity(intent)
                        },
                )
            }
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onDelete: () -> Unit,
) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    note.content,
                    style = MaterialTheme.typography.bodyMedium,
                )
                val displayDate = note.createdAt.take(10)
                if (displayDate.isNotBlank()) {
                    Text(
                        displayDate,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
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
