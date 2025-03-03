package com.example.gymbuddy.networking

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import com.example.gymbuddy.BuildConfig
import okhttp3.OkHttpClient


object RetrofitInstance {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(GeminiInterceptor(BuildConfig.GEMINI_API_KEY))
            .build()
    }

    val api: GeminiApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GeminiApi::class.java)
    }
}