package com.bloomcycle.app.ui.insights

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.usecase.CycleHistoryEntry
import com.bloomcycle.app.ui.theme.FertileGreen
import com.bloomcycle.app.ui.theme.InfoBlue
import com.bloomcycle.app.ui.theme.PeriodRed
import com.bloomcycle.app.ui.theme.PredictedPurple

// ── Cycle Trend Card (contains both charts) ────────────────────

@Composable
fun CycleTrendCard(
    cycleHistory: List<CycleHistoryEntry>,
    modifier: Modifier = Modifier
) {
    // Filter to entries with known cycle lengths (exclude ongoing)
    val completedCycles = cycleHistory.filter { it.cycleLength != null }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.insights_cycle_trends),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (completedCycles.size < 2) {
                // Not enough data — show message
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.insights_need_more_cycles),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                val cycleLengths = completedCycles.mapNotNull { it.cycleLength }
                val periodLengths = cycleHistory.map { it.periodLength }
                val avg = cycleLengths.average().toFloat()
                val min = cycleLengths.min()
                val max = cycleLengths.max()

                // Trend label
                val trend = computeTrend(cycleLengths)
                val trendLabel = when (trend) {
                    TrendDirection.STABLE -> stringResource(R.string.insights_trend_stable)
                    TrendDirection.SHORTENING -> stringResource(R.string.insights_trend_shortening)
                    TrendDirection.LENGTHENING -> stringResource(R.string.insights_trend_lengthening)
                    TrendDirection.VARIABLE -> stringResource(R.string.insights_trend_variable)
                }
                val trendColor = when (trend) {
                    TrendDirection.STABLE -> FertileGreen
                    TrendDirection.SHORTENING -> InfoBlue
                    TrendDirection.LENGTHENING -> PredictedPurple
                    TrendDirection.VARIABLE -> PeriodRed.copy(alpha = 0.7f)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.insights_normal_range, min, max),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TrendBadge(label = trendLabel, color = trendColor)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Cycle Length Line Chart ──────────────
                Text(
                    text = stringResource(R.string.insights_cycle_length_trend),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                CycleLengthLineChart(
                    cycleLengths = cycleLengths,
                    average = avg,
                    minRange = min,
                    maxRange = max,
                    lineColor = PredictedPurple,
                    avgColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    rangeColor = PredictedPurple.copy(alpha = 0.08f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // ── Period Duration Bar Chart ────────────
                Text(
                    text = stringResource(R.string.insights_period_duration_trend),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))

                PeriodDurationBarChart(
                    periodLengths = periodLengths,
                    average = periodLengths.average().toFloat(),
                    barColor = PeriodRed,
                    avgColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                )
            }
        }
    }
}

// ── Cycle Length Line Chart ──────────────────────────────────────

