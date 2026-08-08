package com.example.appdemo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
@OptIn(ExperimentalMaterial3Api::class)
internal fun MilkTeaAppEntry() {
    val context = LocalContext.current

    var language by rememberSaveable { mutableStateOf(loadLanguage(context).name) }
    val strings = remember(language) { stringsFor(AppLanguage.valueOf(language)) }

    var records by remember { mutableStateOf(loadAndPurgeRecords(context)) }
    var selectedRecord by remember { mutableStateOf<MilkTeaRecord?>(null) }
    var editingRecord by remember { mutableStateOf<MilkTeaRecord?>(null) }

    var brandInput by rememberSaveable { mutableStateOf("") }
    var productNameInput by rememberSaveable { mutableStateOf("") }
    var amountInput by rememberSaveable { mutableStateOf("") }
    var noteInput by rememberSaveable { mutableStateOf("") }
    var selectedSugar by rememberSaveable { mutableStateOf(sugarOptions.first()) }
    var selectedIce by rememberSaveable { mutableStateOf(iceOptions.first()) }
    var selectedCupSize by rememberSaveable { mutableStateOf(cupSizeOptions.first()) }
    var selectedDrinkTimeMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

    var currentScreenName by rememberSaveable { mutableStateOf(HomeScreen.Calendar.name) }
    var monthStartMillis by rememberSaveable { mutableStateOf(startOfMonth(System.currentTimeMillis())) }
    var selectedDayStart by rememberSaveable { mutableStateOf(startOfDay(System.currentTimeMillis())) }
    var showMonthPickerDialog by remember { mutableStateOf(false) }

    var statsModeName by rememberSaveable { mutableStateOf(StatsMode.Week.name) }
    var weekAnchorMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var monthAnchorMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var yearAnchorMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

    val currentScreen = remember(currentScreenName) { HomeScreen.valueOf(currentScreenName) }
    val statsMode = remember(statsModeName) { StatsMode.valueOf(statsModeName) }
    val todayStart = remember { startOfDay(System.currentTimeMillis()) }

    val activeRecords = remember(records) {
        records.filter { it.deletedAtMillis == 0L }
    }
    val recordsByDayCount = remember(activeRecords) {
        activeRecords.groupingBy { startOfDay(it.drinkTimeMillis) }.eachCount()
    }
    val selectedDayRecords = remember(activeRecords, selectedDayStart) {
        activeRecords.filter { startOfDay(it.drinkTimeMillis) == selectedDayStart }
            .sortedByDescending { it.drinkTimeMillis }
    }

    val isDark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (isDark) MilkTeaDarkColorScheme else MilkTeaLightColorScheme,
    ) {
    CompositionLocalProvider(LocalMilkTeaStrings provides strings) {
    Scaffold(
        floatingActionButton = {
            if (currentScreen == HomeScreen.Calendar) {
                FloatingActionButton(
                    onClick = {
                        val today = startOfDay(System.currentTimeMillis())
                        selectedDayStart = today
                        monthStartMillis = startOfMonth(today)
                    },
                ) {
                    Text(strings.today)
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = currentScreen == HomeScreen.Calendar,
                    onClick = { currentScreenName = HomeScreen.Calendar.name },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.milktea_nav_calendar),
                            contentDescription = strings.navCalendar,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = { Text(strings.navCalendar) },
                )
                NavigationBarItem(
                    selected = currentScreen == HomeScreen.Records,
                    onClick = { currentScreenName = HomeScreen.Records.name },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.milktea_nav_drink),
                            contentDescription = strings.navRecords,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = { Text(strings.navRecords) },
                )
                NavigationBarItem(
                    selected = currentScreen == HomeScreen.Stats,
                    onClick = { currentScreenName = HomeScreen.Stats.name },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.milktea_nav_stats),
                            contentDescription = strings.navStats,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = { Text(strings.navStats) },
                )
                NavigationBarItem(
                    selected = currentScreen == HomeScreen.Settings,
                    onClick = { currentScreenName = HomeScreen.Settings.name },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.milktea_nav_settings),
                            contentDescription = strings.navSettings,
                            modifier = Modifier.size(24.dp),
                        )
                    },
                    label = { Text(strings.navSettings) },
                )
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when (currentScreen) {
                HomeScreen.Calendar -> {
                    MilkTeaCalendarScreen(
                        monthStartMillis = monthStartMillis,
                        selectedDayStart = selectedDayStart,
                        todayStart = todayStart,
                        recordsByDayCount = recordsByDayCount,
                        selectedDayRecords = selectedDayRecords,
                        onPrevMonth = { monthStartMillis = addMonths(monthStartMillis, -1) },
                        onNextMonth = { monthStartMillis = addMonths(monthStartMillis, 1) },
                        onPickMonth = { showMonthPickerDialog = true },
                        onSelectDay = { selectedDayStart = it },
                        onAddNew = { currentScreenName = HomeScreen.Records.name },
                        onRecordClick = { selectedRecord = it },
                    )
                }

                HomeScreen.Records -> {
                    RecordsScreen(
                        records = activeRecords,
                        brandInput = brandInput,
                        productNameInput = productNameInput,
                        amountInput = amountInput,
                        noteInput = noteInput,
                        selectedSugar = selectedSugar,
                        selectedIce = selectedIce,
                        selectedCupSize = selectedCupSize,
                        selectedDrinkTimeMillis = selectedDrinkTimeMillis,
                        onBrandInputChange = { brandInput = it },
                        onProductNameChange = { productNameInput = it },
                        onAmountInputChange = { amountInput = it },
                        onNoteInputChange = { noteInput = it },
                        onSugarSelect = { selectedSugar = it },
                        onIceSelect = { selectedIce = it },
                        onCupSizeSelect = { selectedCupSize = it },
                        onDrinkTimeChange = { selectedDrinkTimeMillis = it },
                        onSave = {
                            val trimmedBrand = brandInput.trim()
                            if (trimmedBrand.isEmpty()) return@RecordsScreen
                            val newRecord = MilkTeaRecord(
                                id = System.currentTimeMillis(),
                                drinkTimeMillis = selectedDrinkTimeMillis,
                                brand = trimmedBrand,
                                productName = productNameInput.trim(),
                                sugarLevel = selectedSugar,
                                iceLevel = selectedIce,
                                cupSize = selectedCupSize,
                                amountYuan = amountInput.trim(),
                                note = noteInput.trim(),
                            )
                            records = listOf(newRecord) + records
                            saveRecords(context, records)

                            brandInput = ""
                            productNameInput = ""
                            amountInput = ""
                            noteInput = ""
                            selectedSugar = sugarOptions.first()
                            selectedIce = iceOptions.first()
                            selectedCupSize = cupSizeOptions.first()
                            selectedDrinkTimeMillis = System.currentTimeMillis()

                            selectedDayStart = startOfDay(newRecord.drinkTimeMillis)
                            monthStartMillis = startOfMonth(newRecord.drinkTimeMillis)
                            currentScreenName = HomeScreen.Calendar.name
                        },
                        onRecordClick = { selectedRecord = it },
                    )
                }

                HomeScreen.Stats -> {
                    StatsScreen(
                        records = activeRecords,
                        mode = statsMode,
                        weekAnchorMillis = weekAnchorMillis,
                        monthAnchorMillis = monthAnchorMillis,
                        yearAnchorMillis = yearAnchorMillis,
                        onModeChange = { statsModeName = it.name },
                        onWeekAnchorChange = { weekAnchorMillis = it },
                        onMonthAnchorChange = { monthAnchorMillis = it },
                        onYearAnchorChange = { yearAnchorMillis = it },
                    )
                }

                HomeScreen.Settings -> {
                    SettingsScreen(
                        allRecords = records,
                        onRestoreRecord = { target ->
                            records = records.map { if (it.id == target.id) it.copy(deletedAtMillis = 0L) else it }
                            saveRecords(context, records)
                        },
                        onPermanentlyDelete = { target ->
                            records = records.filterNot { it.id == target.id }
                            saveRecords(context, records)
                        },
                        onWebDavRestored = { records = loadAndPurgeRecords(context) },
                        currentLanguage = AppLanguage.valueOf(language),
                        onLanguageChange = { picked ->
                            saveLanguage(context, picked)
                            language = picked.name
                        },
                    )
                }
            }
        }
    }

    selectedRecord?.let { record ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { selectedRecord = null },
            title = { Text(strings.detailTitle) },
            text = {
                androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(strings.detailTime(formatDateWithPeriod(record.drinkTimeMillis, strings)))
                    Text(strings.detailBrand(record.brand))
                    Text(strings.detailProduct(if (record.productName.isBlank()) strings.notFilled else record.productName))
                    Text(strings.detailCupSize(strings.cupSize(record.cupSize)))
                    Text(strings.detailSugar(strings.sugar(record.sugarLevel)))
                    Text(strings.detailIce(strings.ice(record.iceLevel)))
                    if (record.amountYuan.isNotBlank()) {
                        Text(strings.detailAmount(record.amountYuan))
                    }
                    Text(strings.detailNote(if (record.note.isBlank()) strings.none else record.note))
                }
            },
            confirmButton = {
                androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            selectedRecord = null
                            editingRecord = record
                        },
                    ) {
                        Text(strings.edit)
                    }
                    androidx.compose.material3.TextButton(onClick = { selectedRecord = null }) {
                        Text(strings.close)
                    }
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        records = records.map { if (it.id == record.id) it.copy(deletedAtMillis = System.currentTimeMillis()) else it }
                        saveRecords(context, records)
                        selectedRecord = null
                    },
                ) {
                    Text(strings.delete)
                }
            },
        )
    }

    if (showMonthPickerDialog) {
        CaramelDatePickerDialog(
            initialMillis = monthStartMillis,
            onConfirm = { picked ->
                val pickedMonthStart = startOfMonth(picked)
                monthStartMillis = pickedMonthStart
                if (startOfMonth(selectedDayStart) != pickedMonthStart) {
                    selectedDayStart = pickedMonthStart
                }
                showMonthPickerDialog = false
            },
            onDismiss = { showMonthPickerDialog = false },
        )
    }

    editingRecord?.let { record ->
        EditRecordDialog(
            original = record,
            allRecords = activeRecords,
            onDismiss = { editingRecord = null },
            onSave = { updated ->
                records = records.map { if (it.id == updated.id) updated else it }
                    .sortedByDescending { it.drinkTimeMillis }
                saveRecords(context, records)
                editingRecord = null
            },
        )
    }
    }
    }
}

