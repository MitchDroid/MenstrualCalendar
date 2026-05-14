package com.bloomcycle.app.ui.education

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.ui.theme.FertileGreen
import com.bloomcycle.app.ui.theme.OvulationYellow
import com.bloomcycle.app.ui.theme.PeriodRed
import com.bloomcycle.app.ui.theme.PredictedPurple

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CyclePhaseGuideScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val phaseGuides = remember { EducationContentProvider.phaseGuides }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Cycle Phase Guide",
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
            // ── Intro ────────────────────────────────────────
            Text(
                text = "Understanding your cycle",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Your menstrual cycle has four distinct phases, each with unique hormonal changes that affect how you feel physically and emotionally.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Phase Timeline Visual ────────────────────────
            PhaseTimelineBar()

            Spacer(modifier = Modifier.height(24.dp))

            // ── Phase Cards ──────────────────────────────────
            phaseGuides.forEach { guide ->
                PhaseGuideCard(guide = guide)
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ── Phase Timeline Bar ──────────────────────────────────────────

@Composable
private fun PhaseTimelineBar() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Your Cycle at a Glance",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(12.dp))
            ) {
                // Menstrual ~18%
                Box(
                    modifier = Modifier
                        .weight(0.18f)
                        .height(24.dp)
                        .background(PeriodRed)
                )
                // Follicular ~32%
                Box(
                    modifier = Modifier
                        .weight(0.32f)
                        .height(24.dp)
                        .background(PredictedPurple)
                )
                // Ovulation ~7%
                Box(
                    modifier = Modifier
                        .weight(0.07f)
                        .height(24.dp)
                        .background(OvulationYellow)
                )
                // Luteal ~43%
                Box(
                    modifier = Modifier
                        .weight(0.43f)
                        .height(24.dp)
                        .background(FertileGreen)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TimelineLegendItem(color = PeriodRed, label = "Menstrual")
                TimelineLegendItem(color = PredictedPurple, label = "Follicular")
                TimelineLegendItem(color = OvulationYellow, label = "Ovulation")
                TimelineLegendItem(color = FertileGreen, label = "Luteal")
            }
        }
    }
}

@Composable
private fun TimelineLegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Expandable Phase Guide Card ─────────────────────────────────

@Composable
private fun PhaseGuideCard(guide: PhaseGuide) {
    var expanded by remember { mutableStateOf(false) }

    val accentColor = phaseAccentColor(guide.phase)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // ── Header ───────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(accentColor.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = guide.emoji,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = guide.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = guide.durationInfo,
                        style = MaterialTheme.typography.bodySmall,
                        color = accentColor
                    )
                }
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Description (always visible) ─────────────────
            Text(
                text = guide.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
            )

            // ── Expandable Details ───────────────────────────
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(modifier = Modifier.height(16.dp))

                    EducationSection(
                        title = "\uD83D\uDD2C What Happens",
                        items = guide.whatHappens,
                        accentColor = accentColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EducationSection(
                        title = "\uD83E\uDE7A Common Symptoms",
                        items = guide.commonSymptoms,
                        accentColor = accentColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EducationSection(
                        title = "\uD83D\uDC9C Self-Care Tips",
                        items = guide.selfCareTips,
                        accentColor = accentColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EducationSection(
                        title = "\uD83E\uDD57 Nutrition",
                        items = guide.nutritionTips,
                        accentColor = accentColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    EducationSection(
                        title = "\uD83C\uDFCB\uFE0F Exercise",
                        items = guide.exerciseTips,
                        accentColor = accentColor
                    )
                }
            }
        }
    }
}

// ── Reusable Education Section ──────────────────────────────────

@Composable
internal fun EducationSection(
    title: String,
    items: List<String>,
    accentColor: Color
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(6.dp))
        items.forEach { item ->
            Row(
                modifier = Modifier.padding(vertical = 2.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "\u2022",
                    style = MaterialTheme.typography.bodySmall,
                    color = accentColor,
                    modifier = Modifier.padding(end = 8.dp, top = 2.dp)
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ── Phase Color Helper ──────────────────────────────────────────

internal fun phaseAccentColor(phase: CyclePhase): Color = when (phase) {
    CyclePhase.MENSTRUAL -> PeriodRed
    CyclePhase.FOLLICULAR -> PredictedPurple
    CyclePhase.OVULATION -> OvulationYellow
    CyclePhase.LUTEAL -> FertileGreen
}
