package com.arsla.liteapksclone.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.arsla.liteapksclone.api.dto.CategoryDto
import com.arsla.liteapksclone.api.dto.HomeData
import com.arsla.liteapksclone.api.dto.PostDto
import com.arsla.liteapksclone.ui.components.EmptyState
import com.arsla.liteapksclone.ui.components.PostCard
import com.arsla.liteapksclone.ui.components.ShimmerCard
import com.arsla.liteapksclone.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onPostClick: (PostDto) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val home by viewModel.home.collectAsState()
    val posts by viewModel.posts.collectAsState()
    val categories by viewModel.categories.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Liteapks Clone") },
            actions = {
                IconButton(onClick = { viewModel.loadAll() }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
        )

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            when (val h = home) {
                is Resource.Loading -> item { LinearProgressIndicator(modifier = Modifier.fillMaxWidth()) }
                is Resource.Error -> item {
                    ErrorItem(h.message) { viewModel.loadHome() }
                }
                is Resource.Success -> {
                    h.data.sliders.takeIf { it.isNotEmpty() }?.let { sliders ->
                        item {
                            SliderRow(sliders = sliders, onPostClick = onPostClick)
                        }
                    }
                    h.data.tabs.takeIf { it.isNotEmpty() }?.let { tabs ->
                        item {
                            Text(
                                text = tabs.firstOrNull()?.title ?: "",
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                )
            }

            item {
                when (categories) {
                    is Resource.Loading -> {
                        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp)) {
                            items(5) { FilterChip(selected = false, onClick = {}, label = { Text("     ") }) }
                        }
                    }
                    is Resource.Error -> {}
                    is Resource.Success -> {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items((categories as Resource.Success).data) { category ->
                                FilterChip(
                                    selected = false,
                                    onClick = { },
                                    label = { Text(category.name) }
                                )
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Latest Apps",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            when (posts) {
                is Resource.Loading -> {
                    items(6) { ShimmerCard() }
                }
                is Resource.Error -> {
                    val error = posts as Resource.Error
                    item {
                        ErrorItem(error.message) { viewModel.loadPosts() }
                    }
                    error.data?.let { list ->
                        items(list) { post ->
                            PostCard(post = post, onClick = { onPostClick(post) })
                        }
                    }
                }
                is Resource.Success -> {
                    items((posts as Resource.Success).data) { post ->
                        PostCard(post = post, onClick = { onPostClick(post) })
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun SliderRow(
    sliders: List<com.arsla.liteapksclone.api.dto.SliderItemDto>,
    onPostClick: (PostDto) -> Unit
) {
    LazyRow(
        modifier = Modifier.padding(vertical = 16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(sliders) { slider ->
            Box(
                modifier = Modifier
                    .height(180.dp)
                    .fillParentMaxWidth(0.9f)
            ) {
                AsyncImage(
                    model = slider.banner,
                    contentDescription = slider.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
private fun ErrorItem(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error
        )
        Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
            Text("Retry")
        }
    }
}
