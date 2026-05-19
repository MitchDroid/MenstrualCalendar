package com.bloomcycle.app.ui.tracking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomcycle.app.R
import com.bloomcycle.app.ui.components.DailyTrackingScreenSkeleton
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bloomcycle.app.domain.model.CervicalMucus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.SexualActivity
import com.bloomcycle.app.domain.model.Symptom
import com.bloomcycle.app.ui.util.displayNameRes
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DailyTrackingScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DailyTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    // ── Staggered entrance animations ────────────────────
    var showSections by remember { mutableStateOf(BooleanArray(8) { false }) }
    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            for (i in showSections.indices) {
                delay(70L)
                showSections = showSections.copyOf().also { it[i] = true }
            }
        }
    }

    // ── Section completeness for progress ring ───────────
    val sectionsCompleted = listOf(
        uiState.flowIntensity != null,
        uiState.mood != null,
        uiState.symptoms.isNotEmpty(),
        uiState.cervicalMucus != null,
        uiState.sexualActivity != null,
        uiState.temperature.isNotBlank() || uiState.weight.isNotBlank(),
        uiState.notes.isNotBlank()
    )
    val completedCount = sectionsCompleted.count { it }
    val totalSections = sectionsCompleted.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.tracking_title),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = uiState.formattedDate,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        // ── Completeness ring in the top bar ─────
                        if (!uiState.isLoading) {
                            LogCompletenessRing(
                                completed = completedCount,
                                total = totalSections,
                                modifier = Modifier.size(38.dp)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    if (uiState.existingLogId != 0L) {
                        IconButton(onClick = { viewModel.deleteLog() }) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.tracking_delete_log),
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading) {
            DailyTrackingScreenSkeleton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ── Flow Intensity ─────────────────────────
                AnimatedSection(visible = showSections.getOrElse(0) { false }) {
                    TrackingCard(
                        title = stringResource(R.string.tracking_section_flow),
                        emoji = "🩸",
                        isFilled = uiState.flowIntensity != null,
                        accentColor = MaterialTheme.colorScheme.primary
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FlowIntensity.entries.forEach { intensity ->
                                TrackingChip(
                                    label = "${flowEmoji(intensity)} ${stringResource(intensity.displayNameRes())}",
                                    selected = uiState.flowIntensity == intensity,
                                    selectedColor = MaterialTheme.colorScheme.primaryContainer,
                                    onClick = { viewModel.setFlowIntensity(intensity) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Mood ───────────────────────────────────
                AnimatedSection(visible = showSections.getOrElse(1) { false }) {
                    TrackingCard(
                        title = stringResource(R.string.tracking_section_mood),
                        emoji = "😊",
                        isFilled = uiState.mood != null,
                        accentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Mood.entries.forEach { mood ->
                                TrackingChip(
                                    label = "${moodEmoji(mood)} ${stringResource(mood.displayNameRes())}",
                                    selected = uiState.mood == mood,
                                    selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                                    onClick = { viewModel.setMood(mood) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Symptoms (multi-select) ────────────────
                AnimatedSection(visible = showSections.getOrElse(2) { false }) {
                    TrackingCard(
                        title = stringResource(R.string.tracking_section_symptoms),
                        emoji = "🩹",
                        isFilled = uiState.symptoms.isNotEmpty(),
                        accentColor = MaterialTheme.colorScheme.tertiary,
                        badge = if (uiState.symptoms.isNotEmpty()) "${uiState.symptoms.size}" else null
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Symptom.entries.forEach { symptom ->
                                TrackingChip(
                                    label = "${symptomEmoji(symptom)} ${stringResource(symptom.displayNameRes())}",
                                    selected = symptom in uiState.symptoms,
                                    selectedColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    onClick = { viewModel.toggleSymptom(symptom) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Cervical Mucus ─────────────────────────
                AnimatedSection(visible = showSections.getOrElse(3) { false }) {
                    TrackingCard(
                        title = stringResource(R.string.tracking_section_cervical_mucus),
                        emoji = "💧",
                        isFilled = uiState.cervicalMucus != null,
                        accentColor = MaterialTheme.colorScheme.primary
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            CervicalMucus.entries.forEach { mucus ->
                                TrackingChip(
                                    label = stringResource(mucus.displayNameRes()),
                                    selected = uiState.cervicalMucus == mucus,
                                    selectedColor = MaterialTheme.colorScheme.primaryContainer,
                                    onClick = { viewModel.setCervicalMucus(mucus) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Sexual Activity ────────────────────────
                AnimatedSection(visible = showSections.getOrElse(4) { false }) {
                    TrackingCard(
                        title = stringResource(R.string.tracking_section_intimacy),
                        emoji = "💕",
                        isFilled = uiState.sexualActivity != null,
                        accentColor = MaterialTheme.colorScheme.secondary
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            SexualActivity.entries.forEach { activity ->
                                TrackingChip(
                                    label = stringResource(activity.displayNameRes()),
                                    selected = uiState.sexualActivity == activity,
                                    selectedColor = MaterialTheme.colorScheme.secondaryContainer,
                                    onClick = { viewModel.setSexualActivity(activity) }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Temperature & Weight ───────────────────
                AnimatedSection(visible = showSections.getOrElse(5) { false }) {
                    TrackingCard(
                        title = stringResource(R.string.tracking_section_vitals),
                        emoji = "🌡️",
                        isFilled = uiState.temperature.isNotBlank() || uiState.weight.isNotBlank(),
                        accentColor = MaterialTheme.colorScheme.tertiary
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedTextField(
                                value = uiState.temperature,
                                onValueChange = { viewModel.setTemperature(it) },
                                label = { Text(stringResource(R.string.tracking_temp_label)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                            )
                            OutlinedTextField(
                                value = uiState.weight,
                                onValueChange = { viewModel.setWeight(it) },
                                label = { Text(stringResource(R.string.tracking_weight_label)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // ── Notes ──────────────────────────────────
                AnimatedSection(visible = showSections.getOrElse(6) { false }) {
                    TrackingCard(
                        title = stringResource(R.string.tracking_section_notes),
                        emoji = "📝",
                        isFilled = uiState.notes.isNotBlank(),
                        accentColor = MaterialTheme.colorScheme.primary
                    ) {
                        OutlinedTextField(
                            value = uiState.notes,
                            onValueChange = { viewModel.setNotes(it) },
                            placeholder = { Text(stringResource(R.string.tracking_notes_placeholder)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp),
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Save Button ────────────────────────────
                AnimatedSection(visible = showSections.getOrElse(7) { false }) {
                    Button(
                        onClick = { viewModel.saveLog() },
                        enabled = uiState.hasAnyData && !uiState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.large,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (uiState.existingLogId != 0L)
                                    stringResource(R.string.tracking_update_log)
                                else stringResource(R.string.tracking_save_log),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// ══════════════════════════════════════════════════════════════════
// ── Reusable Components ──────────────────────────────────────────
// ══════════════════════════════════════════════════════════════════

/**
 * Card wrapper for each tracking section. Shows a filled-state
 * accent bar on the left edge when the section has data.
 */
@Composable
private fun TrackingCard(
    title: String,
    emoji: String,
    isFilled: Boolean,
    accentColor: Color,
    badge: String? = null,
    content: @Composable () -> Unit
) {
    val cardBackground by animateColorAsState(
        targetValue = if (isFilled)
            accentColor.copy(alpha = 0.06f)
        else
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        animationSpec = tween(400),
        label = "cardBg"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = emoji, fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                // Badge for multi-select counts
                if (badge != null) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = accentColor.copy(alpha = 0.15f)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = badge,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor
                        )
                    }
                }
                // Filled indicator dot
                if (isFilled && badge == null) {
                    Box(modifier = Modifier.size(8.dp)) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(color = accentColor)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            content()
        }
    }
}

/**
 * Animated FilterChip with bounce scale on selection.
 */
@Composable
private fun TrackingChip(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.04f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "chipScale"
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = selectedColor,
            selectedLabelColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.then(
            if (scale != 1f) Modifier else Modifier // scale applied via graphicsLayer below
        ),
        border = if (selected) null else FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = false,
            borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    )
}

/**
 * Small completeness ring shown in the top bar.
 * Animates as the user fills in more sections.
 */
@Composable
private fun LogCompletenessRing(
    completed: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (total > 0) completed.toFloat() / total.toFloat() else 0f
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(500, easing = FastOutSlowInEasing)
        )
    }

    val progressColor by animateColorAsState(
        targetValue = when {
            progress >= 1f -> Color(0xFF4CAF50) // All done — green!
            progress >= 0.5f -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outline
        },
        animationSpec = tween(400),
        label = "ringColor"
    )

    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 3.dp.toPx()
            val padding = strokeWidth / 2 + 1.dp.toPx()
            val arcSize = Size(size.width - padding * 2, size.height - padding * 2)
            val arcOffset = Offset(padding, padding)

            // Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * animatedProgress.value,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Text(
            text = "$completed",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Wrapper for staggered entrance animation.
 */
@Composable
private fun AnimatedSection(
    visible: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(350)) + slideInVertically(tween(350)) { it / 4 }
    ) {
        content()
    }
}

// ── Emoji helpers ────────────────────────────────────────────────

private fun moodEmoji(mood: Mood): String = when (mood) {
    Mood.HAPPY -> "😊"
    Mood.SAD -> "😢"
    Mood.ANXIOUS -> "😟"
    Mood.IRRITABLE -> "😤"
    Mood.CALM -> "😌"
    Mood.ENERGETIC -> "😄"
}

private fun flowEmoji(intensity: FlowIntensity): String = when (intensity) {
    FlowIntensity.NONE -> "◽"
    FlowIntensity.SPOTTING -> "🔸"
    FlowIntensity.LIGHT -> "🩸"
    FlowIntensity.MEDIUM -> "💧"
    FlowIntensity.HEAVY -> "🌊"
}

private fun symptomEmoji(symptom: Symptom): String = when (symptom) {
    Symptom.CRAMPS -> "😣"
    Symptom.HEADACHE -> "🤕"
    Symptom.BLOATING -> "🎈"
    Symptom.ACNE -> "😶"
    Symptom.MOOD_SWINGS -> "🎭"
    Symptom.FATIGUE -> "😴"
    Symptom.TENDER_BREASTS -> "💗"
    Symptom.BACK_PAIN -> "🦴"
    Symptom.NAUSEA -> "🤢"
    Symptom.FOOD_CRAVINGS -> "🍫"
}
