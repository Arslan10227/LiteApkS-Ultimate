package com.arsla.liteapksclone.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.arsla.liteapksclone.BuildConfig
import com.arsla.liteapksclone.R
import com.arsla.liteapksclone.data.repository.UpdateInfo
import com.arsla.liteapksclone.ui.components.AnimatedDialog
import com.arsla.liteapksclone.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAboutClick: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val update by viewModel.update.collectAsState()
    val cleared by viewModel.cleared.collectAsState()
    val context = LocalContext.current
    var showChangelog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(stringResource(R.string.settings)) })

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Delete, contentDescription = null)
                        Text(
                            text = "Clear cache",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Text(
                        text = "Remove cached posts, categories and downloaded APKs.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Button(
                        onClick = { viewModel.clearCache() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Clear now")
                    }
                    if (cleared) {
                        Text(
                            text = stringResource(R.string.cache_cleared),
                            color = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Update, contentDescription = null)
                        Text(
                            text = "App update",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Text(
                        text = "Current version: ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Button(
                        onClick = { viewModel.checkForUpdate() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("Check now")
                    }
                    when (val u = update) {
                        is Resource.Success -> {
                            if (u.data.isAvailable) {
                                Text(
                                    text = "Version ${u.data.latestVersion} is available",
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                OutlinedButton(
                                    onClick = { showChangelog = true },
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Text("View changelog")
                                }
                            } else {
                                Text(
                                    text = "You are on the latest version",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                        }
                        is Resource.Error -> {
                            Text(
                                text = u.message,
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                        else -> {}
                    }
                }
            }

            val latestUpdate = update as? Resource.Success<UpdateInfo>
            if (latestUpdate != null && showChangelog) {
                AnimatedDialog(
                    visible = true,
                    onDismiss = { showChangelog = false },
                    title = "v${latestUpdate.data.latestVersion} is available",
                    text = latestUpdate.data.changelog,
                    confirmText = "Download update",
                    onConfirm = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(latestUpdate.data.downloadUrl)
                        )
                        context.startActivity(intent)
                    }
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Text(
                            text = "About",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Text(
                        text = "Liteapks Clone is a personal, ad-free learning project. It uses the public Liteapks WordPress REST API.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = "No ads, no analytics, no telemetry.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Button(
                        onClick = onAboutClick,
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        Text("Open about")
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
