package com.example.vkdz2

import androidx.lifecycle.ViewModel
import com.example.vkdz2.data.GiphyApi
import com.example.vkdz2.data.GiphyRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object AppModule {

    private const val BASE_URL = "https://api.giphy.com/v1/"
    const val API_KEY = "YOUR_API_KEY_HERE"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(GiphyApi::class.java)


    val repository: GiphyRepository by lazy {
        GiphyRepository(api)
    }
}