@Composable
private fun MilkTeaCalendarScreen(
    monthStartMillis: Long,
    selectedDayStart: Long,
    todayStart: Long,
    recordsByDayCount: Map<Long, Int>,
    selectedDayRecords: List<MilkTeaRecord>,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDay: (Long) -> Unit,
    onPickMonth: () -> Unit,
    onAddNew: () -> Unit,
    onRecordClick: (MilkTeaRecord) -> Unit,
) {
    val strings = LocalMilkTeaStrings.current
    val monthCells = remember(monthStartMillis) { milkTeaBuildMonthCells(monthStartMillis) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                TextButton(onClick = onPrevMonth) { Text(strings.prevMonth) }
                Text(
                    text = milkTeaFormatMonth(monthStartMillis, strings),
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onPickMonth),
                )
                TextButton(onClick = onNextMonth) { Text(strings.nextMonth) }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                strings.weekLabels.forEach { label ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(vertical = 4.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                monthCells.chunked(7).forEach { week ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        week.forEach { cell ->
                            MilkTeaDayCellCard(
                                cell = cell,
                                count = cell.dayStartMillis?.let { recordsByDayCount[it] } ?: 0,
                                isSelected = cell.dayStartMillis == selectedDayStart,
                                isToday = cell.dayStartMillis == todayStart,
                                onClick = { cell.dayStartMillis?.let(onSelectDay) },
                            )
                        }
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                Text(
                    text = strings.dayRecordsTitle(milkTeaFormatDate(selectedDayStart, strings)),
                    style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                TextButton(onClick = onAddNew) { Text(strings.goRecord) }
            }
        }

        if (selectedDayRecords.isEmpty()) {
            item { Text(strings.noDrinkThisDay) }
        } else {
            items(selectedDayRecords, key = { it.id }) { record ->
                MilkTeaRecordTagCard(record = record, onClick = { onRecordClick(record) })
            }
        }
    }
}

@Composable
private fun RowScope.MilkTeaDayCellCard(
    cell: CalendarDayCell,
    count: Int,
    isSelected: Boolean,
    isToday: Boolean,
    onClick: () -> Unit,
) {
    if (cell.dayOfMonth == null || cell.dayStartMillis == null) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(74.dp)
                .padding(2.dp),
        )
        return
    }

    val cellShape = RoundedCornerShape(12.dp)
    val containerColor = when {
        isSelected -> MaterialTheme.colorScheme.primaryContainer
        isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
        else -> MaterialTheme.colorScheme.surface
    }
    val borderMod: Modifier = when {
        isSelected -> Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, cellShape)
        isToday -> Modifier.border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.55f), cellShape)
        else -> Modifier
    }

    Box(
        modifier = Modifier
            .weight(1f)
            .height(74.dp)
            .padding(2.dp)
            .clip(cellShape)
            .background(containerColor)
            .then(borderMod)
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = cell.dayOfMonth.toString(),
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            )
            MilkTeaCountDots(count = count)
        }
    }
}

