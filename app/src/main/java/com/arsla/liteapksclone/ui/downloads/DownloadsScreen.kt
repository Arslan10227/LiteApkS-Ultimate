package com.arsla.liteapksclone.ui.downloads

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arsla.liteapksclone.R
import com.arsla.liteapksclone.data.entity.DownloadEntity
import com.arsla.liteapksclone.ui.components.DEFAULT_LOTTIE_URL
import com.arsla.liteapksclone.ui.components.EmptyState
import com.arsla.liteapksclone.util.TastyToaster

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    viewModel: DownloadsViewModel = hiltViewModel()
) {
    val downloads by viewModel.downloads.collectAsState()
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(stringResource(R.string.downloads)) })

        if (downloads.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Download,
                title = "No downloads",
                message = "Download an app from the detail screen to see it here.",
                lottieUrl = DEFAULT_LOTTIE_URL
            )
            return
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(downloads, key = { it.id }) { download ->
                DownloadItem(
                    download = download,
                    onPause = {
                        TastyToaster.show(context, "Download paused", TastyToaster.Type.WARNING)
                        viewModel.pause(download.id)
                    },
                    onResume = {
                        TastyToaster.show(context, "Download resumed", TastyToaster.Type.INFO)
                        viewModel.resume(download.id)
                    },
                    onRetry = {
                        TastyToaster.show(context, "Download retrying", TastyToaster.Type.INFO)
                        viewModel.retry(download.id)
                    },
                    onDelete = {
                        TastyToaster.show(context, "Download deleted", TastyToaster.Type.CONFUSING)
                        viewModel.delete(download.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun DownloadItem(
    download: DownloadEntity,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onRetry: () -> Unit,
    onDelete: () -> Unit
) {
    val progress = if (download.totalBytes > 0) {
        (download.downloadedBytes.toFloat() / download.totalBytes).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = download.title,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${download.version} • ${download.status}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "${download.downloadedBytes / 1024 / 1024} MB / ${download.totalBytes / 1024 / 1024} MB",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                when (download.status) {
                    DownloadEntity.STATUS_DOWNLOADING -> {
                        IconButton(onClick = onPause) {
                            Icon(Icons.Default.Pause, contentDescription = "Pause")
                        }
                    }
                    DownloadEntity.STATUS_PAUSED -> {
                        IconButton(onClick = onResume) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Resume")
                        }
                    }
                    DownloadEntity.STATUS_FAILED -> {
                        IconButton(onClick = onRetry) {
                            Icon(Icons.Default.Refresh, contentDescription = "Retry")
                        }
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}
