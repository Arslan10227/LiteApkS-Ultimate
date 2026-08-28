package com.arsla.liteapksclone.data.repository

import com.arsla.liteapksclone.api.LiteapksApi
import com.arsla.liteapksclone.api.dto.HomeData
import com.arsla.liteapksclone.api.dto.UpdateData
import com.arsla.liteapksclone.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HomeRepository @Inject constructor(
    private val api: LiteapksApi
) {

    fun getHome(): Flow<Resource<HomeData>> = flow {
        emit(Resource.Loading())
        try {
            emit(Resource.Success(api.getHome().data))
        } catch (e: Exception) {
            emit(Resource.Error(e.localizedMessage ?: "Network error"))
        }
    }

    suspend fun getLatestVersion(): UpdateData = api.getLatestVersion().data
}
