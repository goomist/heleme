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
    val strings = LocalMilkTeaStrings.current
    var showDatePickerDialog by remember { mutableStateOf(false) }
    var selectedTab by rememberSaveable { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text(strings.tabNewRecord) },
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text(strings.tabAllRecords) },
            )
        }

        if (selectedTab == 0) {
            AddRecordPane(
                records = records,
                brandInput = brandInput,
                productNameInput = productNameInput,
                amountInput = amountInput,
                noteInput = noteInput,
                selectedSugar = selectedSugar,
                selectedIce = selectedIce,
                selectedCupSize = selectedCupSize,
                selectedDrinkTimeMillis = selectedDrinkTimeMillis,
                onBrandInputChange = onBrandInputChange,
                onProductNameChange = onProductNameChange,
                onAmountInputChange = onAmountInputChange,
                onNoteInputChange = onNoteInputChange,
                onSugarSelect = onSugarSelect,
                onIceSelect = onIceSelect,
                onCupSizeSelect = onCupSizeSelect,
                onShowDatePicker = { showDatePickerDialog = true },
                onDrinkTimeChange = onDrinkTimeChange,
                onSave = onSave,
            )
        } else {
            AllRecordsPane(records = records, onRecordClick = onRecordClick)
        }
    }

    if (showDatePickerDialog) {
        CaramelDatePickerDialog(
            initialMillis = selectedDrinkTimeMillis,
            onConfirm = { onDrinkTimeChange(it); showDatePickerDialog = false },
            onDismiss = { showDatePickerDialog = false },
        )
    }
}

