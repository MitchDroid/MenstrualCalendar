package com.bloomcycle.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bloomcycle.app.domain.model.CyclePhase
import com.bloomcycle.app.ui.theme.*

/**
 * Phase-aware parallax gradient header with an organic curved bottom.
 *
 * The gradient covers the greeting zone and fades to transparent through
 * a Bézier-curved clip, so the cycle ring below always sits on a clean
 * surface and never gets lost in the background color.
 *
 * Floating orbs breathe independently for an organic, living feel.
 * The entire header slides at 0.5× scroll speed for parallax depth.
 */
@Composable
fun ParallaxHeader(
    scrollState: ScrollState,
    phase: CyclePhase?,
    modifier: Modifier = Modifier,
    headerHeight: Dp = 220.dp
) {
    val density = LocalDensity.current

    // ── Phase-aware gradient colors ────────────────────────────
    val animSpec = tween<Color>(durationMillis = 600)

    val colors = resolveHeaderColors(phase)
    val topColor by animateColorAsState(colors.top, animSpec, label = "headerTop")
    val midColor by animateColorAsState(colors.mid, animSpec, label = "headerMid")
    val accentColor by animateColorAsState(colors.accent, animSpec, label = "headerAccent")

    // ── Infinite breathing animation for the floating orbs ─────
    val infiniteTransition = rememberInfiniteTransition(label = "headerBreathing")

    val breathe1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe1"
    )
    val breathe2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe2"
    )
    val breathe3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe3"
    )

    // ── Parallax offset: header moves at 0.5× scroll speed ────
    val scrollOffset = scrollState.value.toFloat()
    val parallaxOffset = scrollOffset * 0.5f
    val headerHeightPx = with(density) { headerHeight.toPx() }

    // ── Alpha fade-out as the header scrolls away ──────────────
    val headerAlpha = (1f - (scrollOffset / headerHeightPx).coerceIn(0f, 1f))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(headerHeight)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
        ) {
            val w = size.width
            val h = size.height

            // ── Organic curved clip shape ──────────────────────
            // Instead of a rectangular gradient that cuts off, we clip
            // everything to a shape with a deep Bézier curve at the bottom.
            val curvePath = Path().apply {
                moveTo(0f, 0f)
                lineTo(w, 0f)
                lineTo(w, h * 0.65f)
                // Deep organic curve — dips lower in the center
                cubicTo(
                    w * 0.78f, h * 0.78f + (breathe1 * 6.dp.toPx()),
                    w * 0.22f, h * 0.92f - (breathe2 * 8.dp.toPx()),
                    0f, h * 0.72f
                )
                close()
            }

            clipPath(curvePath) {
                // ── Main gradient (top → mid, fading to transparent) ──
                drawRect(
                    brush = Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.0f to topColor,
                            0.45f to midColor,
                            0.85f to midColor.copy(alpha = 0.15f),
                            1.0f to Color.Transparent
                        ),
                        startY = -parallaxOffset,
                        endY = h - parallaxOffset
                    ),
                    alpha = headerAlpha
                )

                // ── Floating bloom orbs (inside the clip) ─────────
                // Orb 1: large, top-right, slow drift
                val orb1Y = h * 0.2f - parallaxOffset * 0.3f + breathe1 * 10.dp.toPx()
                drawCircle(
                    color = accentColor.copy(alpha = 0.10f * headerAlpha),
                    radius = 70.dp.toPx() + breathe1 * 6.dp.toPx(),
                    center = Offset(w * 0.85f, orb1Y)
                )

                // Orb 2: medium, left side
                val orb2Y = h * 0.45f - parallaxOffset * 0.2f + breathe2 * 8.dp.toPx()
                drawCircle(
                    color = accentColor.copy(alpha = 0.07f * headerAlpha),
                    radius = 50.dp.toPx() + breathe2 * 5.dp.toPx(),
                    center = Offset(w * 0.12f, orb2Y)
                )

                // Orb 3: small, center-top
                val orb3Y = h * 0.12f - parallaxOffset * 0.4f + breathe3 * 6.dp.toPx()
                drawCircle(
                    color = accentColor.copy(alpha = 0.12f * headerAlpha),
                    radius = 30.dp.toPx() + breathe3 * 3.dp.toPx(),
                    center = Offset(w * 0.42f, orb3Y)
                )

                // ── Subtle top glow ───────────────────────────────
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.12f * headerAlpha),
                            Color.Transparent
                        ),
                        center = Offset(w * 0.35f, -parallaxOffset * 0.3f),
                        radius = w * 0.55f
                    )
                )
            }
        }
    }
}

// ── Header color palette per phase ─────────────────────────────

private data class HeaderColors(
    val top: Color,
    val mid: Color,
    val accent: Color
)

/**
 * Phase-specific gradient stops. The gradient fades to transparent at
 * the bottom (handled by the gradient stops above), so we only define
 * [top] and [mid] colors — no opaque bottom color that could clash
 * with the cycle ring.
 */
private fun resolveHeaderColors(phase: CyclePhase?): HeaderColors = when (phase) {
    CyclePhase.MENSTRUAL -> HeaderColors(
        top = MenstrualPrimary.copy(alpha = 0.35f),
        mid = MenstrualPrimaryContainer.copy(alpha = 0.55f),
        accent = MenstrualSecondary
    )
    CyclePhase.FOLLICULAR -> HeaderColors(
        top = FollicularPrimary.copy(alpha = 0.30f),
        mid = FollicularPrimaryContainer.copy(alpha = 0.50f),
        accent = FollicularSecondary
    )
    CyclePhase.OVULATION -> HeaderColors(
        top = OvulationPrimary.copy(alpha = 0.30f),
        mid = OvulationPrimaryContainer.copy(alpha = 0.55f),
        accent = OvulationSecondary
    )
    CyclePhase.LUTEAL -> HeaderColors(
        top = LutealPrimary.copy(alpha = 0.30f),
        mid = LutealPrimaryContainer.copy(alpha = 0.50f),
        accent = LutealSecondary
    )
    null -> HeaderColors(
        top = PinkPrimary.copy(alpha = 0.25f),
        mid = PinkPrimaryContainer.copy(alpha = 0.45f),
        accent = LavenderSecondary
    )
}
