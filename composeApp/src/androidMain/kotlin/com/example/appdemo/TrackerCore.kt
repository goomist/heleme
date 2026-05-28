package com.example.appdemo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val MILK_TEA_PREFS_NAME = "milk_tea_tracker"
private const val MILK_TEA_RECORDS_KEY = "records"

enum class HomeScreen {
    Calendar,
    Records,
    Stats,
}

enum class StatsMode {
    Week,
    Month,
    Year,
}

data class MilkTeaRecord(
    val id: Long,
    val drinkTimeMillis: Long,
    val brand: String,
    val productName: String,
    val sugarLevel: String,
    val iceLevel: String,
    val cupSize: String,
    val amountYuan: String,
    val note: String,
)

data class CalendarDayCell(
    val dayOfMonth: Int?,
    val dayStartMillis: Long?,
)

data class PeriodStat(
    val cupCount: Int,
    val totalAmount: Double,
    val favoriteBrand: String,
    val favoriteCount: Int,
)

data class TrendSeries(
    val labels: List<String>,
    val values: List<Double>,
)

internal fun buildMonthCells(monthStartMillis: Long): List<CalendarDayCell> {
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

internal fun formatMonth(timeMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy年MM月", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}

internal fun formatYear(timeMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy年", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}

internal fun formatMonthDay(timeMillis: Long): String {
    val formatter = SimpleDateFormat("MM-dd", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}

internal fun formatWeekRange(weekStart: Long): String {
    val weekEnd = addDays(weekStart, 6)
    return "${formatMonthDay(weekStart)} - ${formatMonthDay(weekEnd)}"
}

internal fun formatDate(timeMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}

internal fun formatTime(timeMillis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(Date(timeMillis))
}

internal fun formatAmount(amount: Double): String {
    return if (amount % 1.0 == 0.0) {
        amount.toInt().toString()
    } else {
        String.format(Locale.getDefault(), "%.2f", amount)
    }
}

internal fun startOfDay(timeMillis: Long): Long {
    return Calendar.getInstance().apply {
        this.timeInMillis = timeMillis
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

internal fun startOfWeek(timeMillis: Long): Long {
    val calendar = Calendar.getInstance().apply {
        this.timeInMillis = timeMillis
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
    return calendar.timeInMillis
}

internal fun startOfMonth(timeMillis: Long): Long {
    return Calendar.getInstance().apply {
        this.timeInMillis = timeMillis
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

internal fun startOfYear(timeMillis: Long): Long {
    return Calendar.getInstance().apply {
        this.timeInMillis = timeMillis
        set(Calendar.MONTH, Calendar.JANUARY)
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

internal fun addMonths(monthStartMillis: Long, offset: Int): Long {
    return Calendar.getInstance().apply {
        timeInMillis = monthStartMillis
        add(Calendar.MONTH, offset)
    }.timeInMillis
}

internal fun addDays(timeMillis: Long, days: Int): Long {
    return Calendar.getInstance().apply {
        this.timeInMillis = timeMillis
        add(Calendar.DAY_OF_MONTH, days)
    }.timeInMillis
}

internal fun addYears(timeMillis: Long, years: Int): Long {
    return Calendar.getInstance().apply {
        this.timeInMillis = timeMillis
        add(Calendar.YEAR, years)
    }.timeInMillis
}

internal fun mergeDateWithCurrentClock(dayStartMillis: Long): Long {
    val now = Calendar.getInstance()
    return Calendar.getInstance().apply {
        timeInMillis = dayStartMillis
        set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, now.get(Calendar.MINUTE))
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

internal fun showMonthPicker(
    context: Context,
    currentMonthMillis: Long,
    onSelectedMonthStart: (Long) -> Unit,
) {
    val calendar = Calendar.getInstance().apply { timeInMillis = currentMonthMillis }
    DatePickerDialog(
        context,
        { _, year, monthOfYear, _ ->
            val picked = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            onSelectedMonthStart(picked.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        1,
    ).show()
}

internal fun showYearPicker(
    context: Context,
    currentYearMillis: Long,
    onSelectedYearStart: (Long) -> Unit,
) {
    showDatePicker(context, currentYearMillis) { picked ->
        onSelectedYearStart(startOfYear(picked))
    }
}

internal fun showDatePicker(
    context: Context,
    currentMillis: Long,
    onSelected: (Long) -> Unit,
) {
    val calendar = Calendar.getInstance().apply { timeInMillis = currentMillis }
    DatePickerDialog(
        context,
        { _, year, monthOfYear, dayOfMonth ->
            val updated = Calendar.getInstance().apply {
                timeInMillis = currentMillis
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, monthOfYear)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            onSelected(updated.timeInMillis)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
    ).show()
}

internal fun showTimePicker(
    context: Context,
    currentMillis: Long,
    onSelected: (Long) -> Unit,
) {
    val calendar = Calendar.getInstance().apply { timeInMillis = currentMillis }
    TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val updated = Calendar.getInstance().apply {
                timeInMillis = currentMillis
                set(Calendar.HOUR_OF_DAY, hourOfDay)
                set(Calendar.MINUTE, minute)
            }
            onSelected(updated.timeInMillis)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true,
    ).show()
}

internal fun StatsMode.displayName(): String {
    return when (this) {
        StatsMode.Week -> "周"
        StatsMode.Month -> "月"
        StatsMode.Year -> "年"
    }
}

internal fun loadRecords(context: Context): List<MilkTeaRecord> {
    val prefs = context.getSharedPreferences(MILK_TEA_PREFS_NAME, Context.MODE_PRIVATE)
    val raw = prefs.getString(MILK_TEA_RECORDS_KEY, "[]") ?: "[]"
    val array = runCatching { JSONArray(raw) }.getOrElse { JSONArray() }

    val result = mutableListOf<MilkTeaRecord>()
    for (i in 0 until array.length()) {
        val obj = array.optJSONObject(i) ?: continue
        result += MilkTeaRecord(
            id = obj.optLong("id"),
            drinkTimeMillis = obj.optLong("drinkTimeMillis"),
            brand = obj.optString("brand"),
            productName = obj.optString("productName").ifBlank { obj.optString("note") },
            sugarLevel = obj.optString("sugarLevel", sugarOptions[2]),
            iceLevel = obj.optString("iceLevel", iceOptions[1]),
            cupSize = obj.optString("cupSize", cupSizeOptions.first()),
            amountYuan = obj.optString("amountYuan"),
            note = if (obj.has("productName")) obj.optString("note") else "",
        )
    }
    return result.sortedByDescending { it.drinkTimeMillis }
}

internal fun saveRecords(context: Context, records: List<MilkTeaRecord>) {
    val array = JSONArray()
    records.forEach { record ->
        array.put(
            JSONObject().apply {
                put("id", record.id)
                put("drinkTimeMillis", record.drinkTimeMillis)
                put("brand", record.brand)
                put("productName", record.productName)
                put("sugarLevel", record.sugarLevel)
                put("iceLevel", record.iceLevel)
                put("cupSize", record.cupSize)
                put("amountYuan", record.amountYuan)
                put("note", record.note)
            },
        )
    }

    context.getSharedPreferences(MILK_TEA_PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(MILK_TEA_RECORDS_KEY, array.toString())
        .apply()
}
