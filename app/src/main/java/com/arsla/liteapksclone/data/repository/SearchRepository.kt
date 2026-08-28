package com.arsla.liteapksclone.data.repository

import com.arsla.liteapksclone.api.LiteapksApi
import com.arsla.liteapksclone.api.dto.PostDto
import com.arsla.liteapksclone.data.dao.SearchHistoryDao
import com.arsla.liteapksclone.data.entity.SearchHistory
import com.arsla.liteapksclone.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val api: LiteapksApi,
    private val searchHistoryDao: SearchHistoryDao
) {

    fun search(
        query: String,
        perPage: Int = 20,
        page: Int = 1
    ): Flow<Resource<List<PostDto>>> = flow {
        emit(Resource.Loading())
        try {
            val results = api.search(query, perPage, page).data ?: emptyList()
            searchHistoryDao.insert(SearchHistory(query))
            emit(Resource.Success(results))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }.flowOn(Dispatchers.IO)

    fun getHistory(limit: Int = 20): Flow<List<SearchHistory>> =
        searchHistoryDao.getRecent(limit)

    suspend fun deleteHistory(query: String) = searchHistoryDao.delete(query)
    suspend fun clearHistory() = searchHistoryDao.clear()
}
