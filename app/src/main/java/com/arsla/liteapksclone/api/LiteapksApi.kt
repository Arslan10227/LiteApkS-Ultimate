package com.arsla.liteapksclone.api

import com.arsla.liteapksclone.api.dto.V2CategoriesResponse
import com.arsla.liteapksclone.api.dto.V2HomeResponse
import com.arsla.liteapksclone.api.dto.V2PostResponse
import com.arsla.liteapksclone.api.dto.V2PostsResponse
import com.arsla.liteapksclone.api.dto.V2SearchResponse
import com.arsla.liteapksclone.api.dto.V2UpdateResponse
import com.arsla.liteapksclone.api.dto.WpCommentDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface LiteapksApi {

    @GET("/wp-json/v2/home")
    suspend fun getHome(): V2HomeResponse

    @GET("/wp-json/v2/posts")
    suspend fun getPosts(
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): V2PostsResponse

    @GET("/wp-json/v2/posts/{id}")
    suspend fun getPost(@Path("id") id: Int): V2PostResponse

    @GET("/wp-json/v2/categories")
    suspend fun getCategories(): V2CategoriesResponse

    @GET("/wp-json/v2/get_latest_version")
    suspend fun getLatestVersion(): V2UpdateResponse

    @GET("/wp-json/v2/search")
    suspend fun search(
        @Query("search") query: String,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): V2SearchResponse

    @GET("/wp-json/wp/v2/comments")
    suspend fun getComments(
        @Query("post") postId: Int,
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): List<WpCommentDto>
}
