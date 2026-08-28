package com.arsla.liteapksclone.data.repository

import android.content.Context
import com.arsla.liteapksclone.api.LiteapksApi
import com.arsla.liteapksclone.api.dto.PostDto
import com.arsla.liteapksclone.data.dao.PostDao
import com.arsla.liteapksclone.data.entity.CachedPost
import com.arsla.liteapksclone.error.ErrorHandler
import com.arsla.liteapksclone.util.Resource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val api: LiteapksApi,
    private val postDao: PostDao,
    private val json: Json
) {

    fun getPosts(perPage: Int = 20, page: Int = 1): Flow<Resource<List<PostDto>>> = flow {
        emit(Resource.Loading())
        try {
            val posts = api.getPosts(perPage, page).data.posts
            postDao.insertAll(posts.map { CachedPost(it.id, json.encodeToString(PostDto.serializer(), it)) })
            emit(Resource.Success(posts))
        } catch (e: Exception) {
            val cached = postDao.getRecent(perPage).map {
                json.decodeFromString(PostDto.serializer(), it.json)
            }
            ErrorHandler.handle(e, "PostRepository")
            emit(Resource.Error(e.localizedMessage ?: "Network error", cached))
        }
    }.flowOn(Dispatchers.IO)

    fun getPost(id: Int): Flow<Resource<PostDto>> = flow {
        emit(Resource.Loading())
        try {
            val post = api.getPost(id).data
            postDao.insertAll(listOf(CachedPost(post.id, json.encodeToString(PostDto.serializer(), post))))
            emit(Resource.Success(post))
        } catch (e: Exception) {
            val cached = postDao.getById(id)?.let {
                json.decodeFromString(PostDto.serializer(), it.json)
            }
            ErrorHandler.handle(e, "PostRepository")
            emit(Resource.Error(e.localizedMessage ?: "Network error", cached))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun clearCache() {
        postDao.clear()
        File(context.cacheDir, "apk_downloaded").deleteRecursively()
    }
}
