package com.bloomcycle.app.ui.reports

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.res.stringResource
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.usecase.CycleHistoryEntry
import com.bloomcycle.app.domain.usecase.CycleSummaryReport
import com.bloomcycle.app.ui.util.displayNameRes
import com.bloomcycle.app.ui.theme.FertileGreen
import com.bloomcycle.app.ui.theme.InfoBlue
import com.bloomcycle.app.ui.theme.PeriodRed
import com.bloomcycle.app.ui.theme.PredictedPurple
import com.bloomcycle.app.ui.theme.SuccessGreen
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Handle export events
    LaunchedEffect(uiState.exportEvent) {
        uiState.exportEvent?.let { event ->
            val intent = when (event) {
                is ExportEvent.ShareCsv -> event.intent
                is ExportEvent.ShareTextFile -> event.intent
                is ExportEvent.ShareTextInline -> event.intent
            }
            context.startActivity(Intent.createChooser(intent, "Share via"))
            viewModel.consumeExportEvent()
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.reports_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (!uiState.isLoaded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(40.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
            return@Scaffold
        }

        val report = uiState.report

        if (report == null || report.totalDaysTracked == 0) {
            EmptyReportsState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // ── Overview Card ────────────────────────────
            OverviewCard(report = report)
            Spacer(modifier = Modifier.height(16.dp))

            // ── Cycle History ────────────────────────────
            if (report.cycleHistory.isNotEmpty()) {
                CycleHistoryCard(history = report.cycleHistory)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Statistics Summary ───────────────────────
            if (report.cycleHistory.size >= 2) {
                StatisticsSummaryCard(report = report)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Symptom & Mood Summary ──────────────────
            if (report.symptomFrequencies.isNotEmpty() || report.moodDistribution.isNotEmpty()) {
                HealthSummaryCard(report = report)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Export Section ───────────────────────────
            ExportCard(
                onExportCsv = { viewModel.exportCsv() },
                onExportTextFile = { viewModel.exportTextReport() },
                onShareText = { viewModel.shareTextSummary() }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Overview Card ───────────────────────────────────────────────

@Composable
private fun OverviewCard(report: CycleSummaryReport) {
    val dateFormat = DateTimeFormatter.ofPattern("MMM d, yyyy")

    val dateStr = report.generatedAt.format(dateFormat)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.reports_overview),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.reports_generated, dateStr),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                OverviewStat(
                    value = "${report.totalDaysTracked}",
                    label = stringResource(R.string.reports_days_tracked),
                    color = MaterialTheme.colorScheme.primary
                )
                OverviewStat(
                    value = "${report.totalPeriodsDetected}",
                    label = stringResource(R.string.reports_periods_detected),
                    color = PeriodRed
                )
                OverviewStat(
                    value = report.averageCycleLength?.let { String.format("%.0f", it) } ?: "—",
                    label = stringResource(R.string.reports_avg_cycle_days),
                    color = PredictedPurple
                )
                OverviewStat(
                    value = report.averagePeriodLength?.let { String.format("%.0f", it) } ?: "—",
                    label = stringResource(R.string.reports_avg_period_days),
                    color = FertileGreen
                )
            }

            // Date range
            val firstDate = report.firstLogDate?.format(dateFormat) ?: "—"
            val lastDate = report.lastLogDate?.format(dateFormat) ?: "—"

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.reports_tracking_period, "$firstDate — $lastDate"),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun OverviewStat(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}

// ── Cycle History Card ──────────────────────────────────────────

@Composable
private fun CycleHistoryCard(history: List<CycleHistoryEntry>) {
    val dateFormat = DateTimeFormatter.ofPattern("MMM d")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.reports_cycle_history),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (history.size != 1)
                    stringResource(R.string.reports_periods_detected_plural, history.size)
                else
                    stringResource(R.string.reports_period_detected_singular, history.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            history.reversed().forEachIndexed { index, entry ->
                CycleHistoryRow(
                    index = history.size - index,
                    entry = entry,
                    dateFormat = dateFormat,
                    isLatest = index == 0
                )
                if (index < history.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun CycleHistoryRow(
    index: Int,
    entry: CycleHistoryEntry,
    dateFormat: DateTimeFormatter,
    isLatest: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Index badge
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    if (isLatest) PeriodRed.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$index",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isLatest) PeriodRed else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${entry.periodStart.format(dateFormat)} – ${entry.periodEnd.format(dateFormat)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.reports_period_duration, entry.periodLength),
                    style = MaterialTheme.typography.labelSmall,
                    color = PeriodRed
                )
                entry.cycleLength?.let { len ->
                    Text(
                        text = stringResource(R.string.reports_cycle_duration, len),
                        style = MaterialTheme.typography.labelSmall,
                        color = PredictedPurple
                    )
                } ?: Text(
                    text = stringResource(R.string.reports_current_cycle),
                    style = MaterialTheme.typography.labelSmall,
                    color = FertileGreen,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (isLatest) {
            Box(
                modifier = Modifier
                    .background(PeriodRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = stringResource(R.string.reports_latest),
                    style = MaterialTheme.typography.labelSmall,
                    color = PeriodRed,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ── Statistics Summary Card ─────────────────────────────────────

@Composable
private fun StatisticsSummaryCard(report: CycleSummaryReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.reports_cycle_statistics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MiniStat(
                    label = stringResource(R.string.reports_shortest),
                    value = "${report.shortestCycle ?: "—"}",
                    unit = stringResource(R.string.days),
                    color = FertileGreen
                )
                MiniStat(
                    label = stringResource(R.string.reports_average),
                    value = report.averageCycleLength?.let { String.format("%.1f", it) } ?: "—",
                    unit = stringResource(R.string.days),
                    color = PredictedPurple
                )
                MiniStat(
                    label = stringResource(R.string.reports_longest),
                    value = "${report.longestCycle ?: "—"}",
                    unit = stringResource(R.string.days),
                    color = PeriodRed
                )
            }

            report.cycleStats?.let { stats ->
                if (stats.cycleVariation > 0) {
                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(8.dp))

                    val regularity = when {
                        stats.cycleVariation <= 2 -> stringResource(R.string.reports_very_regular)
                        stats.cycleVariation <= 5 -> stringResource(R.string.reports_fairly_regular)
                        stats.cycleVariation <= 8 -> stringResource(R.string.reports_somewhat_irregular)
                        else -> stringResource(R.string.reports_irregular)
                    }
                    val regularityColor = when {
                        stats.cycleVariation <= 2 -> SuccessGreen
                        stats.cycleVariation <= 5 -> FertileGreen
                        stats.cycleVariation <= 8 -> InfoBlue
                        else -> PeriodRed
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(regularityColor, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$regularity (±${stringResource(R.string.reports_days_variation, stats.cycleVariation)})",
                            style = MaterialTheme.typography.bodySmall,
                            color = regularityColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MiniStat(label: String, value: String, unit: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

// ── Health Summary Card ─────────────────────────────────────────

@Composable
private fun HealthSummaryCard(report: CycleSummaryReport) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.reports_health_summary),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Top symptoms
            if (report.symptomFrequencies.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.reports_frequent_symptoms),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                report.symptomFrequencies.take(3).forEach { freq ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${symptomEmoji(freq.symptom.name)} ${stringResource(freq.symptom.displayNameRes())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.reports_days_percent, freq.count, (freq.percentage * 100).toInt()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Top moods
            if (report.moodDistribution.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.reports_frequent_moods),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                report.moodDistribution.take(3).forEach { mood ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${moodEmoji(mood.mood.name)} ${stringResource(mood.mood.displayNameRes())}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.reports_days_percent, mood.count, (mood.percentage * 100).toInt()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Flow pattern
            if (report.flowPatterns.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.reports_flow_breakdown),
                    style = MaterialTheme.typography.labelMedium,
                    color = PeriodRed,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                report.flowPatterns.forEach { flow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = stringResource(flow.intensity.displayNameRes()),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = stringResource(R.string.reports_days_percent, flow.dayCount, (flow.percentage * 100).toInt()),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// ── Export Card ──────────────────────────────────────────────────

@Composable
private fun ExportCard(
    onExportCsv: () -> Unit,
    onExportTextFile: () -> Unit,
    onShareText: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.reports_export_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.reports_export_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CSV Export
            ExportButton(
                icon = Icons.Filled.TableChart,
                label = stringResource(R.string.reports_export_csv),
                description = stringResource(R.string.reports_export_csv_desc),
                onClick = onExportCsv,
                isPrimary = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Text Report Export
            ExportButton(
                icon = Icons.Filled.FileDownload,
                label = stringResource(R.string.reports_export_report),
                description = stringResource(R.string.reports_export_report_desc),
                onClick = onExportTextFile,
                isPrimary = false
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Share
            ExportButton(
                icon = Icons.Filled.Share,
                label = stringResource(R.string.reports_quick_share),
                description = stringResource(R.string.reports_quick_share_desc),
                onClick = onShareText,
                isPrimary = false
            )
        }
    }
}

@Composable
private fun ExportButton(
    icon: ImageVector,
    label: String,
    description: String,
    onClick: () -> Unit,
    isPrimary: Boolean
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                )
            }
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ── Empty State ─────────────────────────────────────────────────

@Composable
private fun EmptyReportsState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "\uD83D\uDCC4",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.reports_empty_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.reports_empty_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// ── Helpers ─────────────────────────────────────────────────────

private fun symptomEmoji(name: String): String = when (name) {
    "CRAMPS" -> "\uD83E\uDD1F"
    "HEADACHE" -> "\uD83E\uDD15"
    "BLOATING" -> "\uD83C\uDF88"
    "ACNE" -> "\uD83D\uDCA2"
    "MOOD_SWINGS" -> "\uD83C\uDFA2"
    "FATIGUE" -> "\uD83D\uDE34"
    "TENDER_BREASTS" -> "\uD83E\uDE77"
    "BACK_PAIN" -> "\uD83D\uDECB"
    "NAUSEA" -> "\uD83E\uDD22"
    "FOOD_CRAVINGS" -> "\uD83C\uDF69"
    else -> ""
}

private fun moodEmoji(name: String): String = when (name) {
    "HAPPY" -> "\uD83D\uDE0A"
    "SAD" -> "\uD83D\uDE22"
    "ANXIOUS" -> "\uD83D\uDE1F"
    "IRRITABLE" -> "\uD83D\uDE24"
    "CALM" -> "\uD83D\uDE0C"
    "ENERGETIC" -> "\uD83D\uDE04"
    else -> ""
}
