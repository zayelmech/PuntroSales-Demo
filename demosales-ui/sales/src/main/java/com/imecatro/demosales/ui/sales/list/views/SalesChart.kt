package com.imecatro.demosales.ui.sales.list.views

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.imecatro.demosales.domain.sales.list.model.GroupedSalesByDay
import com.imecatro.demosales.domain.sales.list.model.GroupedSalesByHour
import com.imecatro.demosales.domain.sales.list.model.SalesMetricsDomainModel
import java.text.NumberFormat
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import kotlin.math.max


private enum class SalesRangeChip(val label: String) {

    DAY("Day"),
    WEEK("Week"),
    MONTH("Month")
}

@Immutable
private data class ChartPoint(
    val xLabel: String,
    val value: Double
)

@Composable
fun SalesGraphBottomSheetContent(
    modifier: Modifier = Modifier,
    salesMetrics: SalesMetricsDomainModel,
    referenceDate: LocalDate = LocalDate.now()
) {
    var selectedRange by remember { mutableStateOf(SalesRangeChip.DAY) }

    val points = remember(salesMetrics, selectedRange, referenceDate) {
        when (selectedRange) {
            SalesRangeChip.DAY -> buildDayPoints(salesMetrics.today)
            SalesRangeChip.WEEK -> buildWeekPoints(
                weekSales = salesMetrics.weekly,
                referenceDate = referenceDate
            )
            SalesRangeChip.MONTH -> buildMonthPoints(
                monthSales = salesMetrics.monthly,
                referenceDate = referenceDate
            )
        }
    }

    val periodTotal = remember(points) { points.sumOf { it.value } }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Sales overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = formatCurrency(periodTotal),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(14.dp))

        SalesRangeChips(
            selectedRange = selectedRange,
            onSelected = { selectedRange = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                SalesLineChart(
                    points = points,
                    selectedRange = selectedRange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                SalesSummaryRow(points = points)
            }
        }
    }
}
@Composable
private fun SalesRangeChips(
    selectedRange: SalesRangeChip,
    onSelected: (SalesRangeChip) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(SalesRangeChip.entries) { chip ->
            FilterChip(
                selected = selectedRange == chip,
                onClick = { onSelected(chip) },
                label = { Text(chip.label) }
            )
        }
    }
}

