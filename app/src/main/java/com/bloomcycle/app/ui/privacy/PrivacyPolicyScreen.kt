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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
                        text = "Privacy & Data",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
                        text = "\uD83D\uDD12 Our Privacy Commitment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FertileGreen
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "BloomCycle is designed with privacy as its foundation. Your menstrual health data is deeply personal, and we believe you should have complete control over it.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Policy Sections ──────────────────────────
            PolicySection(
                title = "100% Local Storage",
                emoji = "\uD83D\uDCF1",
                content = "All your data — daily logs, cycle predictions, symptoms, moods, and preferences — is stored exclusively on your device. Nothing is sent to external servers, cloud services, or third parties. Your phone is the only place your data exists."
            )

            PolicySection(
                title = "No Account Required",
                emoji = "\uD83D\uDE4B",
                content = "BloomCycle works without any account registration, email, or personal identity information. You don't need to sign in, create a profile, or share identifying information to use any feature."
            )

            PolicySection(
                title = "No Analytics or Tracking",
                emoji = "\uD83D\uDEAB",
                content = "We do not collect usage analytics, behavioral data, crash reports, or any form of telemetry. There are no tracking pixels, advertising identifiers, or third-party SDKs that monitor your activity."
            )

            PolicySection(
                title = "No Internet Access",
                emoji = "\uD83C\uDF10",
                content = "BloomCycle does not require an internet connection to function. The app works entirely offline. No data is ever transmitted over the network. If you check your device's network permissions, you'll see BloomCycle requests none."
            )

            PolicySection(
                title = "Your Data, Your Control",
                emoji = "\u2705",
                content = "You can export all your data at any time in CSV or text format via the Reports & Export screen. You can also permanently delete all data from Settings. When you delete data, it is immediately and irreversibly removed from your device."
            )

            PolicySection(
                title = "Biometric Protection",
                emoji = "\uD83D\uDD10",
                content = "Enable biometric lock (fingerprint or face) to prevent unauthorized access to your health data. When enabled, you must authenticate each time you open the app. Biometric data is handled entirely by your device's secure hardware — BloomCycle never accesses or stores your biometric information."
            )

            PolicySection(
                title = "Screenshot Protection",
                emoji = "\uD83D\uDCF7",
                content = "Enable screen security in Settings to prevent screenshots and screen recordings of the app. This adds FLAG_SECURE to the app window, ensuring your health data cannot be captured by other apps or screen recording tools."
            )

            PolicySection(
                title = "Data Encryption",
                emoji = "\uD83D\uDD10",
                content = "Your data is stored in Android's Room database and DataStore, both of which reside in the app's private storage directory. This directory is protected by Android's sandboxing security model, meaning no other app can access it without root permissions."
            )

            PolicySection(
                title = "Open Source Philosophy",
                emoji = "\uD83D\uDCBB",
                content = "BloomCycle's privacy claims are verifiable. The app requests minimal permissions (only notifications and alarms), contains no hidden network calls, and stores all data locally. You are in complete control."
            )

            PolicySection(
                title = "Uninstall = Complete Deletion",
                emoji = "\uD83D\uDDD1\uFE0F",
                content = "If you uninstall BloomCycle, all data is permanently deleted from your device. There is no residual data, no backups on external servers, and no way to recover your information after uninstallation unless you've exported it beforehand."
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
                        text = "Questions about your privacy?",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "BloomCycle is a personal project built with care. If you have any concerns about data handling, the entire codebase can be audited. Your trust matters.",
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
