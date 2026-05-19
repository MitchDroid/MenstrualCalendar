package com.bloomcycle.app.ui.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.bloomcycle.app.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

// ══════════════════════════════════════════════════════════════════
// ── Step Dot Indicator ──────────────────────────────────────────
// ══════════════════════════════════════════════════════════════════

/**
 * Animated step indicator with expanding dot for the active step.
 * Active dot stretches into a pill, inactive dots are small circles.
 */
@Composable
fun StepDotIndicator(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.surfaceVariant

    Row(
        modifier = modifier.testTag("step_dot_indicator"),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(totalSteps) { index ->
            val isActive = index == currentStep
            val isPast = index < currentStep

            val targetWidth by animateDpAsState(
                targetValue = if (isActive) 24.dp else 8.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "dotWidth$index"
            )

            val dotColor by animateColorAsState(
                targetValue = when {
                    isActive -> primaryColor
                    isPast -> primaryColor.copy(alpha = 0.4f)
                    else -> inactiveColor
                },
                animationSpec = tween(300),
                label = "dotColor$index"
            )

            val dotState = when {
                isActive -> "active"
                isPast -> "completed"
                else -> "upcoming"
            }

            Box(
                modifier = Modifier
                    .width(targetWidth)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(dotColor)
                    .semantics {
                        contentDescription = "Step ${index + 1} $dotState"
                    }
            )
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// ── Animated Onboarding Background ──────────────────────────────
// ══════════════════════════════════════════════════════════════════

/**
 * Per-step gradient background with floating organic shapes.
 * Each step gets a unique color palette and decorative elements
 * (petals, circles, waves) that breathe and drift subtly.
 */
@Composable
fun OnboardingBackground(
    step: OnboardingStep,
    modifier: Modifier = Modifier
) {
    val colors = stepBackgroundColors(step)
    val topColor by animateColorAsState(colors.top, tween(500), label = "bgTop")
    val bottomColor by animateColorAsState(colors.bottom, tween(500), label = "bgBottom")
    val accentColor by animateColorAsState(colors.accent, tween(500), label = "bgAccent")
    val orbColor by animateColorAsState(colors.orb, tween(500), label = "bgOrb")

    val infiniteTransition = rememberInfiniteTransition(label = "onboardingBg")

    val breathe1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe1"
    )
    val breathe2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe2"
    )
    val breathe3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3800, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe3"
    )

    // Slow rotation for decorative petals
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(30000, easing = FastOutSlowInEasing), RepeatMode.Restart),
        label = "rotation"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // ── Soft gradient fill ─────────────────────────────────
        drawRect(
            brush = Brush.verticalGradient(
                colorStops = arrayOf(
                    0.0f to topColor,
                    0.4f to bottomColor.copy(alpha = 0.3f),
                    1.0f to Color.Transparent
                )
            )
        )

        // ── Floating orb — top right ───────────────────────────
        drawCircle(
            color = orbColor.copy(alpha = 0.08f),
            radius = 90.dp.toPx() + breathe1 * 10.dp.toPx(),
            center = Offset(w * 0.88f, h * 0.08f + breathe1 * 8.dp.toPx())
        )

        // ── Floating orb — bottom left ─────────────────────────
        drawCircle(
            color = accentColor.copy(alpha = 0.06f),
            radius = 70.dp.toPx() + breathe2 * 8.dp.toPx(),
            center = Offset(w * 0.1f, h * 0.85f + breathe2 * 6.dp.toPx())
        )

        // ── Small accent orb — center ──────────────────────────
        drawCircle(
            color = orbColor.copy(alpha = 0.10f),
            radius = 35.dp.toPx() + breathe3 * 4.dp.toPx(),
            center = Offset(w * 0.35f, h * 0.18f + breathe3 * 5.dp.toPx())
        )

        // ── Decorative petals — top left, slowly rotating ──────
        rotate(degrees = rotation * 0.3f, pivot = Offset(w * 0.15f, h * 0.12f)) {
            drawPetalCluster(
                center = Offset(w * 0.15f, h * 0.12f),
                petalCount = 5,
                petalLength = 40.dp.toPx() + breathe1 * 5.dp.toPx(),
                petalWidth = 16.dp.toPx(),
                color = accentColor.copy(alpha = 0.07f)
            )
        }

        // ── Decorative petals — bottom right ───────────────────
        rotate(degrees = -rotation * 0.2f, pivot = Offset(w * 0.85f, h * 0.75f)) {
            drawPetalCluster(
                center = Offset(w * 0.85f, h * 0.75f),
                petalCount = 4,
                petalLength = 30.dp.toPx() + breathe2 * 4.dp.toPx(),
                petalWidth = 12.dp.toPx(),
                color = orbColor.copy(alpha = 0.06f)
            )
        }

        // ── Subtle wavy accent line across the middle ──────────
        val wavePath = Path().apply {
            moveTo(-20f, h * 0.45f)
            cubicTo(
                w * 0.25f, h * 0.42f + breathe1 * 8.dp.toPx(),
                w * 0.55f, h * 0.48f - breathe2 * 6.dp.toPx(),
                w * 0.80f, h * 0.44f + breathe3 * 4.dp.toPx()
            )
            cubicTo(
                w * 0.95f, h * 0.43f,
                w * 1.05f, h * 0.46f,
                w + 20f, h * 0.45f
            )
        }
        drawPath(
            path = wavePath,
            color = accentColor.copy(alpha = 0.04f),
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 40.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
}

