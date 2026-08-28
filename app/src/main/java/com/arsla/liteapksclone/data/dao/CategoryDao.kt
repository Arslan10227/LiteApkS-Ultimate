package com.arsla.liteapksclone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arsla.liteapksclone.data.entity.CachedCategory

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY name")
    suspend fun getAll(): List<CachedCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CachedCategory>)

    @Query("DELETE FROM categories")
    suspend fun clear()
}
