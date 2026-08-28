package com.arsla.liteapksclone.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.arsla.liteapksclone.data.entity.CachedPost

@Dao
interface PostDao {

    @Query("SELECT * FROM posts ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<CachedPost>

    @Query("SELECT * FROM posts WHERE id = :id")
    suspend fun getById(id: Int): CachedPost?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(posts: List<CachedPost>)

    @Query("DELETE FROM posts")
    suspend fun clear()
}
