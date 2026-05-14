package com.bloomcycle.app.ui.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bloomcycle.app.R
import com.bloomcycle.app.ui.theme.FertileGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.privacy_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // ── Privacy Commitment ───────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = FertileGreen.copy(alpha = 0.08f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.privacy_commitment_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FertileGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.privacy_commitment_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Policy Sections ──────────────────────────
            PolicySection(
                title = stringResource(R.string.privacy_local_storage_title),
                emoji = "\uD83D\uDCF1",
                content = stringResource(R.string.privacy_local_storage_body)
            )

            PolicySection(
                title = stringResource(R.string.privacy_no_account_title),
                emoji = "\uD83D\uDE4B",
                content = stringResource(R.string.privacy_no_account_body)
            )

            PolicySection(
                title = stringResource(R.string.privacy_no_analytics_title),
                emoji = "\uD83D\uDEAB",
                content = stringResource(R.string.privacy_no_analytics_body)
            )

            PolicySection(
                title = stringResource(R.string.privacy_no_internet_title),
                emoji = "\uD83C\uDF10",
                content = stringResource(R.string.privacy_no_internet_body)
            )

            PolicySection(
                title = stringResource(R.string.privacy_your_data_title),
                emoji = "\u2705",
                content = stringResource(R.string.privacy_your_data_body)
            )

            PolicySection(
                title = stringResource(R.string.privacy_biometric_title),
                emoji = "\uD83D\uDD10",
                content = stringResource(R.string.privacy_biometric_body)
            )

            PolicySection(
                title = stringResource(R.string.privacy_screenshot_title),
                emoji = "\uD83D\uDCF7",
                content = stringResource(R.string.privacy_screenshot_body)
            )

            PolicySection(
                title = stringResource(R.string.privacy_encryption_title),
                emoji = "\uD83D\uDD10",
                content = stringResource(R.string.privacy_encryption_body)
            )

            PolicySection(
                title = stringResource(R.string.privacy_open_source_title),
                emoji = "\uD83D\uDCBB",
                content = stringResource(R.string.privacy_open_source_body)
            )

            PolicySection(
                title = stringResource(R.string.privacy_uninstall_title),
                emoji = "\uD83D\uDDD1\uFE0F",
                content = stringResource(R.string.privacy_uninstall_body)
            )

            // ── Contact ──────────────────────────────────
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.privacy_questions_title),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.privacy_questions_body),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PolicySection(
    title: String,
    emoji: String,
    content: String
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = "$emoji  $title",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            lineHeight = MaterialTheme.typography.bodySmall.lineHeight
        )
    }
}
