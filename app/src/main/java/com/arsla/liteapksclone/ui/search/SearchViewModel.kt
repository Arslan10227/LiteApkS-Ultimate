package com.arsla.liteapksclone.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsla.liteapksclone.api.dto.PostDto
import com.arsla.liteapksclone.data.entity.SearchHistory
import com.arsla.liteapksclone.data.repository.SearchRepository
import com.arsla.liteapksclone.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query

    private val _results = MutableStateFlow<Resource<List<PostDto>>>(Resource.Loading())
    val results: StateFlow<Resource<List<PostDto>>> = _results

    val history = searchRepository.getHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onQueryChange(value: String) {
        _query.value = value
    }

    fun search() {
        val q = _query.value.trim()
        if (q.isBlank()) return
        viewModelScope.launch {
            searchRepository.search(q).collect { _results.value = it }
        }
    }

    fun deleteHistory(query: String) = viewModelScope.launch {
        searchRepository.deleteHistory(query)
    }

    fun clearHistory() = viewModelScope.launch {
        searchRepository.clearHistory()
    }
}
