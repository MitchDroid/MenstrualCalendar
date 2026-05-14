package com.bloomcycle.app.ui.education

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.ui.theme.FertileGreen
import com.bloomcycle.app.ui.theme.InfoBlue
import com.bloomcycle.app.ui.theme.OvulationYellow
import com.bloomcycle.app.ui.theme.PeriodRed
import com.bloomcycle.app.ui.theme.PredictedPurple
import com.bloomcycle.app.ui.theme.SuccessGreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HealthTipsScreen(
    currentPhase: CyclePhase?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPhase by remember { mutableStateOf(currentPhase) }
    var selectedCategory by remember { mutableStateOf<TipCategory?>(null) }

    val allTips = remember { EducationContentProvider.healthTips }
    val filteredTips = remember(selectedPhase, selectedCategory) {
        allTips.filter { tip ->
            (selectedPhase == null || selectedPhase in tip.applicablePhases) &&
                    (selectedCategory == null || tip.category == selectedCategory)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Health Tips",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // ── Current Phase Highlight ──────────────────────
            currentPhase?.let { phase ->
                CurrentPhaseHeader(phase = phase)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // ── Phase Filter Chips ───────────────────────────
            Text(
                text = "Filter by phase",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = selectedPhase == null,
                    onClick = { selectedPhase = null },
                    label = { Text("All Phases") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                CyclePhase.entries.forEach { phase ->
                    FilterChip(
                        selected = selectedPhase == phase,
                        onClick = {
                            selectedPhase = if (selectedPhase == phase) null else phase
                        },
                        label = { Text(phaseChipLabel(phase)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = phaseAccentColor(phase).copy(alpha = 0.2f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Category Filter Chips ────────────────────────
            Text(
                text = "Filter by category",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
                TipCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        },
                        label = { Text(categoryLabel(category)) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Tips List ────────────────────────────────────
            if (filteredTips.isEmpty()) {
                EmptyTipsState()
            } else {
                Text(
                    text = "${filteredTips.size} tips",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                filteredTips.forEach { tip ->
                    HealthTipCard(tip = tip)
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Current Phase Header ────────────────────────────────────────

@Composable
private fun CurrentPhaseHeader(phase: CyclePhase) {
    val color = phaseAccentColor(phase)
    val guide = EducationContentProvider.getPhaseGuide(phase)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(color.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = guide?.emoji ?: "\uD83C\uDF3A",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "You're in the ${phaseChipLabel(phase)} Phase",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Text(
                    text = "Tips marked below are especially relevant for you right now",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// ── Health Tip Card ─────────────────────────────────────────────

@Composable
private fun HealthTipCard(tip: HealthTip) {
    val categoryColor = categoryColor(tip.category)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = tip.emoji,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = tip.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                // Category badge
                Box(
                    modifier = Modifier
                        .background(
                            categoryColor.copy(alpha = 0.12f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = categoryLabel(tip.category),
                        style = MaterialTheme.typography.labelSmall,
                        color = categoryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tip.content,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            // Phase badges
            if (tip.applicablePhases.size < CyclePhase.entries.size) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    tip.applicablePhases.forEach { phase ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(phaseAccentColor(phase), CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = tip.applicablePhases.joinToString(", ") { phaseChipLabel(it) },
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// ── Empty State ─────────────────────────────────────────────────

@Composable
private fun EmptyTipsState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
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
            Text(
                text = "\uD83D\uDD0D",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "No tips match your filters",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Try adjusting your phase or category filters",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Helpers ─────────────────────────────────────────────────────

private fun phaseChipLabel(phase: CyclePhase): String = when (phase) {
    CyclePhase.MENSTRUAL -> "Menstrual"
    CyclePhase.FOLLICULAR -> "Follicular"
    CyclePhase.OVULATION -> "Ovulation"
    CyclePhase.LUTEAL -> "Luteal"
}

private fun categoryLabel(category: TipCategory): String = when (category) {
    TipCategory.NUTRITION -> "\uD83E\uDD57 Nutrition"
    TipCategory.EXERCISE -> "\uD83C\uDFCB\uFE0F Exercise"
    TipCategory.SELF_CARE -> "\uD83D\uDC9C Self-Care"
    TipCategory.SLEEP -> "\uD83D\uDCA4 Sleep"
    TipCategory.MENTAL_HEALTH -> "\uD83E\uDDD8 Mental Health"
}

@Composable
private fun categoryColor(category: TipCategory): Color = when (category) {
    TipCategory.NUTRITION -> SuccessGreen
    TipCategory.EXERCISE -> PeriodRed
    TipCategory.SELF_CARE -> PredictedPurple
    TipCategory.SLEEP -> InfoBlue
    TipCategory.MENTAL_HEALTH -> FertileGreen
}
