package com.arsla.liteapksclone.download

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.arsla.liteapksclone.api.dto.PostDto
import com.arsla.liteapksclone.api.dto.VersionDownloadDto
import com.arsla.liteapksclone.api.dto.VersionDto
import com.arsla.liteapksclone.data.dao.DownloadDao
import com.arsla.liteapksclone.data.entity.DownloadEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val downloadDao: DownloadDao
) {

    val downloads: Flow<List<DownloadEntity>> = downloadDao.getAll()

    suspend fun startDownload(
        post: PostDto,
        version: VersionDto,
        link: VersionDownloadDto
    ): Long {
        val safe = version.version.replace(Regex("[^A-Za-z0-9.-]"), "_")
        val fileName = "${post.id}-${safe}-${link.role}.apk"
        val entity = DownloadEntity(
            postId = post.id,
            title = post.title,
            version = version.version,
            downloadUrl = link.downloadLink,
            fileName = fileName
        )
        val id = downloadDao.insert(entity)
        enqueueWork(id, post.title)
        return id
    }

    suspend fun pause(id: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("download-$id")
        val d = downloadDao.getById(id) ?: return
        if (d.status == DownloadEntity.STATUS_DOWNLOADING) {
            downloadDao.update(d.copy(status = DownloadEntity.STATUS_PAUSED))
        }
    }

    suspend fun resume(id: Long) {
        val d = downloadDao.getById(id) ?: return
        if (d.status == DownloadEntity.STATUS_PAUSED || d.status == DownloadEntity.STATUS_FAILED) {
            downloadDao.update(d.copy(status = DownloadEntity.STATUS_PENDING))
            enqueueWork(id, d.title)
        }
    }

    suspend fun retry(id: Long) {
        resume(id)
    }

    suspend fun delete(id: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("download-$id")
        val d = downloadDao.getById(id) ?: return
        val file = File(context.cacheDir, "apk_downloaded/${d.fileName}")
        if (file.exists()) file.delete()
        downloadDao.delete(d)
    }

    private fun enqueueWork(id: Long, title: String) {
        val data = Data.Builder()
            .putLong(KEY_DOWNLOAD_ID, id)
            .putInt(KEY_POST_ID, 0)
            .putString(KEY_TITLE, title)
            .build()

        val work = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setInputData(data)
            .addTag(DOWNLOAD_WORK_TAG)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                WorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "download-$id",
            ExistingWorkPolicy.KEEP,
            work
        )
    }
}