@Composable
private fun MilkTeaCountDots(count: Int) {
    if (count <= 0) {
        Spacer(modifier = Modifier.height(8.dp))
        return
    }

    val dotColor = MaterialTheme.colorScheme.primary
    val visibleCount = count.coerceAtMost(4)
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        repeat(visibleCount) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(dotColor, CircleShape),
            )
        }
        if (count > visibleCount) {
            Text(
                text = "+${count - visibleCount}",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = dotColor,
            )
        }
    }
}

private fun milkTeaBuildMonthCells(monthStartMillis: Long): List<CalendarDayCell> {
    val calendar = Calendar.getInstance().apply { timeInMillis = monthStartMillis }
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val leadingEmptyCells = weekDayOffset(calendar.get(Calendar.DAY_OF_WEEK))

    val cells = mutableListOf<CalendarDayCell>()
    repeat(leadingEmptyCells) {
        cells += CalendarDayCell(dayOfMonth = null, dayStartMillis = null)
    }

    for (day in 1..daysInMonth) {
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dayStart = startOfDay(calendar.timeInMillis)
        cells += CalendarDayCell(dayOfMonth = day, dayStartMillis = dayStart)
    }

    while (cells.size % 7 != 0) {
        cells += CalendarDayCell(dayOfMonth = null, dayStartMillis = null)
    }

    return cells
}

