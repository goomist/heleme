package com.example.appdemo

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private enum class SettingsSubScreen {
    Menu,
    WebDav,
    Trash,
}

// Written into the backup payload and verified on restore — never translated. See WebDavBackupScreen.
private const val MILK_TEA_BACKUP_RECORD_TYPE = "奶茶记录"

@Composable
internal fun SettingsScreen(
    allRecords: List<MilkTeaRecord>,
    onRestoreRecord: (MilkTeaRecord) -> Unit,
    onPermanentlyDelete: (MilkTeaRecord) -> Unit,
    onWebDavRestored: () -> Unit,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
) {
    val strings = LocalMilkTeaStrings.current
    var subScreen by rememberSaveable { mutableStateOf(SettingsSubScreen.Menu.name) }
    var showLanguagePicker by remember { mutableStateOf(false) }
    val trashedRecords = remember(allRecords) {
        allRecords.filter { it.deletedAtMillis > 0L }.sortedByDescending { it.deletedAtMillis }
    }

    BackHandler(enabled = subScreen != SettingsSubScreen.Menu.name) {
        subScreen = SettingsSubScreen.Menu.name
    }

    when (SettingsSubScreen.valueOf(subScreen)) {
        SettingsSubScreen.Menu -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item {
                    SettingsRow(
                        title = strings.languageRowTitle,
                        subtitle = strings.languageName,
                        onClick = { showLanguagePicker = true },
                    )
                }
                item {
                    SettingsRow(
                        title = strings.webdavRowTitle,
                        subtitle = strings.webdavRowSubtitle,
                        onClick = { subScreen = SettingsSubScreen.WebDav.name },
                    )
                }
                item {
                    SettingsRow(
                        title = strings.trashRowTitle,
                        subtitle = if (trashedRecords.isEmpty()) {
                            strings.trashEmptySubtitle
                        } else {
                            strings.trashRowSubtitle(trashedRecords.size)
                        },
                        onClick = { subScreen = SettingsSubScreen.Trash.name },
                    )
                }
            }
        }

        SettingsSubScreen.WebDav -> {
            Column(modifier = Modifier.fillMaxSize()) {
                SettingsSubHeader(title = strings.webdavRowTitle, onBack = { subScreen = SettingsSubScreen.Menu.name })
                WebDavBackupScreen(
                    backupFileName = "milk-tea-records.backup",
                    recordsKey = "records",
                    recordType = MILK_TEA_BACKUP_RECORD_TYPE,
                    recordTypeLabel = strings.webdavRecordTypeLabel,
                    onRestored = onWebDavRestored,
                )
            }
        }

        SettingsSubScreen.Trash -> {
            Column(modifier = Modifier.fillMaxSize()) {
                SettingsSubHeader(title = strings.trashRowTitle, onBack = { subScreen = SettingsSubScreen.Menu.name })
                TrashList(
                    trashedRecords = trashedRecords,
                    onRestore = onRestoreRecord,
                    onPermanentlyDelete = onPermanentlyDelete,
                )
            }
        }
    }

    if (showLanguagePicker) {
        LanguagePickerDialog(
            current = currentLanguage,
            onPick = {
                onLanguageChange(it)
                showLanguagePicker = false
            },
            onDismiss = { showLanguagePicker = false },
        )
    }
}

@Composable
private fun LanguagePickerDialog(
    current: AppLanguage,
    onPick: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    val strings = LocalMilkTeaStrings.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.languageRowTitle) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                AppLanguage.entries.forEach { language ->
                    val selected = language == current
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onPick(language) }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        // Each option is written in its own language, so it stays readable no
                        // matter which one is currently active.
                        Text(
                            stringsFor(language).languageName,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                        if (selected) {
                            Text("✓", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text(strings.cancel) }
        },
    )
}

@Composable
private fun SettingsSubHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(onClick = onBack) { Text(LocalMilkTeaStrings.current.back) }
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun TrashList(
    trashedRecords: List<MilkTeaRecord>,
    onRestore: (MilkTeaRecord) -> Unit,
    onPermanentlyDelete: (MilkTeaRecord) -> Unit,
) {
    val strings = LocalMilkTeaStrings.current
    var pendingDelete by remember { mutableStateOf<MilkTeaRecord?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (trashedRecords.isEmpty()) {
            item {
                Text(strings.trashEmptyBody, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(trashedRecords, key = { it.id }) { record ->
                TrashRecordCard(
                    record = record,
                    onRestore = { onRestore(record) },
                    onDeleteForever = { pendingDelete = record },
                )
            }
        }
    }

    pendingDelete?.let { record ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text(strings.deleteForeverTitle) },
            text = { Text(strings.deleteForeverBody) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onPermanentlyDelete(record)
                        pendingDelete = null
                    },
                ) { Text(strings.deleteForeverAction) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text(strings.cancel) }
            },
        )
    }
}

@Composable
private fun TrashRecordCard(
    record: MilkTeaRecord,
    onRestore: () -> Unit,
    onDeleteForever: () -> Unit,
) {
    val strings = LocalMilkTeaStrings.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(record.brand, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            if (record.productName.isNotBlank()) {
                Text(record.productName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(formatDateWithPeriod(record.drinkTimeMillis, strings), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            val remainingDays = daysUntilPermanentDelete(record.deletedAtMillis)
            Text(
                strings.trashRemaining(remainingDays),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onRestore) { Text(strings.restore) }
                TextButton(onClick = onDeleteForever) { Text(strings.deleteForeverAction) }
            }
        }
    }
}
