package com.bloomcycle.app.ui.dedication

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun DedicationScreen(
    onNavigateBack: () -> Unit = {}
) {
    var showHeart by remember { mutableStateOf(false) }
    var showName by remember { mutableStateOf(false) }
    var showDedication by remember { mutableStateOf(false) }
    var showSignature by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        showHeart = true
        delay(800)
        showName = true
        delay(600)
        showDedication = true
        delay(600)
        showSignature = true
    }

    val infiniteTransition = rememberInfiniteTransition(label = "heartbeat")
    val heartScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "heartbeat"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0F5),  // Lavender blush
                        Color(0xFFFCE4EC),  // Pink 50
                        Color(0xFFF8BBD0),  // Pink 100
                        Color(0xFFFFF0F5)   // Lavender blush
                    )
                )
            )
    ) {
        // Floating petals background
        FloatingPetals()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Animated Heart ───────────────────────────
            AnimatedVisibility(
                visible = showHeart,
                enter = fadeIn(tween(1000)) + slideInVertically(
                    initialOffsetY = { -80 },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            ) {
                Canvas(
                    modifier = Modifier.size((120 * heartScale).dp)
                ) {
                    drawHeart(
                        color = Color(0xFFE91E63),
                        glowColor = Color(0x40E91E63)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── "For Karen" ──────────────────────────────
            AnimatedVisibility(
                visible = showName,
                enter = fadeIn(tween(1200))
            ) {
                Text(
                    text = "Para Karen Villarreal",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Light,
                        fontStyle = FontStyle.Italic,
                        letterSpacing = 2.sp
                    ),
                    color = Color(0xFFC2185B),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Dedication message ───────────────────────
            AnimatedVisibility(
                visible = showDedication,
                enter = fadeIn(tween(1500))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "This app was built with love,\ninspired by you and for you.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Serif,
                            lineHeight = 28.sp
                        ),
                        color = Color(0xFF880E4F),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Every line of code carries\na piece of my heart.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            lineHeight = 28.sp
                        ),
                        color = Color(0xFF880E4F).copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── Signature ────────────────────────────────
            AnimatedVisibility(
                visible = showSignature,
                enter = fadeIn(tween(1500))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "— With all my love —",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic
                        ),
                        color = Color(0xFFAD1457)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Your husband Miller",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = Color(0xFFC2185B)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "\uD83C\uDF38",  // 🌸
                        fontSize = 28.sp
                    )
                }
            }
        }

        // ── Back button (subtle) ─────────────────────
        TextButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(8.dp)
        ) {
            Text(
                text = "\u2190",  // ←
                fontSize = 22.sp,
                color = Color(0xFFAD1457).copy(alpha = 0.5f)
            )
        }
    }
}

// ── Floating petals background ──────────────────────────────────

@Composable
private fun FloatingPetals() {
    val infiniteTransition = rememberInfiniteTransition(label = "petals")
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "petalTime"
    )

    Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
        val petals = listOf(
            Triple(0.15f, 0.2f, 12f),
            Triple(0.85f, 0.15f, 8f),
            Triple(0.1f, 0.7f, 10f),
            Triple(0.9f, 0.65f, 14f),
            Triple(0.5f, 0.1f, 9f),
            Triple(0.3f, 0.85f, 11f),
            Triple(0.7f, 0.9f, 7f),
            Triple(0.6f, 0.45f, 13f)
        )

        petals.forEachIndexed { index, (xFrac, yFrac, baseSize) ->
            val phase = time + index * 45f
            val x = size.width * xFrac + sin(Math.toRadians(phase.toDouble())).toFloat() * 20f
            val y = size.height * yFrac + cos(Math.toRadians(phase.toDouble() * 0.7)).toFloat() * 15f
            val petalSize = baseSize + sin(Math.toRadians(phase.toDouble() * 1.3)).toFloat() * 3f

            rotate(degrees = phase * 0.5f, pivot = Offset(x, y)) {
                drawCircle(
                    color = Color(0xFFF8BBD0),
                    radius = petalSize,
                    center = Offset(x, y)
                )
            }
        }
    }
}

// ── Heart drawing ───────────────────────────────────────────────

private fun DrawScope.drawHeart(
    color: Color,
    glowColor: Color
) {
    val width = size.width
    val height = size.height
    val cx = width / 2f
    val cy = height / 2f

    // Soft glow behind the heart
    val glowPath = createHeartPath(cx, cy, width * 0.46f)
    drawPath(glowPath, glowColor)

    // Main heart
    val heartPath = createHeartPath(cx, cy, width * 0.42f)
    drawPath(
        heartPath,
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFFF4081),
                color,
                Color(0xFFC2185B)
            )
        )
    )

    // Highlight
    val highlightPath = createHeartPath(cx - width * 0.04f, cy - height * 0.02f, width * 0.18f)
    drawPath(highlightPath, Color.White.copy(alpha = 0.2f))
}

private fun createHeartPath(cx: Float, cy: Float, scale: Float): Path {
    return Path().apply {
        val points = 200
        for (i in 0..points) {
            val t = (i.toFloat() / points) * 2 * Math.PI
            // Heart parametric equations
            val x = cx + scale * (16 * Math.pow(sin(t), 3.0)).toFloat() / 16f
            val y = cy - scale * (
                    13 * cos(t) - 5 * cos(2 * t) - 2 * cos(3 * t) - cos(4 * t)
                    ).toFloat() / 16f

            if (i == 0) moveTo(x, y) else lineTo(x, y)
        }
        close()
    }
}