private fun milkTeaFormatMonth(timeMillis: Long, strings: MilkTeaStrings): String {
    val formatter = SimpleDateFormat(strings.monthPattern, strings.locale)
    return formatter.format(Date(timeMillis))
}

private fun milkTeaFormatDate(timeMillis: Long, strings: MilkTeaStrings): String {
    val formatter = SimpleDateFormat(strings.datePattern, strings.locale)
    return formatter.format(Date(timeMillis))
}

@Composable
private fun MilkTeaRecordTagCard(
    record: MilkTeaRecord,
    onClick: () -> Unit,
) {
    val strings = LocalMilkTeaStrings.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Brush.horizontalGradient(listOf(Color(0xFFD4956A), Color(0xFFB07240))))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    record.brand,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                if (record.amountYuan.isNotBlank()) {
                    Text(
                        "￥${record.amountYuan}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            if (record.productName.isNotBlank()) {
                Text(record.productName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(formatDateWithPeriod(record.drinkTimeMillis, strings), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AssistChip(onClick = {}, label = { Text(strings.cupSize(record.cupSize)) })
                AssistChip(onClick = {}, label = { Text(strings.sugar(record.sugarLevel)) })
                AssistChip(onClick = {}, label = { Text(strings.ice(record.iceLevel)) })
            }
            if (record.note.isNotBlank()) {
                Text(strings.noteLine(record.note), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}
