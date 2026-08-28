package com.arsla.liteapksclone.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.arsla.liteapksclone.api.dto.PostDto
import com.arsla.liteapksclone.api.dto.VersionDownloadDto
import com.arsla.liteapksclone.api.dto.VersionDto
import com.arsla.liteapksclone.api.dto.WpCommentDto
import com.arsla.liteapksclone.ui.components.CommentCard
import com.arsla.liteapksclone.ui.components.EmptyState
import com.arsla.liteapksclone.ui.components.HtmlText
import com.arsla.liteapksclone.ui.components.VersionItem
import com.arsla.liteapksclone.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    postId: Int,
    onBack: () -> Unit,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val post by viewModel.post.collectAsState()
    val comments by viewModel.comments.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("App Detail") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                IconButton(onClick = { viewModel.load() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        )

        when (val p = post) {
            is Resource.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                ErrorDetail(message = p.message, onRetry = { viewModel.load() })
            }
            is Resource.Success -> {
                DetailContent(
                    post = p.data,
                    comments = comments,
                    onDownload = { version, link ->
                        viewModel.download(version, link)
                    }
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    post: PostDto,
    comments: Resource<List<WpCommentDto>>,
    onDownload: (VersionDto, VersionDownloadDto) -> Unit
) {
    val downloads = post.versions.flatMap { version ->
        version.versionDownloads.map { version to it }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            BannerImage(post = post)
        }

        item {
            TitleSection(post = post)
        }

        item {
            Text(
                text = "About",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
            HtmlText(
                html = post.content,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            if (post.modInfo.isNotBlank() || post.modFeatures.isNotBlank()) {
                Text(
                    text = "Mod Info",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
                if (post.modInfo.isNotBlank()) {
                    Text(
                        text = post.modInfo,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
                if (post.modFeatures.isNotBlank()) {
                    HtmlText(
                        html = post.modFeatures,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }

        item {
            Text(
                text = "Versions",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
        }

        if (downloads.isEmpty()) {
            item {
                Text(
                    text = "No download links available.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(downloads) { (version, link) ->
                VersionItem(
                    version = version,
                    link = link,
                    onDownload = { onDownload(version, link) }
                )
            }
        }

        item {
            Text(
                text = "Comments",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp)
            )
        }

        when (comments) {
            is Resource.Loading -> {
                item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp)) }
            }
            is Resource.Error -> {
                item {
                    Text(
                        text = comments.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            is Resource.Success -> {
                val list = comments.data
                if (list.isEmpty()) {
                    item {
                        Text(
                            text = "No comments yet.",
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(list) { comment ->
                        CommentCard(comment = comment)
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }
    }
}

@Composable
private fun BannerImage(post: PostDto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        AsyncImage(
            model = post.banner ?: post.images.image,
            contentDescription = post.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .align(Alignment.BottomStart)
        ) {
            AsyncImage(
                model = post.images.thumbnail,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(start = 16.dp, bottom = 16.dp)
                    .size(64.dp)
                    .align(Alignment.BottomStart)
            )
        }
    }
}

@Composable
private fun TitleSection(post: PostDto) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = post.title,
            style = MaterialTheme.typography.headlineMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = post.genre,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AssistChip(onClick = { }, label = { Text(post.size) })
            SuggestionChip(onClick = { }, label = { Text(post.type) })
            SuggestionChip(onClick = { }, label = { Text("${post.ratingAvg}") })
            SuggestionChip(onClick = { }, label = { Text("${post.postViews} views") })
        }

        Text(
            text = "Version: ${post.latestVersion}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun ErrorDetail(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 16.dp)) {
            Text("Retry")
        }
    }
}