@Composable
private fun CycleLengthLineChart(
    cycleLengths: List<Int>,
    average: Float,
    minRange: Int,
    maxRange: Int,
    lineColor: Color,
    avgColor: Color,
    rangeColor: Color,
    labelColor: Color,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val avgLabel = stringResource(R.string.insights_avg_line)

    // Draw-on animation: 0f → 1f reveals the chart left-to-right
    val progress = remember { Animatable(0f) }
    LaunchedEffect(cycleLengths) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 800, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = modifier) {
        val leftPadding = 36.dp.toPx()
        val rightPadding = 16.dp.toPx()
        val topPadding = 12.dp.toPx()
        val bottomPadding = 24.dp.toPx()

        val chartWidth = size.width - leftPadding - rightPadding
        val chartHeight = size.height - topPadding - bottomPadding

        if (cycleLengths.size < 2 || chartWidth <= 0 || chartHeight <= 0) return@Canvas

        // Y-axis range: add padding around min/max
        val yMin = (minRange - 3).coerceAtLeast(0).toFloat()
        val yMax = (maxRange + 3).toFloat()
        val yRange = (yMax - yMin).coerceAtLeast(1f)

        fun yToPixel(value: Float): Float =
            topPadding + chartHeight * (1f - (value - yMin) / yRange)

        fun xToPixel(index: Int): Float =
            leftPadding + chartWidth * index.toFloat() / (cycleLengths.size - 1).coerceAtLeast(1)

        // ── Normal range band (shaded) ──────────────
        val rangePath = Path().apply {
            moveTo(leftPadding, yToPixel(maxRange.toFloat()))
            lineTo(leftPadding + chartWidth, yToPixel(maxRange.toFloat()))
            lineTo(leftPadding + chartWidth, yToPixel(minRange.toFloat()))
            lineTo(leftPadding, yToPixel(minRange.toFloat()))
            close()
        }
        drawPath(rangePath, rangeColor)

        // ── Grid lines (light horizontal) ───────────
        val gridSteps = 4
        val gridStepValue = yRange / gridSteps
        for (i in 0..gridSteps) {
            val yValue = yMin + i * gridStepValue
            val y = yToPixel(yValue)
            drawLine(
                color = labelColor.copy(alpha = 0.12f),
                start = Offset(leftPadding, y),
                end = Offset(leftPadding + chartWidth, y),
                strokeWidth = 1.dp.toPx()
            )

            // Y-axis label
            val labelText = "${yValue.toInt()}"
            drawText(
                textMeasurer = textMeasurer,
                text = labelText,
                topLeft = Offset(2.dp.toPx(), y - 6.dp.toPx()),
                style = TextStyle(
                    fontSize = 9.sp,
                    color = labelColor
                )
            )
        }

        // ── Average dashed line ─────────────────────
        val avgY = yToPixel(average)
        drawLine(
            color = avgColor,
            start = Offset(leftPadding, avgY),
            end = Offset(leftPadding + chartWidth, avgY),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(8.dp.toPx(), 6.dp.toPx()), 0f
            )
        )

        // Average label
        drawText(
            textMeasurer = textMeasurer,
            text = "$avgLabel ${String.format("%.0f", average)}",
            topLeft = Offset(leftPadding + chartWidth - 48.dp.toPx(), avgY - 14.dp.toPx()),
            style = TextStyle(
                fontSize = 9.sp,
                color = avgColor,
                fontWeight = FontWeight.Medium
            )
        )

        // ── Data points and connecting line (animated clip) ─
        val points = cycleLengths.mapIndexed { index, value ->
            Offset(xToPixel(index), yToPixel(value.toFloat()))
        }

        // Clip to animated progress — reveals chart left-to-right
        val revealRight = leftPadding + chartWidth * progress.value

        clipRect(right = revealRight) {
            // Gradient fill under the line
            if (points.size >= 2) {
                val fillPath = Path().apply {
                    moveTo(points.first().x, yToPixel(yMin))
                    points.forEach { lineTo(it.x, it.y) }
                    lineTo(points.last().x, yToPixel(yMin))
                    close()
                }
                drawPath(
                    fillPath,
                    Brush.verticalGradient(
                        colors = listOf(
                            lineColor.copy(alpha = 0.15f),
                            lineColor.copy(alpha = 0.02f)
                        ),
                        startY = points.minOf { it.y },
                        endY = yToPixel(yMin)
                    )
                )
            }

            // Line
            val linePath = Path().apply {
                if (points.isNotEmpty()) {
                    moveTo(points.first().x, points.first().y)
                    for (i in 1 until points.size) {
                        lineTo(points[i].x, points[i].y)
                    }
                }
            }
            drawPath(
                linePath,
                color = lineColor,
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )

            // Dots — only show when the reveal has reached them
            points.forEach { point ->
                if (point.x <= revealRight) {
                    drawCircle(color = Color.White, radius = 5.dp.toPx(), center = point)
                    drawCircle(color = lineColor, radius = 4.dp.toPx(), center = point)
                }
            }
        }

        // ── X-axis labels (cycle numbers) — also animated
        cycleLengths.forEachIndexed { index, _ ->
            val x = xToPixel(index)
            if (x <= revealRight) {
                val label = "C${index + 1}"
                val textLayoutResult = textMeasurer.measure(
                    text = label,
                    style = TextStyle(fontSize = 9.sp, color = labelColor)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = Offset(
                        x - textLayoutResult.size.width / 2f,
                        size.height - bottomPadding + 6.dp.toPx()
                    ),
                    style = TextStyle(fontSize = 9.sp, color = labelColor)
                )
            }
        }
    }
}

// ── Period Duration Bar Chart ────────────────────────────────────

