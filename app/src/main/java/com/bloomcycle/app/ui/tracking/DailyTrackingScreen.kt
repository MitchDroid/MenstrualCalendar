package com.bloomcycle.app.ui.tracking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bloomcycle.app.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bloomcycle.app.domain.model.CervicalMucus
import com.bloomcycle.app.domain.model.FlowIntensity
import com.bloomcycle.app.domain.model.Mood
import com.bloomcycle.app.domain.model.SexualActivity
import com.bloomcycle.app.domain.model.Symptom

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ── Flow Intensity ─────────────────────────
                SectionHeader(title = stringResource(R.string.tracking_section_flow), emoji = "\uD83E\uDE78")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    FlowIntensity.entries.forEach { intensity ->
                        FilterChip(
                            selected = uiState.flowIntensity == intensity,
                            onClick = { viewModel.setFlowIntensity(intensity) },
                            label = { Text(formatEnum(intensity.name)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                TrackingDivider()

                // ── Mood ───────────────────────────────────
                SectionHeader(title = stringResource(R.string.tracking_section_mood), emoji = "\uD83D\uDE0A")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Mood.entries.forEach { mood ->
                        FilterChip(
                            selected = uiState.mood == mood,
                            onClick = { viewModel.setMood(mood) },
                            label = { Text("${moodEmoji(mood)} ${formatEnum(mood.name)}") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        )
                    }
                }

                TrackingDivider()

                // ── Symptoms (multi-select) ────────────────
                SectionHeader(title = stringResource(R.string.tracking_section_symptoms), emoji = "\uD83E\uDE7A")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Symptom.entries.forEach { symptom ->
                        FilterChip(
                            selected = symptom in uiState.symptoms,
                            onClick = { viewModel.toggleSymptom(symptom) },
                            label = { Text(formatEnum(symptom.name)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        )
                    }
                }

                TrackingDivider()

                // ── Cervical Mucus ─────────────────────────
                SectionHeader(title = stringResource(R.string.tracking_section_cervical_mucus), emoji = "\uD83D\uDCA7")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CervicalMucus.entries.forEach { mucus ->
                        FilterChip(
                            selected = uiState.cervicalMucus == mucus,
                            onClick = { viewModel.setCervicalMucus(mucus) },
                            label = { Text(formatEnum(mucus.name)) },
                            border = if (uiState.cervicalMucus == mucus) null
                            else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )
                    }
                }

                TrackingDivider()

                // ── Sexual Activity ────────────────────────
                SectionHeader(title = stringResource(R.string.tracking_section_intimacy), emoji = "\uD83D\uDC95")
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    SexualActivity.entries.forEach { activity ->
                        FilterChip(
                            selected = uiState.sexualActivity == activity,
                            onClick = { viewModel.setSexualActivity(activity) },
                            label = { Text(formatEnum(activity.name)) },
                            border = if (uiState.sexualActivity == activity) null
                            else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                        )
                    }
                }

                TrackingDivider()

                // ── Temperature & Weight ───────────────────
                SectionHeader(title = stringResource(R.string.tracking_section_vitals), emoji = "\uD83C\uDF21\uFE0F")
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
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.weight,
                        onValueChange = { viewModel.setWeight(it) },
                        label = { Text(stringResource(R.string.tracking_weight_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                TrackingDivider()

                // ── Notes ──────────────────────────────────
                SectionHeader(title = stringResource(R.string.tracking_section_notes), emoji = "\uD83D\uDCDD")
                OutlinedTextField(
                    value = uiState.notes,
                    onValueChange = { viewModel.setNotes(it) },
                    placeholder = { Text(stringResource(R.string.tracking_notes_placeholder)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ── Save Button ────────────────────────────
                Button(
                    onClick = { viewModel.saveLog() },
                    enabled = uiState.hasAnyData && !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = MaterialTheme.shapes.large
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
                            text = if (uiState.existingLogId != 0L) stringResource(R.string.tracking_update_log) else stringResource(R.string.tracking_save_log),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String, emoji: String) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "$emoji  $title",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
private fun TrackingDivider() {
    Spacer(modifier = Modifier.height(12.dp))
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    Spacer(modifier = Modifier.height(4.dp))
}

private fun formatEnum(name: String): String {
    return name.replace("_", " ")
        .lowercase()
        .replaceFirstChar { it.uppercase() }
}

private fun moodEmoji(mood: Mood): String = when (mood) {
    Mood.HAPPY -> "\uD83D\uDE0A"
    Mood.SAD -> "\uD83D\uDE22"
    Mood.ANXIOUS -> "\uD83D\uDE1F"
    Mood.IRRITABLE -> "\uD83D\uDE24"
    Mood.CALM -> "\uD83D\uDE0C"
    Mood.ENERGETIC -> "\uD83D\uDE04"
}
