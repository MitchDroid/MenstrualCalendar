package com.bloomcycle.app.ui.onboarding

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloomcycle.app.R
import com.bloomcycle.app.ui.theme.PinkPrimary
import com.bloomcycle.app.ui.theme.LavenderSecondary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WelcomeStep(
    modifier: Modifier = Modifier
) {
    // ── Staggered entrance ─────────────────────────────────
    var showFlower by remember { mutableStateOf(false) }
    var showTitle by remember { mutableStateOf(false) }
    var showDescription by remember { mutableStateOf(false) }
    var showFeatures by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        showFlower = true
        delay(300)
        showTitle = true
        delay(200)
        showDescription = true
        delay(200)
        showFeatures = true
    }

    // ── Bloom animation ────────────────────────────────────
    val bloomProgress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        delay(200)
        bloomProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = FastOutSlowInEasing)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "welcomeBreathing")
    val breathing by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ),
        label = "breathing"
    )

    val petalPrimary = PinkPrimary.copy(alpha = 0.6f)
    val petalSecondary = LavenderSecondary.copy(alpha = 0.4f)
    val pistilColor = Color(0xFFFFD54F)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ── Animated bloom flower ──────────────────────────
        AnimatedVisibility(
            visible = showFlower,
            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -it / 4 }
        ) {
            Canvas(modifier = Modifier.size(140.dp)) {
                val cx = size.width / 2
                val cy = size.height / 2
                val progress = bloomProgress.value
                val breathScale = 1f + breathing * 0.03f

                // Outer petals (6)
                val outerRadius = 52.dp.toPx() * progress * breathScale
                val outerWidth = 22.dp.toPx() * progress
                repeat(6) { i ->
                    val angle = (60f * i) + breathing * 3f
                    rotate(degrees = angle, pivot = Offset(cx, cy)) {
                        drawBloomPetal(
                            center = Offset(cx, cy),
                            length = outerRadius,
                            width = outerWidth,
                            color = petalPrimary
                        )
                    }
                }

                // Inner petals (6, rotated 30°)
                val innerRadius = 35.dp.toPx() * progress * breathScale
                val innerWidth = 16.dp.toPx() * progress
                repeat(6) { i ->
                    val angle = (60f * i + 30f) - breathing * 2f
                    rotate(degrees = angle, pivot = Offset(cx, cy)) {
                        drawBloomPetal(
                            center = Offset(cx, cy),
                            length = innerRadius,
                            width = innerWidth,
                            color = petalSecondary
                        )
                    }
                }

                // Center pistil with glow (guard against radius=0 crash)
                val pistilRadius = 12.dp.toPx() * progress * breathScale
                if (pistilRadius > 0.01f) {
                    val glowRadius = pistilRadius * 2f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                pistilColor.copy(alpha = 0.8f),
                                pistilColor.copy(alpha = 0.2f),
                                Color.Transparent
                            ),
                            center = Offset(cx, cy),
                            radius = glowRadius
                        ),
                        center = Offset(cx, cy),
                        radius = glowRadius
                    )
                    drawCircle(
                        color = pistilColor,
                        center = Offset(cx, cy),
                        radius = pistilRadius
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // ── Title ──────────────────────────────────────────
        AnimatedVisibility(
            visible = showTitle,
            enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 3 }
        ) {
            Text(
                text = stringResource(R.string.welcome_title),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── Description ────────────────────────────────────
        AnimatedVisibility(
            visible = showDescription,
            enter = fadeIn(tween(400))
        ) {
            Text(
                text = stringResource(R.string.welcome_description),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ── Feature cards ──────────────────────────────────
        AnimatedVisibility(
            visible = showFeatures,
            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 4 }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                FeatureCard(emoji = "🌸", text = stringResource(R.string.welcome_feature_track))
                FeatureCard(emoji = "🔒", text = stringResource(R.string.welcome_feature_privacy))
                FeatureCard(emoji = "💡", text = stringResource(R.string.welcome_feature_insights))
            }
        }
    }
}

@Composable
private fun FeatureCard(
    emoji: String,
    text: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.75f)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * Draws a single petal shape using Bézier curves.
 */
private fun DrawScope.drawBloomPetal(
    center: Offset,
    length: Float,
    width: Float,
    color: Color
) {
    val tipY = center.y - length // Points upward (will be rotated)
    val path = Path().apply {
        moveTo(center.x, center.y)
        cubicTo(
            center.x - width, center.y - length * 0.4f,
            center.x - width * 0.6f, tipY,
            center.x, tipY
        )
        cubicTo(
            center.x + width * 0.6f, tipY,
            center.x + width, center.y - length * 0.4f,
            center.x, center.y
        )
        close()
    }
    drawPath(path, color)
}
