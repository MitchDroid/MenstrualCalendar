package com.bloomcycle.app.ui.insights

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bloomcycle.app.R
import com.bloomcycle.app.ui.theme.FertileGreen
import com.bloomcycle.app.ui.theme.PredictedPurple

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel(),
    onNavigateToCycleGuide: () -> Unit = {},
    onNavigateToSymptomGuide: () -> Unit = {},
    onNavigateToHealthTips: (String?) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (!uiState.isLoaded) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ── Header ───────────────────────────────────────
        Text(
            text = stringResource(R.string.insights_title),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.insights_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Prediction Card ──────────────────────────────
        uiState.prediction?.let { prediction ->
            PredictionCard(prediction = prediction)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Cycle Stats ──────────────────────────────────
        uiState.cycleStats?.let { stats ->
            CycleStatsCard(stats = stats)
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Analytics sections (require logged data) ─────
        if (!uiState.hasData) {
            InsightsEmptyState()
        } else {
            // Symptom Frequency
            if (uiState.symptomFrequencies.isNotEmpty()) {
                SymptomFrequencyCard(frequencies = uiState.symptomFrequencies)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Mood Distribution
            if (uiState.moodDistribution.isNotEmpty()) {
                MoodDistributionCard(distribution = uiState.moodDistribution)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Flow Pattern
            if (uiState.flowPatterns.isNotEmpty()) {
                FlowPatternCard(patterns = uiState.flowPatterns)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Goal-specific tip
            uiState.prediction?.let { prediction ->
                uiState.userGoal?.let { goal ->
                    GoalInsightCard(prediction = prediction, goal = goal)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        // ── Education Section ────────────────────────────
        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.insights_learn_section),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.insights_learn_subtitle),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        EducationNavigationCard(
            icon = Icons.Filled.Psychology,
            title = stringResource(R.string.insights_cycle_phase_guide),
            subtitle = stringResource(R.string.insights_cycle_phase_guide_desc),
            accentColor = PredictedPurple,
            onClick = onNavigateToCycleGuide
        )

        Spacer(modifier = Modifier.height(10.dp))

        EducationNavigationCard(
            icon = Icons.Filled.MenuBook,
            title = stringResource(R.string.insights_symptom_guide),
            subtitle = stringResource(R.string.insights_symptom_guide_desc),
            accentColor = MaterialTheme.colorScheme.tertiary,
            onClick = onNavigateToSymptomGuide
        )

        Spacer(modifier = Modifier.height(10.dp))

        EducationNavigationCard(
            icon = Icons.Filled.AutoAwesome,
            title = stringResource(R.string.insights_health_tips),
            subtitle = stringResource(R.string.insights_health_tips_desc),
            accentColor = FertileGreen,
            onClick = {
                val phaseArg = uiState.prediction?.currentPhase?.name
                onNavigateToHealthTips(phaseArg)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// ── Education Navigation Card ───────────────────────────────────

@Composable
private fun EducationNavigationCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = accentColor
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = stringResource(R.string.go),
                modifier = Modifier.size(20.dp),
                tint = accentColor
            )
        }
    }
}
