package com.arsla.liteapksclone.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.arsla.liteapksclone.R
import com.arsla.liteapksclone.data.dao.DownloadDao
import com.arsla.liteapksclone.data.entity.DownloadEntity
import com.arsla.liteapksclone.error.ErrorHandler
import com.arsla.liteapksclone.install.ApkInstaller
import com.arsla.liteapksclone.util.TastyToaster
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

const val DOWNLOAD_WORK_TAG = "download"
const val KEY_DOWNLOAD_ID = "download_id"
const val KEY_POST_ID = "post_id"
const val KEY_TITLE = "title"

@HiltWorker
class DownloadWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val downloadDao: DownloadDao,
    private val client: OkHttpClient
) : CoroutineWorker(context, params) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Suppress("BlockingMethodInNonBlockingContext")
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val downloadId = inputData.getLong(KEY_DOWNLOAD_ID, -1)
        if (downloadId == -1L) return@withContext Result.failure()

        val download = downloadDao.getById(downloadId) ?: return@withContext Result.failure()
        val file = File(applicationContext.cacheDir, "apk_downloaded/${download.fileName}")
        file.parentFile?.mkdirs()

        createNotificationChannel()
        val foregroundInfo = ForegroundInfo(
            downloadId.toInt(),
            buildNotification(download.title, 0, 0)
        )
        setForeground(foregroundInfo)
        TastyToaster.show(applicationContext, "Download started", TastyToaster.Type.INFO)

        val offset = file.length()
        val requestBuilder = Request.Builder().url(download.downloadUrl.replace(" ", "%20"))
        if (offset > 0) {
            requestBuilder.header("Range", "bytes=$offset-")
        }

        val response = client.newCall(requestBuilder.build()).execute()
        if (!response.isSuccessful) {
            downloadDao.update(download.copy(status = DownloadEntity.STATUS_FAILED))
            TastyToaster.show(applicationContext, "Download failed", TastyToaster.Type.ERROR)
            return@withContext Result.retry()
        }

        val body = response.body ?: run {
            downloadDao.update(download.copy(status = DownloadEntity.STATUS_FAILED))
            TastyToaster.show(applicationContext, "Download failed", TastyToaster.Type.ERROR)
            return@withContext Result.failure()
        }

        val total = (body.contentLength() + offset).coerceAtLeast(1)
        downloadDao.update(
            download.copy(
                totalBytes = total,
                downloadedBytes = offset,
                status = DownloadEntity.STATUS_DOWNLOADING
            )
        )

        val input = body.byteStream()
        val output = FileOutputStream(file, offset > 0)
        val buffer = ByteArray(8192)
        var read: Int
        var downloaded = offset
        var lastNotify = SystemClock.elapsedRealtime()

        try {
            while (input.read(buffer).also { read = it } != -1) {
                output.write(buffer, 0, read)
                downloaded += read

                if (SystemClock.elapsedRealtime() - lastNotify > 250) {
                    downloadDao.update(download.copy(downloadedBytes = downloaded))
                    notificationManager.notify(
                        downloadId.toInt(),
                        buildNotification(download.title, downloaded, total)
                    )
                    lastNotify = SystemClock.elapsedRealtime()
                }

                if (isStopped) break
            }
            output.flush()
        } catch (e: Exception) {
            TastyToaster.show(applicationContext, "Download failed", TastyToaster.Type.ERROR)
            ErrorHandler.handle(e, "DownloadWorker")
            downloadDao.update(
                download.copy(
                    status = DownloadEntity.STATUS_FAILED,
                    downloadedBytes = downloaded
                )
            )
            return@withContext Result.retry()
        } finally {
            output.close()
            input.close()
        }

        if (isStopped) {
            TastyToaster.show(applicationContext, "Download paused", TastyToaster.Type.WARNING)
            downloadDao.update(
                download.copy(
                    status = DownloadEntity.STATUS_PAUSED,
                    downloadedBytes = downloaded
                )
            )
            return@withContext Result.failure(
                Data.Builder()
                    .putLong("offset", downloaded)
                    .putString("status", DownloadEntity.STATUS_PAUSED)
                    .build()
            )
        }

        downloadDao.update(
            download.copy(
                status = DownloadEntity.STATUS_COMPLETED,
                downloadedBytes = downloaded
            )
        )
        TastyToaster.show(applicationContext, "Download completed", TastyToaster.Type.SUCCESS)
        notificationManager.notify(
            downloadId.toInt(),
            buildNotification(download.title, total, total, complete = true)
        )

        try {
            ApkInstaller.install(applicationContext, file)
        } catch (e: Exception) {
            // Installation is user-driven; ignore.
        }

        return@withContext Result.success(
            Data.Builder()
                .putString("file_path", file.absolutePath)
                .putString("status", DownloadEntity.STATUS_COMPLETED)
                .build()
        )
    }

    private fun buildNotification(
        title: String,
        current: Long,
        total: Long,
        complete: Boolean = false
    ): Notification {
        val progress = if (total > 0) (current * 100 / total).toInt() else 0
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(if (complete) "Download complete" else "$progress%")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(!complete)
            .setProgress(100, progress, false)
            .setOnlyAlertOnce(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "download_channel"
    }
}
