package com.example.appdemo

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

internal const val MILK_TEA_PREFS_NAME = "milk_tea_tracker"
private const val MILK_TEA_RECORDS_KEY = "records"

enum class HomeScreen {
    Calendar,
    Records,
    Stats,
    Settings,
}

enum class StatsMode {
    Week,
    Month,
    Year,
}

internal const val TRASH_RETENTION_DAYS = 30

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
    val deletedAtMillis: Long = 0L,
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

// The week starts on Sunday everywhere: calendar grids, week stats, and the labels above them.
// This matches the platform DatePicker used when picking a record's date.
internal const val WEEK_START_DAY = Calendar.SUNDAY

/** Position of [dayOfWeek] (a Calendar.DAY_OF_WEEK value) within the week, 0 = week start. */
internal fun weekDayOffset(dayOfWeek: Int): Int = (dayOfWeek - WEEK_START_DAY + 7) % 7

internal fun buildMonthCells(monthStartMillis: Long): List<CalendarDayCell> {
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

// `strings` carries the language picked in Settings, along with that language's date patterns
// and Locale. It defaults to Simplified Chinese so call sites outside a composition still work.
internal fun formatMonth(timeMillis: Long, strings: MilkTeaStrings = MilkTeaStringsZhHans): String {
    val formatter = SimpleDateFormat(strings.monthPattern, strings.locale)
    return formatter.format(Date(timeMillis))
}

internal fun formatYear(timeMillis: Long, strings: MilkTeaStrings = MilkTeaStringsZhHans): String {
    val formatter = SimpleDateFormat(strings.yearPattern, strings.locale)
    return formatter.format(Date(timeMillis))
}

internal fun formatMonthDay(timeMillis: Long, strings: MilkTeaStrings = MilkTeaStringsZhHans): String {
    val formatter = SimpleDateFormat(strings.monthDayPattern, strings.locale)
    return formatter.format(Date(timeMillis))
}

internal fun formatWeekRange(weekStart: Long, strings: MilkTeaStrings = MilkTeaStringsZhHans): String {
    val weekEnd = addDays(weekStart, 6)
    return "${formatMonthDay(weekStart, strings)} - ${formatMonthDay(weekEnd, strings)}"
}

internal fun formatDate(timeMillis: Long, strings: MilkTeaStrings = MilkTeaStringsZhHans): String {
    val formatter = SimpleDateFormat(strings.datePattern, strings.locale)
    return formatter.format(Date(timeMillis))
}

internal fun formatTime(timeMillis: Long, strings: MilkTeaStrings = MilkTeaStringsZhHans): String {
    val formatter = SimpleDateFormat(strings.timePattern, strings.locale)
    return formatter.format(Date(timeMillis))
}

// Canonical keys, not display text: they are compared against stored values and fed to
// withDayPeriod. MilkTeaStrings.dayPeriod turns one into the label the user sees.
internal val dayPeriodOptions = listOf("早上", "下午", "晚上")

internal fun dayPeriodLabel(timeMillis: Long): String {
    val hour = Calendar.getInstance().apply { timeInMillis = timeMillis }.get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "早上"
        hour < 18 -> "下午"
        else -> "晚上"
    }
}

internal fun withDayPeriod(timeMillis: Long, period: String): Long {
    val representativeHour = when (period) {
        "早上" -> 9
        "下午" -> 15
        else -> 20
    }
    return Calendar.getInstance().apply {
        this.timeInMillis = timeMillis
        set(Calendar.HOUR_OF_DAY, representativeHour)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

internal fun formatDateWithPeriod(
    timeMillis: Long,
    strings: MilkTeaStrings = MilkTeaStringsZhHans,
): String {
    return "${formatDate(timeMillis, strings)} ${strings.dayPeriod(dayPeriodLabel(timeMillis))}"
}

internal fun formatAmount(amount: Double, strings: MilkTeaStrings = MilkTeaStringsZhHans): String {
    return if (amount % 1.0 == 0.0) {
        amount.toInt().toString()
    } else {
        String.format(strings.locale, "%.2f", amount)
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
        this.timeInMillis = startOfDay(timeMillis)
        firstDayOfWeek = WEEK_START_DAY
    }
    calendar.add(Calendar.DAY_OF_MONTH, -weekDayOffset(calendar.get(Calendar.DAY_OF_WEEK)))
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
            deletedAtMillis = obj.optLong("deletedAtMillis", 0L),
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
                put("deletedAtMillis", record.deletedAtMillis)
            },
        )
    }

    context.getSharedPreferences(MILK_TEA_PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(MILK_TEA_RECORDS_KEY, array.toString())
        .apply()
}

internal fun purgeExpiredTrash(
    records: List<MilkTeaRecord>,
    now: Long = System.currentTimeMillis(),
    retentionDays: Int = TRASH_RETENTION_DAYS,
): List<MilkTeaRecord> {
    val cutoff = now - retentionDays * 24L * 60 * 60 * 1000
    return records.filterNot { it.deletedAtMillis > 0L && it.deletedAtMillis < cutoff }
}

internal fun loadAndPurgeRecords(context: Context): List<MilkTeaRecord> {
    val loaded = loadRecords(context)
    val purged = purgeExpiredTrash(loaded)
    if (purged.size != loaded.size) {
        saveRecords(context, purged)
    }
    return purged
}

internal fun daysUntilPermanentDelete(deletedAtMillis: Long, now: Long = System.currentTimeMillis()): Int {
    val expiresAt = deletedAtMillis + TRASH_RETENTION_DAYS * 24L * 60 * 60 * 1000
    val remainingMillis = (expiresAt - now).coerceAtLeast(0L)
    return (remainingMillis / (24L * 60 * 60 * 1000)).toInt()
}
