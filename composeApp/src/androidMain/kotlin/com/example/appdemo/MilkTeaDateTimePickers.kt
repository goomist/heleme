package com.example.appdemo

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CaramelDatePickerDialog(
    initialMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initialMillis)
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val pickedUtcMillis = state.selectedDateMillis
                    if (pickedUtcMillis != null) {
                        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = pickedUtcMillis }
                        val localCal = Calendar.getInstance().apply {
                            timeInMillis = initialMillis
                            set(Calendar.YEAR, utcCal.get(Calendar.YEAR))
                            set(Calendar.MONTH, utcCal.get(Calendar.MONTH))
                            set(Calendar.DAY_OF_MONTH, utcCal.get(Calendar.DAY_OF_MONTH))
                        }
                        onConfirm(localCal.timeInMillis)
                    } else {
                        onDismiss()
                    }
                },
            ) { Text("确定") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    ) {
        DatePicker(state = state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CaramelTimePickerDialog(
    initialMillis: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    val initialCal = Calendar.getInstance().apply { timeInMillis = initialMillis }
    val state = rememberTimePickerState(
        initialHour = initialCal.get(Calendar.HOUR_OF_DAY),
        initialMinute = initialCal.get(Calendar.MINUTE),
        is24Hour = true,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("选择时间") },
        text = { TimePicker(state = state) },
        confirmButton = {
            TextButton(
                onClick = {
                    val updated = Calendar.getInstance().apply {
                        timeInMillis = initialMillis
                        set(Calendar.HOUR_OF_DAY, state.hour)
                        set(Calendar.MINUTE, state.minute)
                    }
                    onConfirm(updated.timeInMillis)
                },
            ) { Text("确定") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}
