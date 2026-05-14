package com.bloomcycle.app.ui.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun InsightsScreen(
    modifier: Modifier = Modifier,
    viewModel: InsightsViewModel = hiltViewModel()
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
            text = "Insights",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "Your cycle analytics & predictions",
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

        Spacer(modifier = Modifier.height(16.dp))
    }
}
