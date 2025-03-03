package com.example.gymbuddy.networking

import okhttp3.Interceptor
import okhttp3.Response

class GeminiInterceptor(private val apiKey: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("x-goog-api-key", apiKey)  // Google API typically uses this header format
            .build()
        return chain.proceed(request)
    }
}