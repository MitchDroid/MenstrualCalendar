package com.bloomcycle.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomcycle.app.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.ui.components.ParallaxHeader
import com.bloomcycle.app.ui.theme.LocalCyclePhase
import com.bloomcycle.app.domain.model.FertilityStatus
import com.bloomcycle.app.ui.theme.FertileGreen
import com.bloomcycle.app.ui.theme.OvulationYellow
import com.bloomcycle.app.ui.theme.PeriodRed
import com.bloomcycle.app.ui.theme.PredictedPurple
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun HomeScreen(
    onNavigateToTracking: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cyclePhase = LocalCyclePhase.current

    // ── Shared scroll state drives both parallax header and content ──
    val scrollState: ScrollState = rememberScrollState()

    // ── Staggered entrance animation ─────────────────────
    var showGreeting by remember { mutableStateOf(false) }
    var showRing by remember { mutableStateOf(false) }
    var showInfo by remember { mutableStateOf(false) }
    var showStats by remember { mutableStateOf(false) }
    var showCta by remember { mutableStateOf(false) }
    var showTip by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        showGreeting = true
        delay(100)
        showRing = true
        delay(150)
        showInfo = true
        delay(120)
        showStats = true
        delay(120)
        showCta = true
        delay(100)
        showTip = true
    }

    Box(modifier = modifier.fillMaxSize()) {
        // ── Parallax gradient background (behind content) ────
        ParallaxHeader(
            scrollState = scrollState,
            phase = cyclePhase,
            headerHeight = 220.dp
        )

        // ── Scrollable content (overlays the header) ─────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top padding — greeting sits inside the gradient zone
            Spacer(modifier = Modifier.height(48.dp))

            // ── Personalized Greeting (overlays the gradient) ───
            AnimatedVisibility(
                visible = showGreeting,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -it / 3 }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = timeOfDayGreeting(uiState.userName),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.home_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Animated Cycle Progress Ring (frosted card) ────────
            AnimatedVisibility(
                visible = showRing,
                enter = fadeIn(tween(600))
            ) {
                Surface(
                    modifier = Modifier.size(240.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                    shadowElevation = 4.dp,
                    tonalElevation = 2.dp
                ) {
                    CycleProgressRing(
                        cycleDay = uiState.cycleDay,
                        cycleLength = uiState.cycleLength,
                        phase = uiState.currentPhase,
                        daysUntilPeriod = uiState.daysUntilNextPeriod,
                        periodDuration = uiState.periodDuration,
                        modifier = Modifier
                            .padding(10.dp)
                            .size(220.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Cycle day / phase label under ring ──────────────
            AnimatedVisibility(
                visible = showInfo,
                enter = fadeIn(tween(400))
            ) {
                Text(
                    text = stringResource(R.string.home_cycle_progress, uiState.cycleDay, uiState.cycleLength),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Quick Stats (horizontal cards) ──────────────────
            AnimatedVisibility(
                visible = showStats,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatPill(
                        emoji = "🩸",
                        label = stringResource(R.string.home_next_period),
                        value = "${uiState.daysUntilNextPeriod}d",
                        accentColor = PeriodRed,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        emoji = "📅",
                        label = stringResource(R.string.home_cycle_length),
                        value = "${uiState.cycleLength}d",
                        accentColor = PredictedPurple,
                        modifier = Modifier.weight(1f)
                    )
                    StatPill(
                        emoji = phaseEmoji(uiState.currentPhase),
                        label = stringResource(R.string.home_fertility),
                        value = fertilityDisplayName(uiState.fertilityStatus),
                        accentColor = fertilityColor(uiState.fertilityStatus),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Log Today CTA ───────────────────────────────────
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
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Today's Tip ─────────────────────────────────────
            AnimatedVisibility(
                visible = showTip,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
            ) {
                val phaseAccent by animateColorAsState(
                    targetValue = phaseAccentColor(uiState.currentPhase),
                    animationSpec = tween(600),
                    label = "tipAccent"
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = phaseAccent.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = phaseAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.home_todays_tip),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = getTipForPhase(uiState.currentPhase),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Bottom spacing so content clears the bottom nav
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Animated Cycle Progress Ring ───────────────────────────────

@Composable
private fun CycleProgressRing(
    cycleDay: Int,
    cycleLength: Int,
    phase: CyclePhase,
    daysUntilPeriod: Int,
    periodDuration: Int,
    modifier: Modifier = Modifier
) {
    // Animated arc progress
    val targetProgress = cycleDay.toFloat() / cycleLength.toFloat()
    val animatedProgress = remember { Animatable(0f) }
    LaunchedEffect(targetProgress) {
        animatedProgress.snapTo(0f)
        animatedProgress.animateTo(
            targetValue = targetProgress,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    // Phase-dependent colors
    val startColor = phaseRingStartColor(phase)
    val endColor = phaseRingEndColor(phase)

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Ring canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 14.dp.toPx()
            val padding = strokeWidth / 2 + 4.dp.toPx()
            val arcSize = Size(size.width - padding * 2, size.height - padding * 2)
            val arcOffset = Offset(padding, padding)

            // Track (background ring)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc with gradient
            val sweepAngle = 360f * animatedProgress.value
            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(startColor, endColor, startColor),
                    center = Offset(size.width / 2, size.height / 2)
                ),
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Small circle at the end of the arc
            if (animatedProgress.value > 0.02f) {
                val endAngleRad = Math.toRadians((-90.0 + sweepAngle))
                val cx = size.width / 2 + (arcSize.width / 2) * kotlin.math.cos(endAngleRad).toFloat()
                val cy = size.height / 2 + (arcSize.height / 2) * kotlin.math.sin(endAngleRad).toFloat()
                drawCircle(
                    color = Color.White,
                    radius = strokeWidth / 2 + 2.dp.toPx(),
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = endColor,
                    radius = strokeWidth / 2 - 1.dp.toPx(),
                    center = Offset(cx, cy)
                )
            }
        }

        // Center content
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = phaseEmoji(phase),
                fontSize = 28.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            // Show countdown or period status
            if (cycleDay <= periodDuration) {
                Text(
                    text = stringResource(R.string.home_period_today),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onSurface,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = stringResource(R.string.home_days_until_period, daysUntilPeriod),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = onSurface,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = phaseDisplayName(phase),
                style = MaterialTheme.typography.labelMedium,
                color = onSurfaceVariant
            )
        }
    }
}

// ── Compact Stat Pill ─────────────────────────────────────────

@Composable
private fun StatPill(
    emoji: String,
    label: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = accentColor.copy(alpha = 0.08f)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}

// ── Helper functions ──────────────────────────────────────────

@Composable
private fun timeOfDayGreeting(userName: String): String {
    val hour = LocalTime.now().hour
    return if (userName.isNotBlank()) {
        when {
            hour < 12 -> stringResource(R.string.home_greeting_morning, userName)
            hour < 18 -> stringResource(R.string.home_greeting_afternoon, userName)
            else -> stringResource(R.string.home_greeting_evening, userName)
        }
    } else {
        when {
            hour < 12 -> stringResource(R.string.home_greeting_morning_anon)
            hour < 18 -> stringResource(R.string.home_greeting_afternoon_anon)
            else -> stringResource(R.string.home_greeting_evening_anon)
        }
    }
}

private fun phaseEmoji(phase: CyclePhase): String = when (phase) {
    CyclePhase.MENSTRUAL -> "🌺"
    CyclePhase.FOLLICULAR -> "🌱"
    CyclePhase.OVULATION -> "🌸"
    CyclePhase.LUTEAL -> "🍂"
}

@Composable
private fun phaseAccentColor(phase: CyclePhase): Color = when (phase) {
    CyclePhase.MENSTRUAL -> PeriodRed
    CyclePhase.FOLLICULAR -> FertileGreen
    CyclePhase.OVULATION -> OvulationYellow
    CyclePhase.LUTEAL -> PredictedPurple
}

private fun phaseRingStartColor(phase: CyclePhase): Color = when (phase) {
    CyclePhase.MENSTRUAL -> PeriodRed.copy(alpha = 0.5f)
    CyclePhase.FOLLICULAR -> FertileGreen.copy(alpha = 0.5f)
    CyclePhase.OVULATION -> OvulationYellow.copy(alpha = 0.6f)
    CyclePhase.LUTEAL -> PredictedPurple.copy(alpha = 0.5f)
}

private fun phaseRingEndColor(phase: CyclePhase): Color = when (phase) {
    CyclePhase.MENSTRUAL -> PeriodRed
    CyclePhase.FOLLICULAR -> FertileGreen
    CyclePhase.OVULATION -> OvulationYellow
    CyclePhase.LUTEAL -> PredictedPurple
}

@Composable
private fun fertilityColor(status: FertilityStatus): Color = when (status) {
    FertilityStatus.LOW -> MaterialTheme.colorScheme.outline
    FertilityStatus.MEDIUM -> OvulationYellow
    FertilityStatus.HIGH -> FertileGreen
    FertilityStatus.PEAK -> FertileGreen
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
