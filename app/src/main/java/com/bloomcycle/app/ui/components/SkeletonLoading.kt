package com.bloomcycle.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// ══════════════════════════════════════════════════════════════════
// ── Shimmer Brush ───────────────────────────────────────────────
// ══════════════════════════════════════════════════════════════════

/**
 * Creates a soft, sweeping shimmer brush that glides left-to-right.
 * The colors blend with the surface so the effect feels organic,
 * not mechanical.
 */
@Composable
fun shimmerBrush(
    baseColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    highlightColor: Color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
    widthPx: Float = 1200f
): Brush {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = -widthPx,
        targetValue = widthPx * 2,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )

    return Brush.linearGradient(
        colors = listOf(baseColor, highlightColor, baseColor),
        start = Offset(translateAnim, 0f),
        end = Offset(translateAnim + widthPx, 0f)
    )
}

// ══════════════════════════════════════════════════════════════════
// ── Primitive Skeleton Shapes ───────────────────────────────────
// ══════════════════════════════════════════════════════════════════

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(shimmerBrush())
    )
}

@Composable
fun SkeletonCircle(
    size: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(shimmerBrush())
    )
}

@Composable
fun SkeletonText(
    width: Dp,
    height: Dp = 14.dp,
    modifier: Modifier = Modifier
) {
    SkeletonBox(
        modifier = modifier
            .width(width)
            .height(height),
        cornerRadius = 6.dp
    )
}

// ══════════════════════════════════════════════════════════════════
// ── Home Screen Skeleton ────────────────────────────────────────
// ══════════════════════════════════════════════════════════════════

@Composable
fun HomeScreenSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        // ── Greeting placeholder ──
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            SkeletonText(width = 180.dp, height = 22.dp)
            Spacer(modifier = Modifier.height(6.dp))
            SkeletonText(width = 140.dp, height = 14.dp)
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Cycle ring placeholder (frosted circle) ──
        Box(
            modifier = Modifier.size(240.dp),
            contentAlignment = Alignment.Center
        ) {
            SkeletonCircle(size = 240.dp)
            // Inner "content" lines
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                SkeletonCircle(size = 32.dp)
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonText(width = 90.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonText(width = 60.dp, height = 12.dp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ── Cycle day label ──
        SkeletonText(width = 110.dp, height = 12.dp)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Stat pills row ──
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            repeat(3) {
                SkeletonStatPill(modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── CTA button placeholder ──
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            cornerRadius = 16.dp
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Tip card placeholder ──
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            cornerRadius = 16.dp
        )
    }
}

@Composable
private fun SkeletonStatPill(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SkeletonCircle(size = 18.dp)
            Spacer(modifier = Modifier.height(6.dp))
            SkeletonText(width = 32.dp, height = 14.dp)
            Spacer(modifier = Modifier.height(3.dp))
            SkeletonText(width = 48.dp, height = 10.dp)
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// ── Insights Screen Skeleton ────────────────────────────────────
// ══════════════════════════════════════════════════════════════════

@Composable
fun InsightsScreenSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // ── Header ──
        SkeletonText(width = 120.dp, height = 26.dp)
        Spacer(modifier = Modifier.height(4.dp))
        SkeletonText(width = 200.dp, height = 14.dp)

        Spacer(modifier = Modifier.height(20.dp))

        // ── Prediction card placeholder ──
        SkeletonCard(height = 120.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Cycle stats card placeholder ──
        SkeletonCard(height = 100.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Chart card placeholder ──
        SkeletonCard(height = 160.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Symptom card placeholder ──
        SkeletonCard(height = 90.dp)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Education section ──
        SkeletonText(width = 100.dp, height = 16.dp)
        Spacer(modifier = Modifier.height(4.dp))
        SkeletonText(width = 180.dp, height = 12.dp)

        Spacer(modifier = Modifier.height(12.dp))

        // ── Education navigation cards ──
        repeat(3) {
            SkeletonEducationRow()
            if (it < 2) Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SkeletonEducationRow() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonCircle(size = 32.dp)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                SkeletonText(width = 120.dp, height = 14.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonText(width = 180.dp, height = 11.dp)
            }
            SkeletonCircle(size = 20.dp)
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// ── Reports Screen Skeleton ─────────────────────────────────────
// ══════════════════════════════════════════════════════════════════

@Composable
fun ReportsScreenSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        // ── Overview card ──
        SkeletonCard(height = 150.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Cycle history card ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SkeletonText(width = 120.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonText(width = 90.dp, height = 11.dp)

                Spacer(modifier = Modifier.height(16.dp))

                // History rows
                repeat(3) { index ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        SkeletonCircle(size = 32.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            SkeletonText(width = 140.dp, height = 13.dp)
                            Spacer(modifier = Modifier.height(3.dp))
                            SkeletonText(width = 100.dp, height = 10.dp)
                        }
                    }
                    if (index < 2) Spacer(modifier = Modifier.height(14.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Statistics card ──
        SkeletonCard(height = 110.dp)

        Spacer(modifier = Modifier.height(16.dp))

        // ── Export card ──
        SkeletonCard(height = 180.dp)
    }
}

// ══════════════════════════════════════════════════════════════════
// ── Daily Tracking Screen Skeleton ──────────────────────────────
// ══════════════════════════════════════════════════════════════════

@Composable
fun DailyTrackingScreenSkeleton(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // ── Tracking section cards ──
        repeat(5) { index ->
            SkeletonTrackingCard()
            if (index < 4) Spacer(modifier = Modifier.height(10.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Save button ──
        SkeletonBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            cornerRadius = 16.dp
        )
    }
}

@Composable
private fun SkeletonTrackingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header row (emoji + title)
            Row(verticalAlignment = Alignment.CenterVertically) {
                SkeletonCircle(size = 20.dp)
                Spacer(modifier = Modifier.width(8.dp))
                SkeletonText(width = 100.dp, height = 14.dp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            // Chip row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SkeletonBox(
                    modifier = Modifier
                        .width(72.dp)
                        .height(32.dp),
                    cornerRadius = 16.dp
                )
                SkeletonBox(
                    modifier = Modifier
                        .width(64.dp)
                        .height(32.dp),
                    cornerRadius = 16.dp
                )
                SkeletonBox(
                    modifier = Modifier
                        .width(80.dp)
                        .height(32.dp),
                    cornerRadius = 16.dp
                )
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// ── Shared Skeleton Card ────────────────────────────────────────
// ══════════════════════════════════════════════════════════════════

@Composable
private fun SkeletonCard(
    height: Dp,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(20.dp)
        ) {
            Column {
                SkeletonText(width = 130.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonText(width = 200.dp, height = 12.dp)
                Spacer(modifier = Modifier.height(16.dp))
                SkeletonBox(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .height(40.dp),
                    cornerRadius = 10.dp
                )
            }
        }
    }
}
