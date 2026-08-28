package com.arsla.liteapksclone.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.arsla.liteapksclone.api.dto.CategoryDto
import com.arsla.liteapksclone.api.dto.HomeData
import com.arsla.liteapksclone.api.dto.PostDto
import com.arsla.liteapksclone.data.repository.CategoryRepository
import com.arsla.liteapksclone.data.repository.HomeRepository
import com.arsla.liteapksclone.data.repository.PostRepository
import com.arsla.liteapksclone.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
    private val postRepository: PostRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _home = MutableStateFlow<Resource<HomeData>>(Resource.Loading())
    val home: StateFlow<Resource<HomeData>> = _home

    private val _posts = MutableStateFlow<Resource<List<PostDto>>>(Resource.Loading())
    val posts: StateFlow<Resource<List<PostDto>>> = _posts

    private val _categories = MutableStateFlow<Resource<List<CategoryDto>>>(Resource.Loading())
    val categories: StateFlow<Resource<List<CategoryDto>>> = _categories

    init {
        loadAll()
    }

    fun loadAll() {
        loadHome()
        loadPosts()
        loadCategories()
    }

    fun loadHome() = viewModelScope.launch {
        homeRepository.getHome().collect { _home.value = it }
    }

    fun loadPosts() = viewModelScope.launch {
        postRepository.getPosts().collect { _posts.value = it }
    }

    fun loadCategories() = viewModelScope.launch {
        categoryRepository.getCategories().collect { _categories.value = it }
    }
}
