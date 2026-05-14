package com.bloomcycle.app.ui.insights

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bloomcycle.app.R
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.Symptom
import com.bloomcycle.app.domain.usecase.CyclePrediction
import com.bloomcycle.app.domain.usecase.CycleStats
import com.bloomcycle.app.domain.usecase.FlowPattern
import com.bloomcycle.app.domain.usecase.MoodDistribution
import com.bloomcycle.app.domain.usecase.SymptomFrequency
import com.bloomcycle.app.ui.theme.FertileGreen
import com.bloomcycle.app.ui.theme.OvulationYellow
import com.bloomcycle.app.ui.theme.PeriodRed
import com.bloomcycle.app.ui.theme.PredictedPurple
import java.time.format.DateTimeFormatter

// ── Prediction Timeline Card ─────────────────────────────────

@Composable
fun PredictionCard(
    prediction: CyclePrediction,
    modifier: Modifier = Modifier
) {
    val dateFormat = DateTimeFormatter.ofPattern("MMM d")

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.insights_upcoming_predictions),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline items
            PredictionTimelineItem(
                color = PeriodRed,
                label = stringResource(R.string.insights_next_period),
                dateRange = "${prediction.nextPeriodStart.format(dateFormat)} – ${prediction.nextPeriodEnd.format(dateFormat)}",
                daysAway = "${prediction.daysUntilNextPeriod} ${stringResource(R.string.insights_days_away)}"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PredictionTimelineItem(
                color = FertileGreen,
                label = stringResource(R.string.insights_fertile_window),
                dateRange = "${prediction.fertileWindowStart.format(dateFormat)} – ${prediction.fertileWindowEnd.format(dateFormat)}",
                daysAway = null
            )

            Spacer(modifier = Modifier.height(12.dp))

            PredictionTimelineItem(
                color = OvulationYellow,
                label = stringResource(R.string.insights_ovulation),
                dateRange = prediction.ovulationDate.format(dateFormat),
                daysAway = null
            )
        }
    }
}

@Composable
private fun PredictionTimelineItem(
    color: Color,
    label: String,
    dateRange: String,
    daysAway: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = dateRange,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
        }
        daysAway?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

// ── Cycle Statistics Card ────────────────────────────────────

@Composable
fun CycleStatsCard(
    stats: CycleStats,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.insights_cycle_statistics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = String.format("%.1f", stats.averageCycleLength),
                    label = stringResource(R.string.insights_avg_cycle),
                    unit = stringResource(R.string.days)
                )
                StatItem(
                    value = String.format("%.1f", stats.averagePeriodDuration),
                    label = stringResource(R.string.insights_avg_period),
                    unit = stringResource(R.string.days)
                )
                StatItem(
                    value = "${stats.cycleVariation}",
                    label = stringResource(R.string.insights_variation),
                    unit = stringResource(R.string.days)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "${stats.shortestCycle}",
                    label = stringResource(R.string.insights_shortest),
                    unit = stringResource(R.string.days)
                )
                StatItem(
                    value = "${stats.longestCycle}",
                    label = stringResource(R.string.insights_longest),
                    unit = stringResource(R.string.days)
                )
                StatItem(
                    value = "${stats.totalDaysLogged}",
                    label = stringResource(R.string.insights_days_logged),
                    unit = stringResource(R.string.insights_logged)
                )
            }
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = unit,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

// ── Symptom Frequency Chart ──────────────────────────────────

