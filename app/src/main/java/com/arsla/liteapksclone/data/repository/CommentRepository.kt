package com.arsla.liteapksclone.data.repository

import com.arsla.liteapksclone.api.LiteapksApi
import com.arsla.liteapksclone.api.dto.WpCommentDto
import com.arsla.liteapksclone.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
    private val api: LiteapksApi
) {

    fun getComments(
        postId: Int,
        perPage: Int = 20,
        page: Int = 1
    ): Flow<Resource<List<WpCommentDto>>> = flow {
        emit(Resource.Loading())
        try {
            val comments = api.getComments(postId, perPage, page)
            emit(Resource.Success(comments))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }
}
