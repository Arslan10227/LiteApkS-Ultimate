package com.arsla.liteapksclone.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsla.liteapksclone.api.dto.UpdateData
import com.arsla.liteapksclone.data.repository.CategoryRepository
import com.arsla.liteapksclone.data.repository.HomeRepository
import com.arsla.liteapksclone.data.repository.PostRepository
import com.arsla.liteapksclone.data.repository.SearchRepository
import com.arsla.liteapksclone.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository,
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _update = MutableStateFlow<Resource<UpdateData>?>(null)
    val update: StateFlow<Resource<UpdateData>?> = _update

    private val _cleared = MutableStateFlow(false)
    val cleared: StateFlow<Boolean> = _cleared

    fun checkForUpdate() = viewModelScope.launch {
        _update.value = try {
            Resource.Success(homeRepository.getLatestVersion())
        } catch (e: Exception) {
            Resource.Error(e.localizedMessage ?: "Update check failed")
        }
    }

    fun clearCache() = viewModelScope.launch {
        postRepository.clearCache()
        categoryRepository.clearCache()
        searchRepository.clearHistory()
        _cleared.value = true
    }

    fun clearCacheAcknowledged() {
        _cleared.value = false
    }
}
