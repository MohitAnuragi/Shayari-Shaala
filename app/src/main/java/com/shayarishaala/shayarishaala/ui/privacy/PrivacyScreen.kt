package com.shayarishaala.shayarishaala.ui.privacy

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavHostController
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shayarishaala.shayarishaala.ui.components.BentoTile
import com.shayarishaala.shayarishaala.ui.components.KaagazTopBar
import com.shayarishaala.shayarishaala.ui.theme.Ink
import com.shayarishaala.shayarishaala.ui.theme.InkMuted
import com.shayarishaala.shayarishaala.ui.theme.Paper
import com.shayarishaala.shayarishaala.ui.theme.PaperCard
import com.shayarishaala.shayarishaala.utils.PreferenceManager

private const val POLICY_FILE = "privacy_policy.txt"
private const val HEADING_PREFIX = "## "

/** The privacy policy (bundled, so it also opens offline) and the user's privacy choices. */
@Composable
fun PrivacyScreen(navHostController: NavHostController) {
    val context = LocalContext.current
    val preferenceManager = remember { PreferenceManager(context) }
    val crashlytics = remember { FirebaseCrashlytics.getInstance() }
    val policyLines = remember {
        context.assets.open(POLICY_FILE).bufferedReader(Charsets.UTF_8).use { it.readLines() }
    }
    var aiConsent by remember { mutableStateOf(preferenceManager.hasAiConsent()) }
    var crashReports by remember { mutableStateOf(crashlytics.isCrashlyticsCollectionEnabled) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .verticalScroll(rememberScrollState())
    ) {
        KaagazTopBar(
            title = "Privacy Policy",
            subtitle = "Shayari Shaala",
            onBack = dropUnlessResumed { navHostController.popBackStack() }
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .navigationBarsPadding()
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BentoTile(color = PaperCard, onClick = null, modifier = Modifier.fillMaxWidth()) {
                PrivacySwitch(
                    title = "Kalam AI",
                    caption = "Send my topic to Google Gemini",
                    checked = aiConsent,
                    onCheckedChange = {
                        aiConsent = it
                        preferenceManager.setAiConsent(it)
                    }
                )
                PrivacySwitch(
                    title = "Crash reports",
                    caption = "Send crash data to Firebase Crashlytics",
                    checked = crashReports,
                    onCheckedChange = {
                        crashReports = it
                        crashlytics.setCrashlyticsCollectionEnabled(it)
                    }
                )
            }

            SelectionContainer {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    policyLines.filter { it.isNotBlank() }.forEach { line ->
                        if (line.startsWith(HEADING_PREFIX)) {
                            Text(
                                text = line.removePrefix(HEADING_PREFIX),
                                style = MaterialTheme.typography.titleLarge,
                                color = Ink,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        } else {
                            Text(line, style = MaterialTheme.typography.bodyMedium, color = Ink)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PrivacySwitch(
    title: String,
    caption: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Ink)
            Text(caption, style = MaterialTheme.typography.bodySmall, color = InkMuted)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
