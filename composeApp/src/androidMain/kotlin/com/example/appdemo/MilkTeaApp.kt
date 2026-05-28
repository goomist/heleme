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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
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

    var records by remember { mutableStateOf(loadRecords(context)) }
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

    var statsModeName by rememberSaveable { mutableStateOf(StatsMode.Week.name) }
    var weekAnchorMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var monthAnchorMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }
    var yearAnchorMillis by rememberSaveable { mutableStateOf(System.currentTimeMillis()) }

    val currentScreen = remember(currentScreenName) { HomeScreen.valueOf(currentScreenName) }
    val statsMode = remember(statsModeName) { StatsMode.valueOf(statsModeName) }
    val todayStart = remember { startOfDay(System.currentTimeMillis()) }

    val recordsByDayCount = remember(records) {
        records.groupingBy { startOfDay(it.drinkTimeMillis) }.eachCount()
    }
    val selectedDayRecords = remember(records, selectedDayStart) {
        records.filter { startOfDay(it.drinkTimeMillis) == selectedDayStart }
            .sortedByDescending { it.drinkTimeMillis }
    }

    val title = when (currentScreen) {
        HomeScreen.Calendar -> "喝了么"
        HomeScreen.Records -> "记录"
        HomeScreen.Stats -> "统计"
    }

    val isDark = isSystemInDarkTheme()
    MaterialTheme(
        colorScheme = if (isDark) darkColorScheme(
            primary = Color(0xFFD4956A),
            onPrimary = Color(0xFF3D1A00),
            primaryContainer = Color(0xFF5C3520),
            onPrimaryContainer = Color(0xFFF3DFB0),
            secondary = Color(0xFFC1926B),
            onSecondary = Color(0xFF3D1A00),
            background = Color(0xFF1A1208),
            surface = Color(0xFF241A10),
            surfaceVariant = Color(0xFF3A2A1A),
            onBackground = Color(0xFFF5E6CC),
            onSurface = Color(0xFFF5E6CC),
            onSurfaceVariant = Color(0xFFD4B896),
        ) else lightColorScheme(
            primary = Color(0xFFB07240),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFF3DFB0),
            onPrimaryContainer = Color(0xFF3D2000),
            secondary = Color(0xFFC1926B),
            onSecondary = Color.White,
            background = Color(0xFFFAF3EA),
            surface = Color(0xFFFFF9F0),
            surfaceVariant = Color(0xFFF0E0CC),
            onBackground = Color(0xFF2D1A00),
            onSurface = Color(0xFF2D1A00),
            onSurfaceVariant = Color(0xFF7A5236),
        ),
    ) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(title) }) },
        floatingActionButton = {
            if (currentScreen == HomeScreen.Calendar) {
                FloatingActionButton(
                    onClick = {
                        val today = startOfDay(System.currentTimeMillis())
                        selectedDayStart = today
                        monthStartMillis = startOfMonth(today)
                    },
                ) {
                    Text("今天")
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
                            painter = painterResource(R.drawable.nav_calendar),
                            contentDescription = "日历",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Unspecified,
                        )
                    },
                    label = { Text("日历") },
                )
                NavigationBarItem(
                    selected = currentScreen == HomeScreen.Records,
                    onClick = { currentScreenName = HomeScreen.Records.name },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.nav_record),
                            contentDescription = "记录",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Unspecified,
                        )
                    },
                    label = { Text("记录") },
                )
                NavigationBarItem(
                    selected = currentScreen == HomeScreen.Stats,
                    onClick = { currentScreenName = HomeScreen.Stats.name },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.nav_stats),
                            contentDescription = "统计",
                            modifier = Modifier.size(22.dp),
                            tint = Color.Unspecified,
                        )
                    },
                    label = { Text("统计") },
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
                        onPickMonth = {
                            showMonthPicker(context, monthStartMillis) { picked ->
                                monthStartMillis = picked
                                if (startOfMonth(selectedDayStart) != picked) {
                                    selectedDayStart = picked
                                }
                            }
                        },
                        onSelectDay = { selectedDayStart = it },
                        onAddNew = { currentScreenName = HomeScreen.Records.name },
                        onRecordClick = { selectedRecord = it },
                    )
                }

                HomeScreen.Records -> {
                    RecordsScreen(
                        records = records,
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
                        records = records,
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
            }
        }
    }

    selectedRecord?.let { record ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { selectedRecord = null },
            title = { Text("详情") },
            text = {
                androidx.compose.foundation.layout.Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("时间：${formatTime(record.drinkTimeMillis)}")
                    Text("品牌：${record.brand}")
                    Text("品名：${if (record.productName.isBlank()) "未填写" else record.productName}")
                    Text("杯型：${record.cupSize}")
                    Text("糖度：${record.sugarLevel}")
                    Text("温度/冰度：${record.iceLevel}")
                    if (record.amountYuan.isNotBlank()) {
                        Text("金额：${record.amountYuan} 元")
                    }
                    Text("备注：${if (record.note.isBlank()) "无" else record.note}")
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
                        Text("编辑")
                    }
                    androidx.compose.material3.TextButton(onClick = { selectedRecord = null }) {
                        Text("关闭")
                    }
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        records = records.filterNot { it.id == record.id }
                        saveRecords(context, records)
                        selectedRecord = null
                    },
                ) {
                    Text("删除")
                }
            },
        )
    }

    editingRecord?.let { record ->
        EditRecordDialog(
            original = record,
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

private val milkTeaWeekLabels = listOf("一", "二", "三", "四", "五", "六", "日")

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
                TextButton(onClick = onPrevMonth) { Text("‹ 上月") }
                Text(
                    text = milkTeaFormatMonth(monthStartMillis),
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onPickMonth),
                )
                TextButton(onClick = onNextMonth) { Text("下月 ›") }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                milkTeaWeekLabels.forEach { label ->
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
                    text = "${milkTeaFormatDate(selectedDayStart)} 记录",
                    style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
                TextButton(onClick = onAddNew) { Text("去记录") }
            }
        }

        if (selectedDayRecords.isEmpty()) {
            item { Text("这天还没有喝奶茶。") }
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
            MilkTeaRedDots(count = count)
        }
    }
}

@Composable
private fun MilkTeaRedDots(count: Int) {
    if (count <= 0) {
        Spacer(modifier = Modifier.height(8.dp))
        return
    }

    val visibleCount = count.coerceAtMost(4)
    Row(
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        repeat(visibleCount) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(Color(0xFFD32F2F), CircleShape),
            )
        }
        if (count > visibleCount) {
            Text(
                text = "+${count - visibleCount}",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                color = Color(0xFFD32F2F),
            )
        }
    }
}

private fun milkTeaBuildMonthCells(monthStartMillis: Long): List<CalendarDayCell> {
    val calendar = Calendar.getInstance().apply { timeInMillis = monthStartMillis }
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val leadingEmptyCells = (firstDayOfWeek + 5) % 7

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

private fun milkTeaFormatMonth(timeMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}

private fun milkTeaFormatDate(timeMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}

@Composable
private fun MilkTeaRecordTagCard(
    record: MilkTeaRecord,
    onClick: () -> Unit,
) {
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
            Text(formatTime(record.drinkTimeMillis), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                AssistChip(onClick = {}, label = { Text(record.cupSize) })
                AssistChip(onClick = {}, label = { Text(record.sugarLevel) })
                AssistChip(onClick = {}, label = { Text(record.iceLevel) })
            }
            if (record.note.isNotBlank()) {
                Text("备注：${record.note}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}
