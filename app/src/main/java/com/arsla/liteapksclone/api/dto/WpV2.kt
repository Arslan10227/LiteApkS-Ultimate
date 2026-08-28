package com.arsla.liteapksclone.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WpCommentDto(
    val id: Int,
    val post: Int,
    val author: Int? = null,
    @SerialName("author_name") val authorName: String,
    @SerialName("author_email") val authorEmail: String? = null,
    val date: String,
    val content: RenderedContent
)

@Serializable
data class RenderedContent(
    val rendered: String
)

@Serializable
data class WpMediaDto(
    val id: Int,
    @SerialName("source_url") val sourceUrl: String,
    @SerialName("media_details") val mediaDetails: MediaDetails? = null
)

@Serializable
data class MediaDetails(
    val width: Int? = null,
    val height: Int? = null
)
