package com.bloomcycle.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomcycle.app.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.domain.model.FertilityStatus
import kotlinx.coroutines.delay
import java.time.LocalDate

@Composable
fun HomeScreen(
    onNavigateToTracking: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // ── Staggered entrance animation ─────────────────────
    var showHeader by remember { mutableStateOf(false) }
    var showCircle by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showCta by remember { mutableStateOf(false) }
    var showTip by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showHeader = true
        delay(100)
        showCircle = true
        delay(120)
        showStats = true
        delay(120)
        showCta = true
        delay(100)
        showTip = true
    }

    // ── Phase color smooth transition ────────────────────
    val animatedPhaseColor by animateColorAsState(
        targetValue = phaseColor(uiState.currentPhase),
        animationSpec = tween(durationMillis = 600),
        label = "phaseColor"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── Header ────────────────────────────────────────────
        AnimatedVisibility(
            visible = showHeader,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { -it / 4 }
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Cycle Day Circle ──────────────────────────────────
        AnimatedVisibility(
            visible = showCircle,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 }
        ) {
        Card(
            modifier = Modifier
                .size(180.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = animatedPhaseColor
            )
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.WaterDrop,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.home_cycle_day, uiState.cycleDay),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = phaseDisplayName(uiState.currentPhase),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
        } // close AnimatedVisibility for circle

        Spacer(modifier = Modifier.height(24.dp))

        // ── Quick Stats ───────────────────────────────────────
        AnimatedVisibility(
            visible = showStats,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        title = stringResource(R.string.home_next_period),
                        value = "${uiState.daysUntilNextPeriod} ${stringResource(R.string.days)}",
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        title = stringResource(R.string.home_cycle_length),
                        value = "${uiState.cycleLength} ${stringResource(R.string.days)}",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        title = stringResource(R.string.home_fertility),
                        value = fertilityDisplayName(uiState.fertilityStatus),
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        title = stringResource(R.string.home_phase),
                        value = phaseDisplayName(uiState.currentPhase),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Log Today CTA ─────────────────────────────────────
        AnimatedVisibility(
            visible = showCta,
            enter = fadeIn(tween(350))
        ) {
        val todayStr = LocalDate.now().toString()
        if (uiState.todayLog != null) {
            Button(
                onClick = { onNavigateToTracking(todayStr) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.home_edit_today_log),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Button(
                onClick = { onNavigateToTracking(todayStr) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.home_log_today),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        } // close AnimatedVisibility for CTA

        Spacer(modifier = Modifier.height(20.dp))

        // ── Today's Tip ───────────────────────────────────────
        AnimatedVisibility(
            visible = showTip,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
        ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(24.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.home_todays_tip),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = getTipForPhase(uiState.currentPhase),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }
        } // close AnimatedVisibility for Tip
    }
}

@Composable
private fun QuickStatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun phaseColor(phase: CyclePhase) = when (phase) {
    CyclePhase.MENSTRUAL -> MaterialTheme.colorScheme.tertiaryContainer
    CyclePhase.FOLLICULAR -> MaterialTheme.colorScheme.primaryContainer
    CyclePhase.OVULATION -> MaterialTheme.colorScheme.secondaryContainer
    CyclePhase.LUTEAL -> MaterialTheme.colorScheme.surfaceVariant
}

@Composable
private fun phaseDisplayName(phase: CyclePhase): String = when (phase) {
    CyclePhase.MENSTRUAL -> stringResource(R.string.phase_menstrual)
    CyclePhase.FOLLICULAR -> stringResource(R.string.phase_follicular)
    CyclePhase.OVULATION -> stringResource(R.string.phase_ovulation)
    CyclePhase.LUTEAL -> stringResource(R.string.phase_luteal)
}

@Composable
private fun fertilityDisplayName(status: FertilityStatus): String = when (status) {
    FertilityStatus.LOW -> stringResource(R.string.fertility_low)
    FertilityStatus.MEDIUM -> stringResource(R.string.fertility_medium)
    FertilityStatus.HIGH -> stringResource(R.string.fertility_high)
    FertilityStatus.PEAK -> stringResource(R.string.fertility_peak)
}

@Composable
private fun getTipForPhase(phase: CyclePhase): String = when (phase) {
    CyclePhase.MENSTRUAL -> stringResource(R.string.home_tip_menstrual)
    CyclePhase.FOLLICULAR -> stringResource(R.string.home_tip_follicular)
    CyclePhase.OVULATION -> stringResource(R.string.home_tip_ovulation)
    CyclePhase.LUTEAL -> stringResource(R.string.home_tip_luteal)
}
