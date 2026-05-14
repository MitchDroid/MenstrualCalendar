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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
                    text = "Upcoming Predictions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline items
            PredictionTimelineItem(
                color = PeriodRed,
                label = "Next Period",
                dateRange = "${prediction.nextPeriodStart.format(dateFormat)} – ${prediction.nextPeriodEnd.format(dateFormat)}",
                daysAway = "${prediction.daysUntilNextPeriod} days away"
            )

            Spacer(modifier = Modifier.height(12.dp))

            PredictionTimelineItem(
                color = FertileGreen,
                label = "Fertile Window",
                dateRange = "${prediction.fertileWindowStart.format(dateFormat)} – ${prediction.fertileWindowEnd.format(dateFormat)}",
                daysAway = null
            )

            Spacer(modifier = Modifier.height(12.dp))

            PredictionTimelineItem(
                color = OvulationYellow,
                label = "Ovulation",
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
                text = "\uD83D\uDCC8  Cycle Statistics",
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
                    label = "Avg Cycle",
                    unit = "days"
                )
                StatItem(
                    value = String.format("%.1f", stats.averagePeriodDuration),
                    label = "Avg Period",
                    unit = "days"
                )
                StatItem(
                    value = "${stats.cycleVariation}",
                    label = "Variation",
                    unit = "days"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(
                    value = "${stats.shortestCycle}",
                    label = "Shortest",
                    unit = "days"
                )
                StatItem(
                    value = "${stats.longestCycle}",
                    label = "Longest",
                    unit = "days"
                )
                StatItem(
                    value = "${stats.totalDaysLogged}",
                    label = "Days",
                    unit = "logged"
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
                text = "\uD83E\uDE7A  Top Symptoms",
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
                text = "\uD83D\uDE0A  Mood Trends",
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
                    text = "Flow Pattern",
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
                text = "Start logging to see insights",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Log your daily symptoms, mood, and flow to unlock personalized analytics and predictions.",
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
    val (title, message) = when (goal) {
        com.bloomcycle.app.domain.model.UserGoal.TRYING_TO_CONCEIVE -> {
            val dateFormat = DateTimeFormatter.ofPattern("MMM d")
            "Fertility Focus" to "Your next fertile window is ${prediction.fertileWindowStart.format(dateFormat)} – ${prediction.fertileWindowEnd.format(dateFormat)}. Ovulation is expected on ${prediction.ovulationDate.format(dateFormat)}."
        }
        com.bloomcycle.app.domain.model.UserGoal.AVOID_PREGNANCY -> {
            val dateFormat = DateTimeFormatter.ofPattern("MMM d")
            "Protection Reminder" to "Your fertile window is ${prediction.fertileWindowStart.format(dateFormat)} – ${prediction.fertileWindowEnd.format(dateFormat)}. Use extra precaution during this period."
        }
        com.bloomcycle.app.domain.model.UserGoal.TRACK_CYCLE -> {
            "Cycle Tracking" to "You're on day ${prediction.currentCycleDay} of your cycle (${phaseDisplayName(prediction.currentPhase)} phase). Next period in ${prediction.daysUntilNextPeriod} days."
        }
        com.bloomcycle.app.domain.model.UserGoal.MONITOR_HEALTH -> {
            "Health Monitor" to "Keep logging daily to build a comprehensive health profile. Consistent tracking reveals patterns your doctor can use."
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

private fun phaseDisplayName(phase: com.bloomcycle.app.domain.model.CyclePhase): String = when (phase) {
    com.bloomcycle.app.domain.model.CyclePhase.MENSTRUAL -> "Menstrual"
    com.bloomcycle.app.domain.model.CyclePhase.FOLLICULAR -> "Follicular"
    com.bloomcycle.app.domain.model.CyclePhase.OVULATION -> "Ovulation"
    com.bloomcycle.app.domain.model.CyclePhase.LUTEAL -> "Luteal"
}

// ── Helpers ──────────────────────────────────────────────────

private fun formatEnum(name: String): String =
    name.replace("_", " ").lowercase().replaceFirstChar { it.uppercase() }

private fun symptomDisplayName(symptom: Symptom): String = when (symptom) {
    Symptom.CRAMPS -> "\uD83E\uDD1F Cramps"
    Symptom.HEADACHE -> "\uD83E\uDD15 Headache"
    Symptom.BLOATING -> "\uD83C\uDF88 Bloating"
    Symptom.ACNE -> "\uD83D\uDCA2 Acne"
    Symptom.MOOD_SWINGS -> "\uD83C\uDFA2 Mood swings"
    Symptom.FATIGUE -> "\uD83D\uDE34 Fatigue"
    Symptom.TENDER_BREASTS -> "\uD83E\uDE77 Tender breasts"
    Symptom.BACK_PAIN -> "\uD83D\uDECB Back pain"
    Symptom.NAUSEA -> "\uD83E\uDD22 Nausea"
    Symptom.FOOD_CRAVINGS -> "\uD83C\uDF69 Food cravings"
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