@Composable
private fun PeriodDurationBarChart(
    periodLengths: List<Int>,
    average: Float,
    barColor: Color,
    avgColor: Color,
    labelColor: Color,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val avgLabel = stringResource(R.string.insights_avg_line)

    // Grow-up animation: bars rise from 0 to full height
    val progress = remember { Animatable(0f) }
    LaunchedEffect(periodLengths) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(durationMillis = 700, delayMillis = 200, easing = FastOutSlowInEasing))
    }

    Canvas(modifier = modifier) {
        val leftPadding = 36.dp.toPx()
        val rightPadding = 16.dp.toPx()
        val topPadding = 12.dp.toPx()
        val bottomPadding = 24.dp.toPx()

        val chartWidth = size.width - leftPadding - rightPadding
        val chartHeight = size.height - topPadding - bottomPadding

        if (periodLengths.isEmpty() || chartWidth <= 0 || chartHeight <= 0) return@Canvas

        val maxValue = (periodLengths.max() + 2).toFloat()
        val minValue = 0f
        val yRange = maxValue - minValue

        fun yToPixel(value: Float): Float =
            topPadding + chartHeight * (1f - (value - minValue) / yRange)

        val barCount = periodLengths.size
        val totalBarSpace = chartWidth
        val barWidth = (totalBarSpace / barCount * 0.6f).coerceAtMost(32.dp.toPx())
        val barSpacing = totalBarSpace / barCount

        // ── Grid lines ──────────────────────────────
        val gridMax = maxValue.toInt()
        for (i in 0..gridMax step (gridMax / 4).coerceAtLeast(1)) {
            val y = yToPixel(i.toFloat())
            drawLine(
                color = labelColor.copy(alpha = 0.1f),
                start = Offset(leftPadding, y),
                end = Offset(leftPadding + chartWidth, y),
                strokeWidth = 1.dp.toPx()
            )
            drawText(
                textMeasurer = textMeasurer,
                text = "$i",
                topLeft = Offset(2.dp.toPx(), y - 6.dp.toPx()),
                style = TextStyle(fontSize = 9.sp, color = labelColor)
            )
        }

        // ── Bars (animated grow-up) ─────────────────
        val animProgress = progress.value
        periodLengths.forEachIndexed { index, value ->
            val centerX = leftPadding + barSpacing * index + barSpacing / 2
            val barLeft = centerX - barWidth / 2
            val fullBarTop = yToPixel(value.toFloat())
            val barBottom = yToPixel(0f)
            // Animate: bar grows from bottom → full height
            val barTop = barBottom + (fullBarTop - barBottom) * animProgress

            // Bar with rounded top via gradient
            drawRoundedBar(
                left = barLeft,
                top = barTop,
                right = barLeft + barWidth,
                bottom = barBottom,
                color = barColor,
                cornerRadius = 4.dp.toPx()
            )

            // X-axis label (always visible)
            val label = "C${index + 1}"
            val textLayoutResult = textMeasurer.measure(
                text = label,
                style = TextStyle(fontSize = 9.sp, color = labelColor)
            )
            drawText(
                textMeasurer = textMeasurer,
                text = label,
                topLeft = Offset(
                    centerX - textLayoutResult.size.width / 2f,
                    size.height - bottomPadding + 6.dp.toPx()
                ),
                style = TextStyle(fontSize = 9.sp, color = labelColor)
            )

            // Value label on top of bar (fade in at end of animation)
            if (animProgress > 0.7f) {
                val labelAlpha = ((animProgress - 0.7f) / 0.3f).coerceIn(0f, 1f)
                val valueLabel = "${value}d"
                val valueMeasure = textMeasurer.measure(
                    text = valueLabel,
                    style = TextStyle(fontSize = 8.sp, color = barColor)
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = valueLabel,
                    topLeft = Offset(
                        centerX - valueMeasure.size.width / 2f,
                        barTop - valueMeasure.size.height - 2.dp.toPx()
                    ),
                    style = TextStyle(
                        fontSize = 8.sp,
                        color = barColor.copy(alpha = labelAlpha),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }

        // ── Average dashed line ─────────────────────
        val avgY = yToPixel(average)
        drawLine(
            color = avgColor,
            start = Offset(leftPadding, avgY),
            end = Offset(leftPadding + chartWidth, avgY),
            strokeWidth = 1.5.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(
                floatArrayOf(8.dp.toPx(), 6.dp.toPx()), 0f
            )
        )
        drawText(
            textMeasurer = textMeasurer,
            text = "$avgLabel ${String.format("%.0f", average)}",
            topLeft = Offset(leftPadding + chartWidth - 48.dp.toPx(), avgY - 14.dp.toPx()),
            style = TextStyle(
                fontSize = 9.sp,
                color = avgColor,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

// ── Helper: Draw a bar with rounded top corners ─────────────────

private fun DrawScope.drawRoundedBar(
    left: Float,
    top: Float,
    right: Float,
    bottom: Float,
    color: Color,
    cornerRadius: Float
) {
    val barPath = Path().apply {
        // Start at bottom-left
        moveTo(left, bottom)
        // Left edge up to corner
        lineTo(left, top + cornerRadius)
        // Top-left rounded corner
        quadraticTo(left, top, left + cornerRadius, top)
        // Top edge
        lineTo(right - cornerRadius, top)
        // Top-right rounded corner
        quadraticTo(right, top, right, top + cornerRadius)
        // Right edge down
        lineTo(right, bottom)
        close()
    }
    drawPath(
        barPath,
        Brush.verticalGradient(
            colors = listOf(color, color.copy(alpha = 0.6f)),
            startY = top,
            endY = bottom
        )
    )
}

// ── Trend Badge ─────────────────────────────────────────────────

@Composable
private fun TrendBadge(label: String, color: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}

// ── Trend Calculation ───────────────────────────────────────────

private enum class TrendDirection { STABLE, SHORTENING, LENGTHENING, VARIABLE }

private fun computeTrend(cycleLengths: List<Int>): TrendDirection {
    if (cycleLengths.size < 3) return TrendDirection.STABLE

    val variation = cycleLengths.max() - cycleLengths.min()
    if (variation <= 2) return TrendDirection.STABLE

    // Simple linear trend: compare first half average to second half average
    val midpoint = cycleLengths.size / 2
    val firstHalf = cycleLengths.take(midpoint).average()
    val secondHalf = cycleLengths.drop(midpoint).average()
    val diff = secondHalf - firstHalf

    return when {
        diff < -1.5 -> TrendDirection.SHORTENING
        diff > 1.5 -> TrendDirection.LENGTHENING
        variation > 5 -> TrendDirection.VARIABLE
        else -> TrendDirection.STABLE
    }
}

// ── Previews ───────────────────────────────────────────────────

private fun sampleCycleHistory(): List<CycleHistoryEntry> {
    val baseDate = java.time.LocalDate.of(2025, 1, 5)
    return listOf(
        CycleHistoryEntry(baseDate, baseDate.plusDays(4), 5, 28),
        CycleHistoryEntry(baseDate.plusDays(28), baseDate.plusDays(32), 5, 30),
        CycleHistoryEntry(baseDate.plusDays(58), baseDate.plusDays(63), 6, 27),
        CycleHistoryEntry(baseDate.plusDays(85), baseDate.plusDays(89), 5, 29),
        CycleHistoryEntry(baseDate.plusDays(114), baseDate.plusDays(118), 5, 31),
        CycleHistoryEntry(baseDate.plusDays(145), baseDate.plusDays(150), 6, 28),
        CycleHistoryEntry(baseDate.plusDays(173), baseDate.plusDays(177), 5, null)
    )
}

@Preview(showBackground = true, widthDp = 380)
@Composable
private fun CycleTrendCardPreview() {
    MaterialTheme {
        CycleTrendCard(cycleHistory = sampleCycleHistory())
    }
}

@Preview(showBackground = true, widthDp = 380, name = "Few Cycles")
@Composable
private fun CycleTrendCardFewCyclesPreview() {
    MaterialTheme {
        CycleTrendCard(
            cycleHistory = sampleCycleHistory().take(1)
        )
    }
}

@Preview(showBackground = true, widthDp = 380, name = "Variable Trend")
@Composable
private fun CycleTrendCardVariablePreview() {
    val baseDate = java.time.LocalDate.of(2025, 1, 5)
    MaterialTheme {
        CycleTrendCard(
            cycleHistory = listOf(
                CycleHistoryEntry(baseDate, baseDate.plusDays(5), 6, 26),
                CycleHistoryEntry(baseDate.plusDays(26), baseDate.plusDays(29), 4, 34),
                CycleHistoryEntry(baseDate.plusDays(60), baseDate.plusDays(66), 7, 25),
                CycleHistoryEntry(baseDate.plusDays(85), baseDate.plusDays(88), 4, 33),
                CycleHistoryEntry(baseDate.plusDays(118), baseDate.plusDays(124), 7, null)
            )
        )
    }
}
