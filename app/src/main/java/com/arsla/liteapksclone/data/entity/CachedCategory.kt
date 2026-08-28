package com.arsla.liteapksclone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CachedCategory(
    @PrimaryKey val id: Int,
    val name: String,
    val slug: String,
    val parent: Int,
    val count: Int
)
