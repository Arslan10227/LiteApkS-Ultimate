package com.arsla.liteapksclone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class CachedPost(
    @PrimaryKey val id: Int,
    val json: String,
    val cachedAt: Long = System.currentTimeMillis()
)
