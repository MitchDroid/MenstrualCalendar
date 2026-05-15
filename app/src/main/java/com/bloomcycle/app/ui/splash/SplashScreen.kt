package com.bloomcycle.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.bloomcycle.app.ui.theme.BloomCycleTheme
import com.bloomcycle.app.ui.theme.Coral
import com.bloomcycle.app.ui.theme.Lavender
import com.bloomcycle.app.ui.theme.SoftPink
import kotlin.math.PI
import kotlin.math.sin

/**
 * Animated splash screen with a blooming flower animation.
 * Petals unfold from the center outward, the app name fades in,
 * and the whole composition gently pulses while loading completes.
 */
@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    // ── Petal bloom animation (0 → 1 over 1.2s) ─────────────
    val bloomProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        bloomProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    // ── Title fade-in (delayed, 0 → 1 over 600ms) ───────────
    val titleAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(700)
        titleAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
    }

    // ── Gentle breathing pulse (continuous) ──────────────────
    val infiniteTransition = rememberInfiniteTransition(label = "splashPulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // ── Slow rotation for organic feel ───────────────────────
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    val backgroundColor = MaterialTheme.colorScheme.background
    val onBackground = MaterialTheme.colorScheme.onBackground

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // ── Bloom flower canvas ──────────────────────────
            Canvas(
                modifier = Modifier.size(180.dp)
            ) {
                val progress = bloomProgress.value
                val center = Offset(size.width / 2f, size.height / 2f)

                // Draw with gentle pulse and rotation
                scale(pulseScale, pivot = center) {
                    rotate(rotationAngle, pivot = center) {
                        drawBloomFlower(
                            center = center,
                            progress = progress,
                            maxRadius = size.minDimension / 2.4f
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── App name ─────────────────────────────────────
            Text(
                text = "BloomCycle",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = onBackground.copy(alpha = titleAlpha.value),
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "🌸",
                fontSize = 16.sp,
                color = onBackground.copy(alpha = titleAlpha.value * 0.7f)
            )
        }
    }
}

// ── Previews ─────────────────────────────────────────────────────

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Splash – Light")
@Composable
private fun SplashScreenPreview() {
    BloomCycleTheme {
        SplashScreen()
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640, name = "Splash – Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SplashScreenDarkPreview() {
    BloomCycleTheme(darkTheme = true) {
        SplashScreen()
    }
}

// ── Draw the bloom flower ────────────────────────────────────────

private fun DrawScope.drawBloomFlower(
    center: Offset,
    progress: Float,
    maxRadius: Float
) {
    val petalCount = 6
    val petalColors = listOf(
        SoftPink,
        Lavender.copy(alpha = 0.7f),
        Coral.copy(alpha = 0.6f),
        SoftPink.copy(alpha = 0.8f),
        Lavender.copy(alpha = 0.5f),
        Coral.copy(alpha = 0.7f)
    )

    // Each petal blooms with a staggered delay
    for (i in 0 until petalCount) {
        val angleOffset = (360f / petalCount) * i
        val petalDelay = i * 0.08f // Stagger each petal
        val petalProgress = ((progress - petalDelay) / (1f - petalDelay)).coerceIn(0f, 1f)

        if (petalProgress > 0f) {
            drawPetal(
                center = center,
                angleDeg = angleOffset,
                progress = petalProgress,
                maxRadius = maxRadius,
                color = petalColors[i % petalColors.size]
            )
        }
    }

    // Inner petals (smaller, rotated 30°)
    for (i in 0 until petalCount) {
        val angleOffset = (360f / petalCount) * i + 30f
        val innerDelay = 0.3f + i * 0.06f
        val innerProgress = ((progress - innerDelay) / (1f - innerDelay)).coerceIn(0f, 1f)

        if (innerProgress > 0f) {
            drawPetal(
                center = center,
                angleDeg = angleOffset,
                progress = innerProgress,
                maxRadius = maxRadius * 0.6f,
                color = petalColors[(i + 2) % petalColors.size].copy(alpha = 0.5f)
            )
        }
    }

    // Center circle (pistil) — blooms last
    val centerProgress = ((progress - 0.5f) / 0.5f).coerceIn(0f, 1f)
    if (centerProgress > 0f) {
        val centerRadius = maxRadius * 0.15f * centerProgress

        // Warm glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF3E0).copy(alpha = 0.8f * centerProgress),
                    Color(0xFFFFE0B2).copy(alpha = 0.4f * centerProgress),
                    Color.Transparent
                ),
                center = center,
                radius = centerRadius * 2.5f
            ),
            center = center,
            radius = centerRadius * 2.5f
        )

        // Solid center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFF8E1),
                    Color(0xFFFFE0B2)
                ),
                center = center,
                radius = centerRadius
            ),
            center = center,
            radius = centerRadius
        )
    }
}

private fun DrawScope.drawPetal(
    center: Offset,
    angleDeg: Float,
    progress: Float,
    maxRadius: Float,
    color: Color
) {
    val angleRad = angleDeg * (PI / 180f).toFloat()
    val petalLength = maxRadius * progress
    val petalWidth = maxRadius * 0.35f * progress

    // Smooth ease-out curve for petal shape
    val easedProgress = sin((progress * PI / 2f).toFloat())

    rotate(angleDeg, pivot = center) {
        val path = Path().apply {
            moveTo(center.x, center.y)

            // Right curve of petal
            cubicTo(
                center.x + petalWidth * 0.5f * easedProgress,
                center.y - petalLength * 0.3f,
                center.x + petalWidth * easedProgress,
                center.y - petalLength * 0.6f,
                center.x,
                center.y - petalLength
            )

            // Left curve of petal (mirror)
            cubicTo(
                center.x - petalWidth * easedProgress,
                center.y - petalLength * 0.6f,
                center.x - petalWidth * 0.5f * easedProgress,
                center.y - petalLength * 0.3f,
                center.x,
                center.y
            )

            close()
        }

        drawPath(
            path = path,
            brush = Brush.verticalGradient(
                colors = listOf(
                    color.copy(alpha = color.alpha * 0.3f),
                    color,
                    color.copy(alpha = color.alpha * 0.7f)
                ),
                startY = center.y,
                endY = center.y - petalLength
            )
        )
    }
}
