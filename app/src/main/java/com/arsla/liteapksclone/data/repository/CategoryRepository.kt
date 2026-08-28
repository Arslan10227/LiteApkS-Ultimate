package com.arsla.liteapksclone.data.repository

import com.arsla.liteapksclone.api.LiteapksApi
import com.arsla.liteapksclone.api.dto.CategoryDto
import com.arsla.liteapksclone.data.dao.CategoryDao
import com.arsla.liteapksclone.data.entity.CachedCategory
import com.arsla.liteapksclone.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val api: LiteapksApi,
    private val categoryDao: CategoryDao
) {

    fun getCategories(): Flow<Resource<List<CategoryDto>>> = flow {
        emit(Resource.Loading())
        try {
            val list = api.getCategories().data.list
            categoryDao.insertAll(list.map {
                CachedCategory(it.termId, it.name, it.slug, it.parent, it.count)
            })
            emit(Resource.Success(list))
        } catch (e: Exception) {
            val cached = categoryDao.getAll().map {
                CategoryDto(
                    termId = it.id,
                    name = it.name,
                    slug = it.slug,
                    parent = it.parent,
                    count = it.count
                )
            }
            emit(Resource.Error(e.localizedMessage ?: "Network error", cached))
        }
    }

    suspend fun clearCache() {
        categoryDao.clear()
    }
}
