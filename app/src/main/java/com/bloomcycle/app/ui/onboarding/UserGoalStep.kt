package com.bloomcycle.app.ui.onboarding

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bloomcycle.app.domain.model.UserGoal

@Composable
fun UserGoalStep(
    selectedGoal: UserGoal?,
    onGoalSelected: (UserGoal) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "What's your\nmain goal?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "This helps us personalize your experience.\nYou can change this anytime in settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Goal selection cards
        GoalCard(
            goal = UserGoal.TRACK_CYCLE,
            icon = Icons.Filled.CalendarMonth,
            title = "Track My Cycle",
            description = "Keep track of periods and understand patterns",
            isSelected = selectedGoal == UserGoal.TRACK_CYCLE,
            onClick = { onGoalSelected(UserGoal.TRACK_CYCLE) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        GoalCard(
            goal = UserGoal.TRYING_TO_CONCEIVE,
            icon = Icons.Filled.ChildCare,
            title = "Trying to Conceive",
            description = "Track fertile days and ovulation window",
            isSelected = selectedGoal == UserGoal.TRYING_TO_CONCEIVE,
            onClick = { onGoalSelected(UserGoal.TRYING_TO_CONCEIVE) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        GoalCard(
            goal = UserGoal.AVOID_PREGNANCY,
            icon = Icons.Filled.Shield,
            title = "Avoid Pregnancy",
            description = "Know your fertile window for planning",
            isSelected = selectedGoal == UserGoal.AVOID_PREGNANCY,
            onClick = { onGoalSelected(UserGoal.AVOID_PREGNANCY) }
        )

        Spacer(modifier = Modifier.height(12.dp))

        GoalCard(
            goal = UserGoal.MONITOR_HEALTH,
            icon = Icons.Filled.MonitorHeart,
            title = "Monitor Health",
            description = "Track symptoms and overall wellness",
            isSelected = selectedGoal == UserGoal.MONITOR_HEALTH,
            onClick = { onGoalSelected(UserGoal.MONITOR_HEALTH) }
        )
    }
}

@Composable
private fun GoalCard(
    goal: UserGoal,
    icon: ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val border = if (isSelected) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
    }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = border,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = if (isSelected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.7f)
                )
            }
        }
    }
}
