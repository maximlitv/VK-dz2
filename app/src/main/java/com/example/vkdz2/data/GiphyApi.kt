package com.example.vkdz2.data

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response


interface GiphyApi {

    @GET("gifs/trending")
    suspend fun getTrendingGifs(
        @Query("api_key") apiKey: String,
        @Query("limit") limit: Int = 25,
        @Query("offset") offset: Int = 0
    ): Response<GiphyResponse>
}

data class GiphyResponse(
    val data: List<GiphyGif>
)

data class GiphyGif(
    val id: String,
    val title: String,
    val images: GiphyImages
)

data class GiphyImages(
    val original: GiphyImage
)

data class GiphyImage(
    val url: String,
    val width: String,
    val height: String
)