package com.arsla.liteapksclone.ui.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsla.liteapksclone.data.entity.DownloadEntity
import com.arsla.liteapksclone.download.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DownloadsViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    val downloads: StateFlow<List<DownloadEntity>> = downloadRepository.downloads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun pause(id: Long) = viewModelScope.launch { downloadRepository.pause(id) }
    fun resume(id: Long) = viewModelScope.launch { downloadRepository.resume(id) }
    fun retry(id: Long) = viewModelScope.launch { downloadRepository.retry(id) }
    fun delete(id: Long) = viewModelScope.launch { downloadRepository.delete(id) }
}