@Composable
fun SymptomFrequencyCard(
    frequencies: List<SymptomFrequency>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.insights_top_symptoms),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            frequencies.take(6).forEach { freq ->
                BarChartRow(
                    label = symptomDisplayName(freq.symptom),
                    count = freq.count,
                    percentage = freq.percentage,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ── Mood Distribution Card ───────────────────────────────────

@Composable
fun MoodDistributionCard(
    distribution: List<MoodDistribution>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = stringResource(R.string.insights_mood_trends),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(16.dp))

            distribution.forEach { mood ->
                BarChartRow(
                    label = "${moodEmoji(mood.mood)} ${formatEnum(mood.mood.name)}",
                    count = mood.count,
                    percentage = mood.percentage,
                    color = moodColor(mood.mood)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ── Flow Pattern Card ────────────────────────────────────────

@Composable
fun FlowPatternCard(
    patterns: List<FlowPattern>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = PeriodRed
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.insights_flow_pattern),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            patterns.forEach { pattern ->
                BarChartRow(
                    label = formatEnum(pattern.intensity.name),
                    count = pattern.dayCount,
                    percentage = pattern.percentage,
                    color = flowIntensityColor(pattern.intensity)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ── Shared Bar Chart Row ─────────────────────────────────────

@Composable
private fun BarChartRow(
    label: String,
    count: Int,
    percentage: Float,
    color: Color
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "$count  (${(percentage * 100).toInt()}%)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { percentage.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

// ── Empty State ──────────────────────────────────────────────

@Composable
fun InsightsEmptyState(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.Favorite,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.insights_empty_title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.insights_empty_description),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Goal-Specific Insight Card ───────────────────────────────

@Composable
fun GoalInsightCard(
    prediction: CyclePrediction,
    goal: com.bloomcycle.app.domain.model.UserGoal,
    modifier: Modifier = Modifier
) {
    val dateFormat = DateTimeFormatter.ofPattern("MMM d")
    val (title, message) = when (goal) {
        com.bloomcycle.app.domain.model.UserGoal.TRYING_TO_CONCEIVE -> {
            val fertileRange = "${prediction.fertileWindowStart.format(dateFormat)} – ${prediction.fertileWindowEnd.format(dateFormat)}"
            stringResource(R.string.insights_goal_fertility_title) to stringResource(R.string.insights_goal_fertility_msg, fertileRange, prediction.ovulationDate.format(dateFormat))
        }
        com.bloomcycle.app.domain.model.UserGoal.AVOID_PREGNANCY -> {
            val fertileRange = "${prediction.fertileWindowStart.format(dateFormat)} – ${prediction.fertileWindowEnd.format(dateFormat)}"
            stringResource(R.string.insights_goal_protection_title) to stringResource(R.string.insights_goal_protection_msg, fertileRange)
        }
        com.bloomcycle.app.domain.model.UserGoal.TRACK_CYCLE -> {
            stringResource(R.string.insights_goal_tracking_title) to stringResource(R.string.insights_goal_tracking_msg, prediction.currentCycleDay, phaseDisplayName(prediction.currentPhase), prediction.daysUntilNextPeriod)
        }
        com.bloomcycle.app.domain.model.UserGoal.MONITOR_HEALTH -> {
            stringResource(R.string.insights_goal_health_title) to stringResource(R.string.insights_goal_health_msg)
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.Favorite,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
            )
        }
    }
}

@Composable
private fun phaseDisplayName(phase: com.bloomcycle.app.domain.model.CyclePhase): String = when (phase) {
    com.bloomcycle.app.domain.model.CyclePhase.MENSTRUAL -> stringResource(R.string.phase_menstrual)
    com.bloomcycle.app.domain.model.CyclePhase.FOLLICULAR -> stringResource(R.string.phase_follicular)
    com.bloomcycle.app.domain.model.CyclePhase.OVULATION -> stringResource(R.string.phase_ovulation)
    com.bloomcycle.app.domain.model.CyclePhase.LUTEAL -> stringResource(R.string.phase_luteal)
}

// ── Helpers ──────────────────────────────────────────────────

private fun formatEnum(name: String): String =
    name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

@Composable
private fun symptomDisplayName(symptom: Symptom): String = when (symptom) {
    Symptom.CRAMPS -> "\uD83E\uDD1F ${stringResource(R.string.symptom_cramps)}"
    Symptom.HEADACHE -> "\uD83E\uDD15 ${stringResource(R.string.symptom_headache)}"
    Symptom.BLOATING -> "\uD83C\uDF88 ${stringResource(R.string.symptom_bloating)}"
    Symptom.ACNE -> "\uD83D\uDCA2 ${stringResource(R.string.symptom_acne)}"
    Symptom.MOOD_SWINGS -> "\uD83C\uDFA2 ${stringResource(R.string.symptom_mood_swings)}"
    Symptom.FATIGUE -> "\uD83D\uDE34 ${stringResource(R.string.symptom_fatigue)}"
    Symptom.TENDER_BREASTS -> "\uD83E\uDE77 ${stringResource(R.string.symptom_tender_breasts)}"
    Symptom.BACK_PAIN -> "\uD83D\uDECB ${stringResource(R.string.symptom_back_pain)}"
    Symptom.NAUSEA -> "\uD83E\uDD22 ${stringResource(R.string.symptom_nausea)}"
    Symptom.FOOD_CRAVINGS -> "\uD83C\uDF69 ${stringResource(R.string.symptom_food_cravings)}"
}

private fun moodEmoji(mood: Mood): String = when (mood) {
    Mood.HAPPY -> "\uD83D\uDE0A"
    Mood.SAD -> "\uD83D\uDE22"
    Mood.ANXIOUS -> "\uD83D\uDE1F"
    Mood.IRRITABLE -> "\uD83D\uDE24"
    Mood.CALM -> "\uD83D\uDE0C"
    Mood.ENERGETIC -> "\uD83D\uDE04"
}

@Composable
private fun moodColor(mood: Mood): Color = when (mood) {
    Mood.HAPPY -> FertileGreen
    Mood.SAD -> PredictedPurple
    Mood.ANXIOUS -> OvulationYellow
    Mood.IRRITABLE -> PeriodRed
    Mood.CALM -> MaterialTheme.colorScheme.primary
    Mood.ENERGETIC -> FertileGreen
}

private fun flowIntensityColor(intensity: FlowIntensity): Color = when (intensity) {
    FlowIntensity.NONE -> Color.Gray
    FlowIntensity.SPOTTING -> PeriodRed.copy(alpha = 0.3f)
    FlowIntensity.LIGHT -> PeriodRed.copy(alpha = 0.5f)
    FlowIntensity.MEDIUM -> PeriodRed.copy(alpha = 0.75f)
    FlowIntensity.HEAVY -> PeriodRed
}
