package com.example.vkdz2.data



class GiphyRepository(private val api: GiphyApi) {

    private val apiKey = "uRV3HbZG89DQDcHzjKFCf5VDDWmipeSV"
    suspend fun getTrendingGifs(offset: Int): Result<List<GifItem>> {
        return kotlin.runCatching {
            val response = api.getTrendingGifs(
                apiKey = apiKey,
                limit = 25,
                offset = offset
            )

            if (response.isSuccessful) {
                val gifs = response.body()?.data?.map { gif ->
                    GifItem(
                        id = gif.id,
                        title = gif.title,
                        url = gif.images.original.url,
                        width = gif.images.original.width.toIntOrNull() ?: 0,
                        height = gif.images.original.height.toIntOrNull() ?: 0
                    )
                } ?: emptyList()

                gifs
            } else {
                throw Exception("HTTP ${response.code()}: ${response.message()}")
            }
        }
    }
}