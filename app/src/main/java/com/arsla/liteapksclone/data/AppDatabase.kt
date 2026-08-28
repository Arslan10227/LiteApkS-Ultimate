package com.arsla.liteapksclone.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.arsla.liteapksclone.data.dao.CategoryDao
import com.arsla.liteapksclone.data.dao.DownloadDao
import com.arsla.liteapksclone.data.dao.PostDao
import com.arsla.liteapksclone.data.dao.SearchHistoryDao
import com.arsla.liteapksclone.data.entity.CachedCategory
import com.arsla.liteapksclone.data.entity.CachedPost
import com.arsla.liteapksclone.data.entity.DownloadEntity
import com.arsla.liteapksclone.data.entity.SearchHistory

@Database(
    entities = [CachedPost::class, CachedCategory::class, SearchHistory::class, DownloadEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao
    abstract fun categoryDao(): CategoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun downloadDao(): DownloadDao
}