@Composable
private fun SalesLineChart(
    points: List<ChartPoint>,
    selectedRange: SalesRangeChip,
    modifier: Modifier = Modifier
) {
    val lineColor = Color(0xFFE53935)
    val fillTopColor = Color(0xFFE53935).copy(alpha = 0.22f)
    val gridColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
    val axisTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            if (points.isEmpty()) return@Canvas

            val leftPadding = 8.dp.toPx()
            val rightPadding = 8.dp.toPx()
            val topPadding = 12.dp.toPx()
            val bottomPadding = 14.dp.toPx()

            val chartWidth = size.width - leftPadding - rightPadding
            val chartHeight = size.height - topPadding - bottomPadding

            if (chartWidth <= 0f || chartHeight <= 0f) return@Canvas

            val maxValue = max(points.maxOfOrNull { it.value } ?: 0.0, 1.0)
            val minValue = 0.0
            val range = maxValue - minValue

            val horizontalLines = 4
            repeat(horizontalLines + 1) { index ->
                val y = topPadding + (chartHeight / horizontalLines) * index
                drawLine(
                    color = gridColor,
                    start = Offset(leftPadding, y),
                    end = Offset(size.width - rightPadding, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val stepX = if (points.size > 1) chartWidth / (points.size - 1) else chartWidth

            fun pointOffset(index: Int, value: Double): Offset {
                val x = leftPadding + (stepX * index)
                val normalized = if (range == 0.0) 0f else ((value - minValue) / range).toFloat()
                val y = topPadding + chartHeight - (normalized * chartHeight)
                return Offset(x, y)
            }

            val linePath = Path()
            val fillPath = Path()

            points.forEachIndexed { index, point ->
                val offset = pointOffset(index, point.value)

                if (index == 0) {
                    linePath.moveTo(offset.x, offset.y)
                    fillPath.moveTo(offset.x, topPadding + chartHeight)
                    fillPath.lineTo(offset.x, offset.y)
                } else {
                    linePath.lineTo(offset.x, offset.y)
                    fillPath.lineTo(offset.x, offset.y)
                }

                if (index == points.lastIndex) {
                    fillPath.lineTo(offset.x, topPadding + chartHeight)
                    fillPath.close()
                }
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(fillTopColor, Color.Transparent)
                )
            )

            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(
                    width = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            )

            points.forEachIndexed { index, point ->
                val offset = pointOffset(index, point.value)
                val shouldDrawDot = when (selectedRange) {
                    SalesRangeChip.DAY -> index % 3 == 0 || point.value > 0
                    SalesRangeChip.WEEK -> true
                    SalesRangeChip.MONTH -> index % 4 == 0 || point.value > 0
                }

                if (shouldDrawDot) {
                    drawCircle(
                        color = lineColor,
                        radius = 3.5.dp.toPx(),
                        center = offset
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        XAxisLabels(
            points = points,
            selectedRange = selectedRange,
            textColor = axisTextColor
        )
    }
}

@Composable
private fun XAxisLabels(
    points: List<ChartPoint>,
    selectedRange: SalesRangeChip,
    textColor: Color
) {
    val visibleLabels = when (selectedRange) {
        SalesRangeChip.DAY -> points.mapIndexedNotNull { index, point ->
            if (index in listOf(0, 6, 12, 18, 23)) point else null
        }
        SalesRangeChip.WEEK -> points
        SalesRangeChip.MONTH -> points.mapIndexedNotNull { index, point ->
            val numericDay = point.xLabel.toIntOrNull()
            if (numericDay == 1 || numericDay == 5 || numericDay == 10 ||
                numericDay == 15 || numericDay == 20 || numericDay == 25 ||
                index == points.lastIndex
            ) point else null
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        visibleLabels.forEach { point ->
            Text(
                text = point.xLabel,
                style = MaterialTheme.typography.labelSmall,
                color = textColor
            )
        }
    }
}

@Composable
private fun SalesSummaryRow(points: List<ChartPoint>) {
    val total = points.sumOf { it.value }
    val average = if (points.isNotEmpty()) total / points.size else 0.0
    val highest = points.maxOfOrNull { it.value } ?: 0.0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        SummaryItem(
            title = "Total",
            value = formatCurrency(total)
        )
        SummaryItem(
            title = "Avg",
            value = formatCurrency(average)
        )
        SummaryItem(
            title = "Peak",
            value = formatCurrency(highest)
        )
    }
}

@Composable
private fun SummaryItem(
    title: String,
    value: String
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/* ---------------------- Mapping helpers ---------------------- */

private fun buildDayPoints(hours: List<GroupedSalesByHour>): List<ChartPoint> {
    val byHour = hours.associateBy { it.hour }

    return (0..23).map { hour ->
        ChartPoint(
            xLabel = hour.toString().padStart(2, '0'),
            value = byHour[hour]?.total ?: 0.0
        )
    }
}


private fun buildWeekPoints(
    weekSales: List<GroupedSalesByDay>,
    referenceDate: LocalDate
): List<ChartPoint> {
    val zoneId = ZoneId.systemDefault()

    val byDate = weekSales.associateBy {
        Instant.ofEpochMilli(it.date.time)
            .atZone(zoneId)
            .toLocalDate()
    }

    val startOfWeek = referenceDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    return (0..6).map { index ->
        val date = startOfWeek.plusDays(index.toLong())
        ChartPoint(
            xLabel = labels[index],
            value = byDate[date]?.total ?: 0.0
        )
    }
}
private fun buildMonthPoints(
    monthSales: List<GroupedSalesByDay>,
    referenceDate: LocalDate
): List<ChartPoint> {
    val zoneId = ZoneId.systemDefault()
    val yearMonth = YearMonth.from(referenceDate)

    val byDate = monthSales.associateBy {
        Instant.ofEpochMilli(it.date.time)
            .atZone(zoneId)
            .toLocalDate()
    }

    return (1..yearMonth.lengthOfMonth()).map { day ->
        val date = yearMonth.atDay(day)
        ChartPoint(
            xLabel = day.toString(),
            value = byDate[date]?.total ?: 0.0
        )
    }
}
private fun formatCurrency(value: Double): String {
    return NumberFormat.getCurrencyInstance().format(value)
}