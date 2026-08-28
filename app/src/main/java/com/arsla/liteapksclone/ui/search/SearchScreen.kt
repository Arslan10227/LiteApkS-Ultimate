package com.arsla.liteapksclone.ui.search

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.arsla.liteapksclone.R
import com.arsla.liteapksclone.api.dto.PostDto
import com.arsla.liteapksclone.ui.components.DEFAULT_LOTTIE_URL
import com.arsla.liteapksclone.ui.components.EmptyState
import com.arsla.liteapksclone.ui.components.PostCard
import com.arsla.liteapksclone.util.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onPostClick: (PostDto) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val query by viewModel.query.collectAsState()
    val results by viewModel.results.collectAsState()
    val history by viewModel.history.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(title = { Text(stringResource(R.string.search)) })

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::onQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search apps and games...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotBlank()) {
                    IconButton(onClick = { viewModel.onQueryChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true
        )

        if (query.isBlank() && history.isNotEmpty()) {
            Text(
                text = "Recent searches",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp, top = 8.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history) { item ->
                    FilterChip(
                        selected = false,
                        onClick = {
                            viewModel.onQueryChange(item.query)
                            viewModel.search()
                        },
                        label = { Text(item.query) },
                        leadingIcon = { Icon(Icons.Default.History, contentDescription = null) }
                    )
                }
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            when (val r = results) {
                is Resource.Loading -> {
                    if (query.isNotBlank()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    } else {
                        item {
                            EmptyState(
                                icon = Icons.Default.Search,
                                title = stringResource(R.string.search),
                                message = "Type to search apps and games",
                                lottieUrl = DEFAULT_LOTTIE_URL
                            )
                        }
                    }
                }
                is Resource.Error -> {
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = r.message, color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            IconButton(onClick = { viewModel.search() }) {
                                Icon(Icons.Default.Search, contentDescription = "Search again")
                            }
                        }
                    }
                }
                is Resource.Success -> {
                    val list = r.data
                    if (list.isEmpty()) {
                        item {
                            EmptyState(
                                icon = Icons.Default.Search,
                                title = stringResource(R.string.no_results),
                                message = "Try a different keyword",
                                lottieUrl = DEFAULT_LOTTIE_URL
                            )
                        }
                    } else {
                        items(list) { post ->
                            PostCard(post = post, onClick = { onPostClick(post) })
                        }
                    }
                }
            }
        }
    }
}
