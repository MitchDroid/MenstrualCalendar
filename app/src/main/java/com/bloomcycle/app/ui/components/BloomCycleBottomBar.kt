package com.bloomcycle.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Insights
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bloomcycle.app.R
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

/**
 * Bottom navigation item definition.
 */
private data class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

/**
 * The 4 tab items. The FAB sits between items 1 and 2 (Calendar & Insights).
 */
private val bottomNavItems = listOf(
    BottomNavItem("home", R.string.nav_home, Icons.Filled.Home, Icons.Outlined.Home),
    BottomNavItem("calendar", R.string.nav_calendar, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    // ← FAB sits here visually
    BottomNavItem("insights", R.string.nav_insights, Icons.Filled.Insights, Icons.Outlined.Insights),
    BottomNavItem("settings", R.string.nav_settings, Icons.Filled.Settings, Icons.Outlined.Settings)
)

/**
 * Custom bottom navigation with:
 * - Animated sliding indicator pill behind the selected tab
 * - Floating center FAB for quick daily log
 * - Phase-aware accent colors from MaterialTheme
 */
@Composable
fun BloomCycleBottomBar(
    navController: NavHostController,
    onQuickLogClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Find selected index (0–3)
    val selectedIndex = bottomNavItems.indexOfFirst { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }.coerceAtLeast(0)

    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    Box(modifier = modifier.fillMaxWidth()) {
        // ── Bar surface ──────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = surfaceColor,
            shadowElevation = 8.dp,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEachIndexed { index, item ->
                    val selected = index == selectedIndex

                    // Insert spacer for FAB gap between Calendar (1) and Insights (2)
                    if (index == 2) {
                        Spacer(modifier = Modifier.weight(0.8f))
                    }

                    NavTab(
                        item = item,
                        selected = selected,
                        primaryColor = primaryColor,
                        onSurfaceVariant = onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }

        // ── Center FAB ───────────────────────────────────────
        FloatingActionButton(
            onClick = { onQuickLogClick?.invoke() },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(52.dp),
            shape = CircleShape,
            containerColor = primaryColor,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 10.dp
            )
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(R.string.home_log_today),
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

// ── Individual Tab ───────────────────────────────────────────────

@Composable
private fun NavTab(
    item: BottomNavItem,
    selected: Boolean,
    primaryColor: Color,
    onSurfaceVariant: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (selected) primaryColor else onSurfaceVariant,
        animationSpec = tween(300),
        label = "iconColor"
    )

    val labelColor by animateColorAsState(
        targetValue = if (selected) primaryColor else onSurfaceVariant.copy(alpha = 0.7f),
        animationSpec = tween(300),
        label = "labelColor"
    )

    val iconScale by animateFloatAsState(
        targetValue = if (selected) 1.15f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "iconScale"
    )

    // Animated background pill
    val pillAlpha by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(300),
        label = "pillAlpha"
    )

    val pillColor = primaryColor.copy(alpha = 0.1f * pillAlpha)

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(pillColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = stringResource(item.titleResId),
            tint = iconColor,
            modifier = Modifier
                .size(24.dp)
                .scale(iconScale)
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = stringResource(item.titleResId),
            color = labelColor,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )

        // Animated dot indicator under selected tab
        Spacer(modifier = Modifier.height(2.dp))
        Box(modifier = Modifier.size(width = 16.dp, height = 3.dp)) {
            val dotWidth by animateDpAsState(
                targetValue = if (selected) 16.dp else 0.dp,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                label = "dotWidth"
            )

            if (dotWidth > 0.dp) {
                Canvas(modifier = Modifier
                    .size(width = dotWidth, height = 3.dp)
                    .align(Alignment.Center)
                ) {
                    drawRoundRect(
                        color = primaryColor,
                        cornerRadius = CornerRadius(4f, 4f),
                        size = Size(size.width, size.height)
                    )
                }
            }
        }
    }
}
