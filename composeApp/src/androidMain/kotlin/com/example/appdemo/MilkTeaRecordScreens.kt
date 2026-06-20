package com.example.appdemo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
internal fun RecordsScreen(
    records: List<MilkTeaRecord>,
    brandInput: String,
    productNameInput: String,
    amountInput: String,
    noteInput: String,
    selectedSugar: String,
    selectedIce: String,
    selectedCupSize: String,
    selectedDrinkTimeMillis: Long,
    onBrandInputChange: (String) -> Unit,
    onProductNameChange: (String) -> Unit,
    onAmountInputChange: (String) -> Unit,
    onNoteInputChange: (String) -> Unit,
    onSugarSelect: (String) -> Unit,
    onIceSelect: (String) -> Unit,
    onCupSizeSelect: (String) -> Unit,
    onDrinkTimeChange: (Long) -> Unit,
    onSave: () -> Unit,
    onRecordClick: (MilkTeaRecord) -> Unit,
) {
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            Text("新增记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                OutlinedTextField(
                    value = brandInput,
                    onValueChange = onBrandInputChange,
                    label = { Text("品牌/店名") },
                    placeholder = { Text("例如：喜茶、奈雪、霸王茶姬") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                ProductNameField(
                    value = productNameInput,
                    onValueChange = onProductNameChange,
                    suggestionSource = records,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = amountInput,
                    onValueChange = onAmountInputChange,
                    label = { Text("金额（元）") },
                    placeholder = { Text("例如：18.5") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        "喝奶茶时间：${formatTime(selectedDrinkTimeMillis)}",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f), RoundedCornerShape(20.dp))
                                .clickable { showDatePickerDialog = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) { Text("选日期", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f), RoundedCornerShape(20.dp))
                                .clickable { showTimePickerDialog = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) { Text("选时间", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f), RoundedCornerShape(20.dp))
                                .clickable { onDrinkTimeChange(System.currentTimeMillis()) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                        ) { Text("现在", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold) }
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("糖度", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ChoiceChips(options = sugarOptions, selectedOption = selectedSugar, onSelect = onSugarSelect)
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("温度/冰度", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ChoiceChips(options = iceOptions, selectedOption = selectedIce, onSelect = onIceSelect)
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("杯型", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    ChoiceChips(options = cupSizeOptions, selectedOption = selectedCupSize, onSelect = onCupSizeSelect)
                }
                OutlinedTextField(
                    value = noteInput,
                    onValueChange = onNoteInputChange,
                    label = { Text("备注") },
                    placeholder = { Text("例如：加珍珠、少奶、排队很久") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                )
                Button(
                    onClick = onSave,
                    enabled = brandInput.trim().isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("记录一次", fontWeight = FontWeight.Bold)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("全部记录", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        }

        if (records.isEmpty()) {
            item {
                Text("还没有记录，先打第一杯吧。")
            }
        } else {
            items(records, key = { it.id }) { record ->
                RecordTagCard(record = record, onClick = { onRecordClick(record) })
            }
        }
    }

    if (showDatePickerDialog) {
        CaramelDatePickerDialog(
            initialMillis = selectedDrinkTimeMillis,
            onConfirm = { onDrinkTimeChange(it); showDatePickerDialog = false },
            onDismiss = { showDatePickerDialog = false },
        )
    }
    if (showTimePickerDialog) {
        CaramelTimePickerDialog(
            initialMillis = selectedDrinkTimeMillis,
            onConfirm = { onDrinkTimeChange(it); showTimePickerDialog = false },
            onDismiss = { showTimePickerDialog = false },
        )
    }
}

@Composable
private fun ProductNameField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestionSource: List<MilkTeaRecord>,
    modifier: Modifier = Modifier,
) {
    var isFocused by remember { mutableStateOf(false) }
    val suggestions = remember(value, suggestionSource) {
        val query = value.trim()
        if (query.isEmpty()) {
            emptyList()
        } else {
            suggestionSource
                .map { it.productName.trim() }
                .filter { it.isNotEmpty() && it.contains(query, ignoreCase = true) && !it.equals(query, ignoreCase = true) }
                .distinct()
                .take(5)
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("品名") },
            placeholder = { Text("例如：多肉葡萄、伯牙绝弦") },
            modifier = Modifier.fillMaxWidth().onFocusChanged { isFocused = it.isFocused },
            singleLine = true,
        )
        if (isFocused && suggestions.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)),
            ) {
                suggestions.forEach { suggestion ->
                    Text(
                        suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onValueChange(suggestion) }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

@Composable
internal fun EditRecordDialog(
    original: MilkTeaRecord,
    allRecords: List<MilkTeaRecord>,
    onDismiss: () -> Unit,
    onSave: (MilkTeaRecord) -> Unit,
) {
    var brand by rememberSaveable(original.id) { mutableStateOf(original.brand) }
    var productName by rememberSaveable(original.id) { mutableStateOf(original.productName) }
    var amount by rememberSaveable(original.id) { mutableStateOf(original.amountYuan) }
    var note by rememberSaveable(original.id) { mutableStateOf(original.note) }
    var sugar by rememberSaveable(original.id) { mutableStateOf(original.sugarLevel) }
    var ice by rememberSaveable(original.id) { mutableStateOf(original.iceLevel) }
    var cupSize by rememberSaveable(original.id) { mutableStateOf(original.cupSize) }
    var drinkTimeMillis by rememberSaveable(original.id) { mutableStateOf(original.drinkTimeMillis) }
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑记录") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("品牌/店名") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                ProductNameField(
                    value = productName,
                    onValueChange = { productName = it },
                    suggestionSource = allRecords,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("金额（元）") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                Text("时间：${formatTime(drinkTimeMillis)}")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { showDatePickerDialog = true }) {
                        Text("选日期")
                    }
                    TextButton(onClick = { showTimePickerDialog = true }) {
                        Text("选时间")
                    }
                }
                Text("糖度")
                ChoiceChips(
                    options = sugarOptions,
                    selectedOption = sugar,
                    onSelect = { sugar = it },
                )
                Text("温度/冰度")
                ChoiceChips(
                    options = iceOptions,
                    selectedOption = ice,
                    onSelect = { ice = it },
                )
                Text("杯型")
                ChoiceChips(
                    options = cupSizeOptions,
                    selectedOption = cupSize,
                    onSelect = { cupSize = it },
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("备注") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val trimmedBrand = brand.trim()
                    if (trimmedBrand.isEmpty()) return@TextButton
                    onSave(
                        original.copy(
                            brand = trimmedBrand,
                            productName = productName.trim(),
                            amountYuan = amount.trim(),
                            sugarLevel = sugar,
                            iceLevel = ice,
                            cupSize = cupSize,
                            drinkTimeMillis = drinkTimeMillis,
                            note = note.trim(),
                        ),
                    )
                },
            ) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        },
    )

    if (showDatePickerDialog) {
        CaramelDatePickerDialog(
            initialMillis = drinkTimeMillis,
            onConfirm = { drinkTimeMillis = it; showDatePickerDialog = false },
            onDismiss = { showDatePickerDialog = false },
        )
    }
    if (showTimePickerDialog) {
        CaramelTimePickerDialog(
            initialMillis = drinkTimeMillis,
            onConfirm = { drinkTimeMillis = it; showTimePickerDialog = false },
            onDismiss = { showTimePickerDialog = false },
        )
    }
}

