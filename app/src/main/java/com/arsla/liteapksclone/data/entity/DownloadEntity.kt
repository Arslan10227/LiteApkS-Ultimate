package com.arsla.liteapksclone.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val postId: Int,
    val title: String,
    val version: String,
    val downloadUrl: String,
    val fileName: String,
    val totalBytes: Long = -1,
    val downloadedBytes: Long = 0,
    val status: String = STATUS_PENDING,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val STATUS_PENDING = "PENDING"
        const val STATUS_DOWNLOADING = "DOWNLOADING"
        const val STATUS_PAUSED = "PAUSED"
        const val STATUS_COMPLETED = "COMPLETED"
        const val STATUS_FAILED = "FAILED"
    }
}