@Composable
private fun AddRecordPane(
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
    onShowDatePicker: () -> Unit,
    onDrinkTimeChange: (Long) -> Unit,
    onSave: () -> Unit,
) {
    val strings = LocalMilkTeaStrings.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 12.dp),
    ) {
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
                label = { Text(strings.brandLabel) },
                placeholder = { Text(strings.brandPlaceholder) },
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
                label = { Text(strings.amountLabel) },
                placeholder = { Text(strings.amountPlaceholder) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    strings.drinkTime(formatDateWithPeriod(selectedDrinkTimeMillis, strings)),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f), RoundedCornerShape(20.dp))
                            .clickable { onShowDatePicker() }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) { Text(strings.pickDate, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold) }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.30f), RoundedCornerShape(20.dp))
                            .clickable { onDrinkTimeChange(System.currentTimeMillis()) }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    ) { Text(strings.nowButton, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold) }
                }
                ChoiceChips(
                    options = dayPeriodOptions,
                    selectedOption = dayPeriodLabel(selectedDrinkTimeMillis),
                    onSelect = { onDrinkTimeChange(withDayPeriod(selectedDrinkTimeMillis, it)) },
                    label = strings.dayPeriod,
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(strings.sugarTitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                ChoiceChips(options = sugarOptions, selectedOption = selectedSugar, onSelect = onSugarSelect, label = strings.sugar)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(strings.iceTitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                ChoiceChips(options = iceOptions, selectedOption = selectedIce, onSelect = onIceSelect, label = strings.ice)
            }
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(strings.cupSizeTitle, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                ChoiceChips(options = cupSizeOptions, selectedOption = selectedCupSize, onSelect = onCupSizeSelect, label = strings.cupSize)
            }
            OutlinedTextField(
                value = noteInput,
                onValueChange = onNoteInputChange,
                label = { Text(strings.noteLabel) },
                placeholder = { Text(strings.notePlaceholder) },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
            )
            Button(
                onClick = onSave,
                enabled = brandInput.trim().isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(strings.submitRecord, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AllRecordsPane(
    records: List<MilkTeaRecord>,
    onRecordClick: (MilkTeaRecord) -> Unit,
) {
    val strings = LocalMilkTeaStrings.current
    var keyword by rememberSaveable { mutableStateOf("") }

    val filteredRecords = remember(records, keyword) {
        val trimmed = keyword.trim()
        if (trimmed.isEmpty()) {
            records
        } else {
            records.filter {
                it.brand.contains(trimmed, ignoreCase = true) ||
                    it.productName.contains(trimmed, ignoreCase = true) ||
                    it.note.contains(trimmed, ignoreCase = true)
            }
        }
    }

    val groupedByMonth = remember(filteredRecords) {
        filteredRecords.groupBy { startOfMonth(it.drinkTimeMillis) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item {
            OutlinedTextField(
                value = keyword,
                onValueChange = { keyword = it },
                label = { Text(strings.searchLabel) },
                placeholder = { Text(strings.searchPlaceholder) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
        }

        if (filteredRecords.isEmpty()) {
            item {
                Text(
                    if (records.isEmpty()) strings.emptyNoRecords else strings.emptyNoMatch,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            groupedByMonth.forEach { (monthStart, monthRecords) ->
                item(key = "header_$monthStart") {
                    Text(
                        formatMonth(monthStart, strings),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                items(monthRecords, key = { it.id }) { record ->
                    RecordTagCard(record = record, onClick = { onRecordClick(record) })
                }
            }
        }
    }
}

@Composable
private fun ProductNameField(
    value: String,
    onValueChange: (String) -> Unit,
    suggestionSource: List<MilkTeaRecord>,
    modifier: Modifier = Modifier,
) {
    val strings = LocalMilkTeaStrings.current
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
            label = { Text(strings.productLabel) },
            placeholder = { Text(strings.productPlaceholder) },
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
    val strings = LocalMilkTeaStrings.current
    var brand by rememberSaveable(original.id) { mutableStateOf(original.brand) }
    var productName by rememberSaveable(original.id) { mutableStateOf(original.productName) }
    var amount by rememberSaveable(original.id) { mutableStateOf(original.amountYuan) }
    var note by rememberSaveable(original.id) { mutableStateOf(original.note) }
    var sugar by rememberSaveable(original.id) { mutableStateOf(original.sugarLevel) }
    var ice by rememberSaveable(original.id) { mutableStateOf(original.iceLevel) }
    var cupSize by rememberSaveable(original.id) { mutableStateOf(original.cupSize) }
    var drinkTimeMillis by rememberSaveable(original.id) { mutableStateOf(original.drinkTimeMillis) }
    var showDatePickerDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(strings.editRecordTitle) },
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
                    label = { Text(strings.brandLabel) },
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
                    label = { Text(strings.amountLabel) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                )
                Text(strings.detailTime(formatDateWithPeriod(drinkTimeMillis, strings)))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { showDatePickerDialog = true }) {
                        Text(strings.pickDate)
                    }
                }
                ChoiceChips(
                    options = dayPeriodOptions,
                    selectedOption = dayPeriodLabel(drinkTimeMillis),
                    onSelect = { drinkTimeMillis = withDayPeriod(drinkTimeMillis, it) },
                    label = strings.dayPeriod,
                )
                Text(strings.sugarTitle)
                ChoiceChips(
                    options = sugarOptions,
                    selectedOption = sugar,
                    onSelect = { sugar = it },
                    label = strings.sugar,
                )
                Text(strings.iceTitle)
                ChoiceChips(
                    options = iceOptions,
                    selectedOption = ice,
                    onSelect = { ice = it },
                    label = strings.ice,
                )
                Text(strings.cupSizeTitle)
                ChoiceChips(
                    options = cupSizeOptions,
                    selectedOption = cupSize,
                    onSelect = { cupSize = it },
                    label = strings.cupSize,
                )
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text(strings.noteLabel) },
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
                Text(strings.save)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(strings.cancel)
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
    val strings = LocalMilkTeaStrings.current
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
            periodLabel = formatWeekRange(start, strings)
            amountTrend = buildWeekAmountTrend(records, start, strings)
            cupTrend = buildWeekCupTrend(records, start, strings)
        }

        StatsMode.Month -> {
            val start = startOfMonth(monthAnchorMillis)
            val end = addMonths(start, 1)
            periodStart = start
            periodEnd = end
            prevPeriodStart = addMonths(start, -1)
            prevPeriodEnd = start
            periodLabel = formatMonth(start, strings)
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
            periodLabel = formatYear(start, strings)
            amountTrend = buildYearAmountTrend(records, start, strings)
            cupTrend = buildYearCupTrend(records, start, strings)
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
                Tab(selected = mode == StatsMode.Week, onClick = { onModeChange(StatsMode.Week) }, text = { Text(strings.tabWeekly) })
                Tab(selected = mode == StatsMode.Month, onClick = { onModeChange(StatsMode.Month) }, text = { Text(strings.tabMonthly) })
                Tab(selected = mode == StatsMode.Year, onClick = { onModeChange(StatsMode.Year) }, text = { Text(strings.tabYearly) })
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
                    Text(strings.prevPeriod(mode))
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
                    Text(strings.nextPeriod(mode))
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
                    Text(strings.cupsOfMilkTea(stat.cupCount), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
                        Text(strings.spendingLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        Text(strings.brandValueSpending(formatAmount(stat.totalAmount, strings)), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(strings.favoriteLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                        Text(
                            if (stat.favoriteCount > 0) strings.favoriteValue(stat.favoriteBrand, stat.favoriteCount) else strings.empty,
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
                title = strings.spendTrendTitle(mode),
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
                        Text(strings.avgPriceLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                        if (avgPrice > 0) {
                            Text(strings.brandValueSpending(formatAmount(avgPrice, strings)), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        } else {
                            Text(strings.empty, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f))
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
                        Text(strings.mostExpensiveLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                        if (mostExpensive != null && parseAmount(mostExpensive.amountYuan) > 0) {
                            Text(strings.brandValueSpending(mostExpensive.amountYuan), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Text(mostExpensive.brand, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        } else {
                            Text(strings.empty, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f))
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
                            Text(strings.brandRankingTitle, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                FilterChip(
                                    selected = sortBySpending,
                                    onClick = { sortBySpending = true },
                                    label = { Text(strings.chipSpending) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                    ),
                                )
                                FilterChip(
                                    selected = !sortBySpending,
                                    onClick = { sortBySpending = false },
                                    label = { Text(strings.chipCups) },
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
                                            if (sortBySpending) strings.brandSubSpending(cups, formatAmount(avgSpend, strings))
                                            else strings.brandSubCups(formatAmount(totalSpend, strings)),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f),
                                        )
                                    }
                                }
                                Text(
                                    if (sortBySpending) strings.brandValueSpending(formatAmount(totalSpend, strings)) else strings.brandValueCups(cups),
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
                        Text(strings.currentStreakLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                        Text(strings.daysValue(currentStreak), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(strings.longestStreakLabel, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                        Text(strings.daysValue(longestStreak), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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
                    Text(strings.compareTitle(mode), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    if (amountChangeRatio == null) {
                        Text(strings.comparePrevEmpty(mode), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f))
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
                                strings.comparePrevValue(mode, formatAmount(prevStat.totalAmount, strings)),
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
                    title = strings.cupTrendTitle(mode),
                    trend = cupTrend,
                    blockColor = MaterialTheme.colorScheme.primary,
                    unitLabel = strings.cupUnit,
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
                        strings.brandValueSpending(record.amountYuan),
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

private fun buildWeekAmountTrend(
    records: List<MilkTeaRecord>,
    weekStart: Long,
    strings: MilkTeaStrings,
): TrendSeries {
    val amounts = mutableListOf<Double>()
    val labels = mutableListOf<String>()
    repeat(7) { offset ->
        val dayStart = addDays(weekStart, offset)
        val dayEnd = addDays(dayStart, 1)
        val value = records
            .filter { it.drinkTimeMillis in dayStart until dayEnd }
            .sumOf { parseAmount(it.amountYuan) }
        labels += formatMonthDay(dayStart, strings)
        amounts += value
    }
    return TrendSeries(labels = labels, values = amounts)
}

private fun buildWeekCupTrend(
    records: List<MilkTeaRecord>,
    weekStart: Long,
    strings: MilkTeaStrings,
): TrendSeries {
    val cups = mutableListOf<Double>()
    val labels = mutableListOf<String>()
    repeat(7) { offset ->
        val dayStart = addDays(weekStart, offset)
        val dayEnd = addDays(dayStart, 1)
        val value = records.count { it.drinkTimeMillis in dayStart until dayEnd }
        labels += strings.weekFullLabels[offset]
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

private fun buildYearAmountTrend(
    records: List<MilkTeaRecord>,
    yearStart: Long,
    strings: MilkTeaStrings,
): TrendSeries {
    val amounts = mutableListOf<Double>()
    val labels = mutableListOf<String>()
    repeat(12) { monthOffset ->
        val monthStart = addMonths(yearStart, monthOffset)
        val monthEnd = addMonths(monthStart, 1)
        val value = records
            .filter { it.drinkTimeMillis in monthStart until monthEnd }
            .sumOf { parseAmount(it.amountYuan) }
        labels += strings.monthShort(monthOffset + 1)
        amounts += value
    }
    return TrendSeries(labels = labels, values = amounts)
}

private fun buildYearCupTrend(
    records: List<MilkTeaRecord>,
    yearStart: Long,
    strings: MilkTeaStrings,
): TrendSeries {
    val cups = mutableListOf<Double>()
    val labels = mutableListOf<String>()
    repeat(12) { monthOffset ->
        val monthStart = addMonths(yearStart, monthOffset)
        val monthEnd = addMonths(monthStart, 1)
        val value = records.count { it.drinkTimeMillis in monthStart until monthEnd }
        labels += strings.monthShort(monthOffset + 1)
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