/**
 * Draws a ring of petals using Bézier curves.
 */
private fun DrawScope.drawPetalCluster(
    center: Offset,
    petalCount: Int,
    petalLength: Float,
    petalWidth: Float,
    color: Color
) {
    val angleStep = 360f / petalCount
    repeat(petalCount) { i ->
        val angle = Math.toRadians((angleStep * i).toDouble())
        val tipX = center.x + petalLength * cos(angle).toFloat()
        val tipY = center.y + petalLength * sin(angle).toFloat()

        // Control points perpendicular to the petal direction
        val perpAngle = angle + Math.PI / 2
        val cp1X = center.x + petalLength * 0.4f * cos(angle).toFloat() + petalWidth * cos(perpAngle).toFloat()
        val cp1Y = center.y + petalLength * 0.4f * sin(angle).toFloat() + petalWidth * sin(perpAngle).toFloat()
        val cp2X = center.x + petalLength * 0.4f * cos(angle).toFloat() - petalWidth * cos(perpAngle).toFloat()
        val cp2Y = center.y + petalLength * 0.4f * sin(angle).toFloat() - petalWidth * sin(perpAngle).toFloat()

        val petalPath = Path().apply {
            moveTo(center.x, center.y)
            cubicTo(cp1X, cp1Y, tipX, tipY, tipX, tipY)
            cubicTo(tipX, tipY, cp2X, cp2Y, center.x, center.y)
            close()
        }
        drawPath(petalPath, color)
    }
}

// ── Step-specific background color palettes ─────────────────────

private data class StepBgColors(
    val top: Color,
    val bottom: Color,
    val accent: Color,
    val orb: Color
)

private fun stepBackgroundColors(step: OnboardingStep): StepBgColors = when (step) {
    OnboardingStep.WELCOME -> StepBgColors(
        top = PinkPrimary.copy(alpha = 0.12f),
        bottom = PinkPrimaryContainer.copy(alpha = 0.20f),
        accent = LavenderSecondary,
        orb = PinkPrimary
    )
    OnboardingStep.YOUR_NAME -> StepBgColors(
        top = OvulationPrimary.copy(alpha = 0.10f),
        bottom = OvulationPrimaryContainer.copy(alpha = 0.18f),
        accent = OvulationSecondary,
        orb = OvulationPrimary
    )
    OnboardingStep.LAST_PERIOD -> StepBgColors(
        top = MenstrualPrimary.copy(alpha = 0.10f),
        bottom = MenstrualPrimaryContainer.copy(alpha = 0.18f),
        accent = MenstrualSecondary,
        orb = MenstrualPrimary
    )
    OnboardingStep.CYCLE_LENGTH -> StepBgColors(
        top = FollicularPrimary.copy(alpha = 0.10f),
        bottom = FollicularPrimaryContainer.copy(alpha = 0.18f),
        accent = FollicularSecondary,
        orb = FollicularPrimary
    )
    OnboardingStep.PERIOD_DURATION -> StepBgColors(
        top = CoralTertiary.copy(alpha = 0.10f),
        bottom = CoralTertiaryContainer.copy(alpha = 0.18f),
        accent = MenstrualSecondary,
        orb = CoralTertiary
    )
    OnboardingStep.USER_GOAL -> StepBgColors(
        top = LutealPrimary.copy(alpha = 0.10f),
        bottom = LutealPrimaryContainer.copy(alpha = 0.18f),
        accent = LutealSecondary,
        orb = LutealPrimary
    )
}
