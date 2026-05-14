package com.bloomcycle.app.ui.calendar

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.ui.theme.FertileGreen
import com.bloomcycle.app.ui.theme.OvulationYellow
import com.bloomcycle.app.ui.theme.PeriodRed
import com.bloomcycle.app.ui.theme.PredictedPurple
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    onNavigateToTracking: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToTracking(uiState.selectedDate.toString()) },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Day", fontWeight = FontWeight.SemiBold)
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // ── Month Navigation Header ──────────────────
            MonthHeader(
                monthYear = "${uiState.currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${uiState.currentMonth.year}",
                onPreviousMonth = { viewModel.navigateMonth(-1) },
                onNextMonth = { viewModel.navigateMonth(1) },
                onToday = { viewModel.goToToday() }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Calendar Grid ────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                CalendarGrid(
                    days = uiState.calendarDays,
                    selectedDate = uiState.selectedDate,
                    onDayClick = { date -> viewModel.selectDate(date) },
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Legend ────────────────────────────────────
            CalendarLegend()

            Spacer(modifier = Modifier.height(16.dp))

            // ── Selected Day Info ─────────────────────────
            SelectedDayCard(
                uiState = uiState,
                onLogDay = { onNavigateToTracking(uiState.selectedDate.toString()) }
            )

            Spacer(modifier = Modifier.height(80.dp)) // FAB clearance
        }
    }
}

@Composable
private fun MonthHeader(
    monthYear: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onToday: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(onClick = onPreviousMonth) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous month",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = monthYear,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row {
            IconButton(onClick = onToday) {
                Icon(
                    Icons.Filled.Today,
                    contentDescription = "Go to today",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onNextMonth) {
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next month",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
private fun CalendarLegend() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        LegendItem(color = PeriodRed, label = "Period")
        LegendItem(color = PredictedPurple, label = "Predicted")
        LegendItem(color = FertileGreen, label = "Fertile")
        LegendItem(color = OvulationYellow, label = "Ovulation")
    }
}

@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color.copy(alpha = 0.6f), CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun SelectedDayCard(
    uiState: CalendarUiState,
    onLogDay: () -> Unit
) {
    val selectedDay = uiState.calendarDays.find { it.date == uiState.selectedDate }
    val dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = uiState.selectedDate.format(dateFormatter),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedDay != null) {
                // Cycle day info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    selectedDay.cycleDay?.let { day ->
                        InfoPill(label = "Cycle Day", value = "$day")
                    }
                    selectedDay.cyclePhase?.let { phase ->
                        InfoPill(
                            label = "Phase",
                            value = phaseDisplayName(phase)
                        )
                    }
                    InfoPill(
                        label = "Status",
                        value = markerDisplayName(selectedDay.markerType)
                    )
                }
            }

            // Show existing log summary or CTA to log
            val log = uiState.selectedDayLog
            if (log != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Today's Log",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val logSummary = buildList {
                            log.flowIntensity?.let { add("\uD83E\uDE78 ${formatEnum(it.name)}") }
                            log.mood?.let { add("${moodEmoji(it)} ${formatEnum(it.name)}") }
                            if (log.symptoms.isNotEmpty()) {
                                add("\uD83E\uDE7A ${log.symptoms.size} symptom(s)")
                            }
                            log.notes?.let { add("\uD83D\uDCDD Note added") }
                        }
                        Text(
                            text = logSummary.joinToString("  \u2022  "),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onLogDay,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Edit Log")
                }
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = onLogDay,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add log for this day")
                }
            }
        }
    }
}

@Composable
private fun InfoPill(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun phaseDisplayName(phase: CyclePhase): String = when (phase) {
    CyclePhase.MENSTRUAL -> "Menstrual"
    CyclePhase.FOLLICULAR -> "Follicular"
    CyclePhase.OVULATION -> "Ovulation"
    CyclePhase.LUTEAL -> "Luteal"
}

private fun markerDisplayName(type: DayMarkerType): String = when (type) {
    DayMarkerType.PERIOD -> "Period"
    DayMarkerType.PREDICTED_PERIOD -> "Predicted"
    DayMarkerType.FERTILE -> "Fertile"
    DayMarkerType.OVULATION -> "Ovulation"
    DayMarkerType.LOGGED -> "Logged"
    DayMarkerType.NONE -> "Regular"
}

private fun formatEnum(name: String): String =
    name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

private fun moodEmoji(mood: com.bloomcycle.app.domain.model.Mood): String = when (mood) {
    com.bloomcycle.app.domain.model.Mood.HAPPY -> "\uD83D\uDE0A"
    com.bloomcycle.app.domain.model.Mood.SAD -> "\uD83D\uDE22"
    com.bloomcycle.app.domain.model.Mood.ANXIOUS -> "\uD83D\uDE1F"
    com.bloomcycle.app.domain.model.Mood.IRRITABLE -> "\uD83D\uDE24"
    com.bloomcycle.app.domain.model.Mood.CALM -> "\uD83D\uDE0C"
    com.bloomcycle.app.domain.model.Mood.ENERGETIC -> "\uD83D\uDE04"
}
