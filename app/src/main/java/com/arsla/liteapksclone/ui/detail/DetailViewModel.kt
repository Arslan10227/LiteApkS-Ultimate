package com.arsla.liteapksclone.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsla.liteapksclone.api.dto.PostDto
import com.arsla.liteapksclone.api.dto.VersionDownloadDto
import com.arsla.liteapksclone.api.dto.VersionDto
import com.arsla.liteapksclone.api.dto.WpCommentDto
import com.arsla.liteapksclone.data.repository.CommentRepository
import com.arsla.liteapksclone.data.repository.PostRepository
import com.arsla.liteapksclone.download.DownloadRepository
import com.arsla.liteapksclone.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    val postId: Int = savedStateHandle["postId"] ?: 0

    private val _post = MutableStateFlow<Resource<PostDto>>(Resource.Loading())
    val post: StateFlow<Resource<PostDto>> = _post

    private val _comments = MutableStateFlow<Resource<List<WpCommentDto>>>(Resource.Loading())
    val comments: StateFlow<Resource<List<WpCommentDto>>> = _comments

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            postRepository.getPost(postId).collect { _post.value = it }
        }
        viewModelScope.launch {
            commentRepository.getComments(postId).collect { _comments.value = it }
        }
    }

    fun download(version: VersionDto, link: VersionDownloadDto) = viewModelScope.launch {
        val current = _post.value as? Resource.Success<PostDto> ?: return@launch
        downloadRepository.startDownload(current.data, version, link.copy(downloadLink = link.downloadLink.replace(" ", "%20")))
    }
}
