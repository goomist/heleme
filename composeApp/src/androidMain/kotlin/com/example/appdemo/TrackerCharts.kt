package com.example.appdemo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun TrendChartCard(
    title: String,
    trend: TrendSeries,
    lineColor: Color,
    unitPrefix: String,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.90f))
            if (trend.values.all { it == 0.0 }) {
                Text("暂无记录", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f))
            } else {
                LineTrendChart(trend = trend, lineColor = lineColor)
                val peak = trend.values.maxOrNull() ?: 0.0
                val peakText = if (unitPrefix.isBlank()) formatAmount(peak) else "$unitPrefix${formatAmount(peak)}"
                Text("峰值：$peakText", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
            }
        }
    }
}

@Composable
internal fun BlockTrendCard(
    title: String,
    trend: TrendSeries,
    blockColor: Color,
    unitLabel: String,
    fixedColumns: Int? = null,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.90f))
            if (trend.values.all { it == 0.0 }) {
                Text("暂无记录", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.48f))
            } else {
                BlockTrendChart(trend = trend, blockColor = blockColor, fixedColumns = fixedColumns)
                val peak = trend.values.maxOrNull() ?: 0.0
                val peakText = if (unitLabel.isBlank()) formatAmount(peak) else "${formatAmount(peak)}$unitLabel"
                Text("峰值：$peakText", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
            }
        }
    }
}

@Composable
private fun BlockTrendChart(
    trend: TrendSeries,
    blockColor: Color,
    fixedColumns: Int? = null,
) {
    val maxValue = (trend.values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
    if (fixedColumns != null && trend.values.size == fixedColumns) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            trend.values.forEach { value ->
                val ratio = (value / maxValue).toFloat().coerceIn(0f, 1f)
                val color = if (value <= 0.0) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    blockColor.copy(alpha = 0.25f + ratio * 0.75f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color),
                )
            }
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            trend.values.forEach { value ->
                val ratio = (value / maxValue).toFloat().coerceIn(0f, 1f)
                val color = if (value <= 0.0) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    blockColor.copy(alpha = 0.25f + ratio * 0.75f)
                }
                Box(
                    modifier = Modifier
                        .size(width = 16.dp, height = 16.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(color),
                )
            }
        }
    }
    if (trend.labels.isNotEmpty()) {
        if ((fixedColumns == 7 || fixedColumns == 12) && trend.labels.size == fixedColumns) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                trend.labels.forEach { label ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(label, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        } else {
            val first = trend.labels.first()
            val mid = trend.labels[trend.labels.size / 2]
            val last = trend.labels.last()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(first, style = MaterialTheme.typography.labelSmall)
                Text(mid, style = MaterialTheme.typography.labelSmall)
                Text(last, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun LineTrendChart(
    trend: TrendSeries,
    lineColor: Color,
) {
    val values = trend.values
    val maxValue = (values.maxOrNull() ?: 1.0).coerceAtLeast(1.0)
    val chartHeight = 160.dp
    val yAxisWidth = 36.dp

    val guideLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.07f)

    Row(modifier = Modifier.fillMaxWidth()) {
        // Y-axis labels
        Box(
            modifier = Modifier
                .width(yAxisWidth)
                .height(chartHeight),
        ) {
            Text(
                formatAmount(maxValue),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f),
                modifier = Modifier.align(Alignment.TopEnd),
            )
            Text(
                formatAmount(maxValue / 2.0),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f),
                modifier = Modifier.align(Alignment.CenterEnd),
            )
            Text(
                "0",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.42f),
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }

        Canvas(
            modifier = Modifier
                .weight(1f)
                .height(chartHeight),
        ) {
            val count = values.size
            if (count == 0) return@Canvas

            val w = size.width
            val h = size.height
            val gap = 3f
            val barWidth = if (count > 1) (w - gap * (count - 1)) / count else w
            val cornerR = (barWidth / 2f).coerceAtMost(10f)

            // Subtle horizontal guide lines
            repeat(5) { i ->
                val y = h * i / 4f
                drawLine(
                    color = guideLineColor,
                    start = Offset(0f, y),
                    end = Offset(w, y),
                    strokeWidth = 1f,
                )
            }

            values.forEachIndexed { index, value ->
                val ratio = (value / maxValue).toFloat().coerceIn(0f, 1f)
                val barHeight = h * ratio
                if (barHeight < 2f) return@forEachIndexed
                val left = index * (barWidth + gap)
                val top = h - barHeight
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(lineColor, lineColor.copy(alpha = 0.55f)),
                        startY = top,
                        endY = h,
                    ),
                    topLeft = Offset(left, top),
                    size = Size(barWidth, barHeight),
                    cornerRadius = CornerRadius(cornerR, cornerR),
                )
            }
        }
    }

    // X-axis labels, indented to align with chart area
    if (trend.labels.isNotEmpty()) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(yAxisWidth))
            val first = trend.labels.first()
            val mid = trend.labels[trend.labels.size / 2]
            val last = trend.labels.last()
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(first, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                Text(mid, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
                Text(last, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.52f))
            }
        }
    }
}

@Composable
internal fun ChoiceChips(
    options: List<String>,
    selectedOption: String,
    onSelect: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        options.forEach { option ->
            FilterChip(
                selected = option == selectedOption,
                onClick = { onSelect(option) },
                label = { Text(option) },
            )
        }
    }
}