@Composable
internal fun StatsScreen(
    records: List<MilkTeaRecord>,
    mode: StatsMode,
    weekAnchorMillis: Long,
    monthAnchorMillis: Long,
    yearAnchorMillis: Long,
    onModeChange: (StatsMode) -> Unit,
    onWeekAnchorChange: (Long) -> Unit,
    onMonthAnchorChange: (Long) -> Unit,
    onYearAnchorChange: (Long) -> Unit,
) {
    var showAnchorPickerDialog by remember { mutableStateOf(false) }

    val periodStart: Long
    val periodEnd: Long
    val prevPeriodStart: Long
    val prevPeriodEnd: Long
    val periodLabel: String
    val amountTrend: TrendSeries
    val cupTrend: TrendSeries

    when (mode) {
        StatsMode.Week -> {
            val start = startOfWeek(weekAnchorMillis)
            val end = addDays(start, 7)
            periodStart = start
            periodEnd = end
            prevPeriodStart = addDays(start, -7)
            prevPeriodEnd = start
            periodLabel = formatWeekRange(start)
            amountTrend = buildWeekAmountTrend(records, start)
            cupTrend = buildWeekCupTrend(records, start)
        }

        StatsMode.Month -> {
            val start = startOfMonth(monthAnchorMillis)
            val end = addMonths(start, 1)
            periodStart = start
            periodEnd = end
            prevPeriodStart = addMonths(start, -1)
            prevPeriodEnd = start
            periodLabel = formatMonth(start)
            amountTrend = buildMonthAmountTrend(records, start)
            cupTrend = buildMonthCupTrend(records, start)
        }

        StatsMode.Year -> {
            val start = startOfYear(yearAnchorMillis)
            val end = addYears(start, 1)
            periodStart = start
            periodEnd = end
            prevPeriodStart = addYears(start, -1)
            prevPeriodEnd = start
            periodLabel = formatYear(start)
            amountTrend = buildYearAmountTrend(records, start)
            cupTrend = buildYearCupTrend(records, start)
        }
    }

    val stat = remember(records, periodStart, periodEnd) {
        buildPeriodStat(records, periodStart, periodEnd)
    }
    val prevStat = remember(records, prevPeriodStart, prevPeriodEnd) {
        buildPeriodStat(records, prevPeriodStart, prevPeriodEnd)
    }
    val periodRecords = remember(records, periodStart, periodEnd) {
        records.filter { it.drinkTimeMillis in periodStart until periodEnd }
    }
    val avgPrice = remember(periodRecords) {
        val withAmt = periodRecords.filter { parseAmount(it.amountYuan) > 0 }
        if (withAmt.isEmpty()) 0.0 else withAmt.sumOf { parseAmount(it.amountYuan) } / withAmt.size
    }
    val mostExpensive = remember(periodRecords) {
        periodRecords.maxByOrNull { parseAmount(it.amountYuan) }
    }
    val brandStats = remember(periodRecords) {
        periodRecords
            .filter { it.brand.trim().isNotBlank() }
            .groupBy { it.brand.trim() }
            .mapValues { (_, recs) -> Pair(recs.size, recs.sumOf { parseAmount(it.amountYuan) }) }
    }
    val currentStreak = remember(records) { computeCurrentStreak(records) }
    val longestStreak = remember(records) { computeLongestStreak(records) }
    val amountChangeRatio = remember(stat, prevStat) {
        if (prevStat.totalAmount > 0) (stat.totalAmount - prevStat.totalAmount) / prevStat.totalAmount else null
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            TabRow(selectedTabIndex = mode.ordinal) {
                Tab(selected = mode == StatsMode.Week, onClick = { onModeChange(StatsMode.Week) }, text = { Text("周报") })
                Tab(selected = mode == StatsMode.Month, onClick = { onModeChange(StatsMode.Month) }, text = { Text("月报") })
                Tab(selected = mode == StatsMode.Year, onClick = { onModeChange(StatsMode.Year) }, text = { Text("年报") })
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextButton(
                    onClick = {
                        when (mode) {
                            StatsMode.Week -> onWeekAnchorChange(addDays(weekAnchorMillis, -7))
                            StatsMode.Month -> onMonthAnchorChange(addMonths(monthAnchorMillis, -1))
                            StatsMode.Year -> onYearAnchorChange(addYears(yearAnchorMillis, -1))
                        }
                    },
                ) {
                    Text("‹ 上一${mode.displayName()}")
                }

                Text(
                    text = periodLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { showAnchorPickerDialog = true },
                )

                TextButton(
                    onClick = {
                        when (mode) {
                            StatsMode.Week -> onWeekAnchorChange(addDays(weekAnchorMillis, 7))
                            StatsMode.Month -> onMonthAnchorChange(addMonths(monthAnchorMillis, 1))
                            StatsMode.Year -> onYearAnchorChange(addYears(yearAnchorMillis, 1))
                        }
                    },
                ) {
                    Text("下一${mode.displayName()} ›")
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Brush.linearGradient(listOf(MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f))))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        "${stat.cupCount}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text("杯奶茶", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text("花费", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        Text("￥${formatAmount(stat.totalAmount)}", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("最常喝", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        Text(
                            if (stat.favoriteCount > 0) "${stat.favoriteBrand}·${stat.favoriteCount}杯" else "暂无",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }

        item {
            TrendChartCard(
                title = "${mode.displayName()}花费趋势",
                trend = amountTrend,
                lineColor = MaterialTheme.colorScheme.primary,
                unitPrefix = "￥",
            )
        }

        item {
            Row(
                modifier = Modifier.height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text("平均单价", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                        if (avgPrice > 0) {
                            Text("￥${formatAmount(avgPrice)}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        } else {
                            Text("暂无", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f))
                        }
                    }
                }
                Card(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text("最贵一杯", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                        if (mostExpensive != null && parseAmount(mostExpensive.amountYuan) > 0) {
                            Text("￥${mostExpensive.amountYuan}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(mostExpensive.brand, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        } else {
                            Text("暂无", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f))
                        }
                    }
                }
            }
        }

        if (brandStats.isNotEmpty()) {
            item {
                var sortBySpending by rememberSaveable { mutableStateOf(true) }
                val displayRanking = if (sortBySpending) {
                    brandStats.entries.sortedByDescending { it.value.second }.take(3)
                } else {
                    brandStats.entries.sortedByDescending { it.value.first }.take(3)
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("品牌排行", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = sortBySpending,
                                    onClick = { sortBySpending = true },
                                    label = { Text("花费") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                )
                                FilterChip(
                                    selected = !sortBySpending,
                                    onClick = { sortBySpending = false },
                                    label = { Text("杯数") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                )
                            }
                        }
                        displayRanking.forEachIndexed { i, entry ->
                            val (cups, totalSpend) = entry.value
                            val avgSpend = if (cups > 0) totalSpend / cups else 0.0
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        "${i + 1}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (i == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.40f),
                                    )
                                    Column {
                                        Text(entry.key, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                        Text(
                                            if (sortBySpending) "${cups}杯 · 均价￥${formatAmount(avgSpend)}"
                                            else "共￥${formatAmount(totalSpend)}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f),
                                        )
                                    }
                                }
                                Text(
                                    if (sortBySpending) "￥${formatAmount(totalSpend)}" else "${cups}杯",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("当前连续", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                        Text("$currentStreak 天", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("历史最长", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                        Text("$longestStreak 天", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("与上${mode.displayName()}对比", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    if (amountChangeRatio == null) {
                        Text("上${mode.displayName()}暂无数据", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f))
                    } else {
                        val pct = (amountChangeRatio * 100).toInt()
                        val absPct = if (pct < 0) -pct else pct
                        val sign = if (pct >= 0) "↑" else "↓"
                        val changeColor = if (pct >= 0) Color(0xFFE57373) else Color(0xFF81C784)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "$sign $absPct%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = changeColor,
                            )
                            Text(
                                "上${mode.displayName()}：￥${formatAmount(prevStat.totalAmount)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f),
                            )
                        }
                    }
                }
            }
        }

        if (mode != StatsMode.Month) {
            item {
                BlockTrendCard(
                    title = "${mode.displayName()}杯数趋势",
                    trend = cupTrend,
                    blockColor = Color(0xFFD32F2F),
                    unitLabel = "杯",
                    fixedColumns = if (mode == StatsMode.Week) 7 else 12,
                )
            }
        }
    }

    if (showAnchorPickerDialog) {
        val anchorMillis = when (mode) {
            StatsMode.Week -> weekAnchorMillis
            StatsMode.Month -> monthAnchorMillis
            StatsMode.Year -> yearAnchorMillis
        }
        CaramelDatePickerDialog(
            initialMillis = anchorMillis,
            onConfirm = { picked ->
                when (mode) {
                    StatsMode.Week -> onWeekAnchorChange(picked)
                    StatsMode.Month -> onMonthAnchorChange(startOfMonth(picked))
                    StatsMode.Year -> onYearAnchorChange(startOfYear(picked))
                }
                showAnchorPickerDialog = false
            },
            onDismiss = { showAnchorPickerDialog = false },
        )
    }
}

@Composable
private fun RecordTagCard(
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
                .background(Brush.horizontalGradient(listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)))
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

private fun buildPeriodStat(records: List<MilkTeaRecord>, start: Long, end: Long): PeriodStat {
    val periodRecords = records.filter { it.drinkTimeMillis in start until end }
    val totalAmount = periodRecords.sumOf { parseAmount(it.amountYuan) }
    val groupedBrand = periodRecords
        .map { it.brand.trim() }
        .filter { it.isNotBlank() }
        .groupingBy { it }
        .eachCount()
    val topBrand = groupedBrand.maxByOrNull { it.value }

    return PeriodStat(
        cupCount = periodRecords.size,
        totalAmount = totalAmount,
        favoriteBrand = topBrand?.key ?: "",
        favoriteCount = topBrand?.value ?: 0,
    )
}

private fun buildWeekAmountTrend(records: List<MilkTeaRecord>, weekStart: Long): TrendSeries {
    val amounts = mutableListOf<Double>()
    val labels = mutableListOf<String>()
    repeat(7) { offset ->
        val dayStart = addDays(weekStart, offset)
        val dayEnd = addDays(dayStart, 1)
        val value = records
            .filter { it.drinkTimeMillis in dayStart until dayEnd }
            .sumOf { parseAmount(it.amountYuan) }
        labels += formatMonthDay(dayStart)
        amounts += value
    }
    return TrendSeries(labels = labels, values = amounts)
}

private fun buildWeekCupTrend(records: List<MilkTeaRecord>, weekStart: Long): TrendSeries {
    val cups = mutableListOf<Double>()
    val labels = mutableListOf<String>()
    repeat(7) { offset ->
        val dayStart = addDays(weekStart, offset)
        val dayEnd = addDays(dayStart, 1)
        val value = records.count { it.drinkTimeMillis in dayStart until dayEnd }
        labels += weekFullLabels[offset]
        cups += value.toDouble()
    }
    return TrendSeries(labels = labels, values = cups)
}

private fun buildMonthAmountTrend(records: List<MilkTeaRecord>, monthStart: Long): TrendSeries {
    val calendar = Calendar.getInstance().apply { timeInMillis = monthStart }
    val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val amounts = mutableListOf<Double>()
    val labels = mutableListOf<String>()

    for (day in 1..days) {
        calendar.timeInMillis = monthStart
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dayStart = startOfDay(calendar.timeInMillis)
        val dayEnd = addDays(dayStart, 1)
        val value = records
            .filter { it.drinkTimeMillis in dayStart until dayEnd }
            .sumOf { parseAmount(it.amountYuan) }
        labels += day.toString().padStart(2, '0')
        amounts += value
    }
    return TrendSeries(labels = labels, values = amounts)
}

private fun buildMonthCupTrend(records: List<MilkTeaRecord>, monthStart: Long): TrendSeries {
    val calendar = Calendar.getInstance().apply { timeInMillis = monthStart }
    val days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    val cups = mutableListOf<Double>()
    val labels = mutableListOf<String>()

    for (day in 1..days) {
        calendar.timeInMillis = monthStart
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dayStart = startOfDay(calendar.timeInMillis)
        val dayEnd = addDays(dayStart, 1)
        val value = records.count { it.drinkTimeMillis in dayStart until dayEnd }
        labels += day.toString().padStart(2, '0')
        cups += value.toDouble()
    }
    return TrendSeries(labels = labels, values = cups)
}

private fun buildYearAmountTrend(records: List<MilkTeaRecord>, yearStart: Long): TrendSeries {
    val amounts = mutableListOf<Double>()
    val labels = mutableListOf<String>()
    repeat(12) { monthOffset ->
        val monthStart = addMonths(yearStart, monthOffset)
        val monthEnd = addMonths(monthStart, 1)
        val value = records
            .filter { it.drinkTimeMillis in monthStart until monthEnd }
            .sumOf { parseAmount(it.amountYuan) }
        labels += "${monthOffset + 1}月"
        amounts += value
    }
    return TrendSeries(labels = labels, values = amounts)
}

private fun buildYearCupTrend(records: List<MilkTeaRecord>, yearStart: Long): TrendSeries {
    val cups = mutableListOf<Double>()
    val labels = mutableListOf<String>()
    repeat(12) { monthOffset ->
        val monthStart = addMonths(yearStart, monthOffset)
        val monthEnd = addMonths(monthStart, 1)
        val value = records.count { it.drinkTimeMillis in monthStart until monthEnd }
        labels += "${monthOffset + 1}月"
        cups += value.toDouble()
    }
    return TrendSeries(labels = labels, values = cups)
}

private fun parseAmount(raw: String): Double {
    return raw.trim().replace("，", ".").replace(",", ".").toDoubleOrNull() ?: 0.0
}

private fun computeCurrentStreak(records: List<MilkTeaRecord>): Int {
    if (records.isEmpty()) return 0
    val recordedDays = records.map { startOfDay(it.drinkTimeMillis) }.toSet()
    var streak = 0
    var day = startOfDay(System.currentTimeMillis())
    while (recordedDays.contains(day)) {
        streak++
        day = addDays(day, -1)
    }
    return streak
}

private fun computeLongestStreak(records: List<MilkTeaRecord>): Int {
    if (records.isEmpty()) return 0
    val days = records.map { startOfDay(it.drinkTimeMillis) }.toSet().sorted()
    if (days.isEmpty()) return 0
    var longest = 1
    var current = 1
    for (i in 1 until days.size) {
        if (days[i] - days[i - 1] == 86_400_000L) {
            current++
            if (current > longest) longest = current
        } else {
            current = 1
        }
    }
    return longest
}
