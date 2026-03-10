package com.seongokryu.relocationplanner.ui.screens.contact

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.seongokryu.relocationplanner.domain.model.Contact

@Composable
fun ContactScreen(viewModel: ContactViewModel = hiltViewModel()) {
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "연락처 추가")
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
            if (contacts.isEmpty()) {
                item {
                    Text(
                        "아직 등록된 연락처가 없습니다.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(contacts, key = { it.id }) { contact ->
                    ContactCard(
                        contact = contact,
                        onDelete = { viewModel.deleteContact(contact) },
                    )
                }
            }
        }
    }

    if (showDialog) {
        AddContactDialog(
            onDismiss = { showDialog = false },
            onConfirm = { contact ->
                viewModel.addContact(contact)
                showDialog = false
            },
        )
    }
}

@Composable
private fun ContactCard(
    contact: Contact,
    onDelete: () -> Unit,
) {
    val context = LocalContext.current
    val roleIcon = getRoleIcon(contact.role)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (contact.role.isNotBlank()) {
                        Text(
                            "$roleIcon ${contact.role}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Text(
                        contact.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "삭제",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (contact.phone.isNotBlank()) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.phone}"))
                            context.startActivity(intent)
                        },
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "전화",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        contact.phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (contact.email.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            val intent =
                                Intent(
                                    Intent.ACTION_SENDTO,
                                    Uri.parse("mailto:${contact.email}"),
                                )
                            context.startActivity(intent)
                        },
                    ) {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "이메일",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    Text(
                        contact.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (contact.note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    contact.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private fun getRoleIcon(role: String): String =
    when {
        role.contains("변호사") || role.contains("법") -> "\u2696\uFE0F"
        role.contains("부동산") || role.contains("에이전트") -> "\uD83C\uDFE0"
        role.contains("은행") || role.contains("재정") -> "\uD83C\uDFE6"
        role.contains("의사") || role.contains("병원") -> "\uD83C\uDFE5"
        role.contains("회사") || role.contains("직장") -> "\uD83C\uDFE2"
        else -> "\uD83D\uDC64"
    }

@Composable
private fun AddContactDialog(
    onDismiss: () -> Unit,
    onConfirm: (Contact) -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("연락처 추가") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("이름") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("역할 (예: 이민 변호사)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("전화번호") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("이메일") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("메모") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (name.isBlank()) return@TextButton
                    onConfirm(
                        Contact(
                            name = name,
                            role = role,
                            phone = phone,
                            email = email,
                            note = note,
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
