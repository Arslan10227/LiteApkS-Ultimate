package com.arsla.liteapksclone.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class V2PostsResponse(
    val status: Int,
    val message: String,
    val data: PostsData
)

@Serializable
data class V2PostResponse(
    val status: Int,
    val message: String,
    val data: PostDto
)

@Serializable
data class V2HomeResponse(
    val status: Int,
    val message: String,
    val data: HomeData
)

@Serializable
data class V2CategoriesResponse(
    val status: Int,
    val message: String,
    val data: CategoriesData
)

@Serializable
data class V2UpdateResponse(
    val status: Int,
    val message: String,
    val data: UpdateData
)

@Serializable
data class V2SearchResponse(
    val status: Int,
    val message: String,
    val data: List<PostDto>? = null
)

@Serializable
data class PostsData(
    val posts: List<PostDto>,
    val limit: Int,
    val page: Int
)

@Serializable
data class HomeData(
    val sliders: List<SliderItemDto>,
    val tabs: List<TabSectionDto>
)

@Serializable
data class CategoriesData(
    val list: List<CategoryDto>,
    val count: Int
)

@Serializable
data class PostDto(
    val id: Int,
    val title: String,
    val content: String,
    val status: String,
    val images: ImageSetDto,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    val publisher: String,
    val genre: String,
    val size: String,
    val type: String,
    @SerialName("mod_info") val modInfo: String,
    @SerialName("lastest_version") val latestVersion: String,
    @SerialName("rating_avg") val ratingAvg: Double,
    @SerialName("rating_count") val ratingCount: Int,
    @SerialName("post_views") val postViews: Int,
    @SerialName("original_download_url") val originalDownloadUrl: String,
    @SerialName("mod_features") val modFeatures: String,
    val downloads: List<String>,
    val tabs: List<TabDto>,
    val banner: String? = null,
    val name: String,
    val versions: List<VersionDto> = emptyList(),
    val gallery: List<String> = emptyList()
)

@Serializable
data class ImageSetDto(
    val thumbnail: String,
    val image: String
)

@Serializable
data class TabDto(
    val title: String,
    val content: String
)

@Serializable
data class VersionDto(
    val version: String,
    @SerialName("version_note") val versionNote: String,
    @SerialName("version_downloads") val versionDownloads: List<VersionDownloadDto>,
    @SerialName("download_push_noti-box") val downloadPushNotiBox: DownloadPushNotiBox? = null
)

@Serializable
data class VersionDownloadDto(
    val recommended: Boolean = false,
    @SerialName("version_download_type") val downloadType: String,
    @SerialName("version_download_size") val downloadSize: String,
    @SerialName("version_download_link") val downloadLink: String,
    @SerialName("version_download_note") val downloadNote: String = "",
    val abi: String,
    val role: String
)

@Serializable
data class DownloadPushNotiBox(
    @SerialName("download_push_noti") val downloadPushNoti: Boolean = false,
    @SerialName("download_push_noti_to_all_user") val toAll: Boolean = false
)

@Serializable
data class SliderItemDto(
    val banner: String,
    val thumbnail: String,
    val title: String,
    val category: String,
    val id: Int,
    val type: String
)

@Serializable
data class TabSectionDto(
    val title: String,
    val content: JsonElement? = null
)

@Serializable
data class CategoryDto(
    @SerialName("term_id") val termId: Int,
    val name: String,
    val slug: String,
    @SerialName("term_group") val termGroup: Int = 0,
    @SerialName("term_taxonomy_id") val termTaxonomyId: Int,
    val taxonomy: String,
    val description: String = "",
    val parent: Int,
    val count: Int,
    val children: List<CategoryDto> = emptyList(),
    @SerialName("child_ids") val childIds: List<Int> = emptyList()
)

@Serializable
data class UpdateData(
    @SerialName("latest_version") val latestVersion: String,
    @SerialName("force_update") val forceUpdate: Boolean,
    @SerialName("download_url") val downloadUrl: String,
    val dev: String
)
