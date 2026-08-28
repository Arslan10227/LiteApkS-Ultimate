package com.arsla.liteapksclone.data.repository

import com.arsla.liteapksclone.api.dto.GitHubReleaseDto
import com.arsla.liteapksclone.error.ErrorHandler
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

data class UpdateInfo(
    val currentVersion: String,
    val latestVersion: String,
    val changelog: String,
    val downloadUrl: String,
    val isAvailable: Boolean
)

@Singleton
class UpdateRepository @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json
) {
    private val url = "https://api.github.com/repos/Arslan10227/LiteApkS-Ultimate/releases/latest"

    suspend fun getLatestUpdate(currentVersion: String): UpdateInfo {
        val request = Request.Builder()
            .url(url)
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("GitHub API error: ${response.code}")
                }
                val body = response.body?.string()
                    ?: throw IOException("Empty GitHub response")
                val release = json.decodeFromString(GitHubReleaseDto.serializer(), body)
                val latest = release.tagName.removePrefix("v").removePrefix("V")
                val apkUrl = release.assets
                    .firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
                    ?.browserDownloadUrl
                    ?: "https://github.com/Arslan10227/LiteApkS-Ultimate/releases/tag/${release.tagName}"

                UpdateInfo(
                    currentVersion = currentVersion,
                    latestVersion = latest,
                    changelog = release.body?.trim() ?: "No changelog provided.",
                    downloadUrl = apkUrl,
                    isAvailable = latest != currentVersion
                )
            }
        } catch (e: Exception) {
            ErrorHandler.handle(e, "UpdateRepository")
            throw e
        }
    }
}
