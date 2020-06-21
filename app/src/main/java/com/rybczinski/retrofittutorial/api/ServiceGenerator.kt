package com.rybczinski.retrofittutorial.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ServiceGenerator {
    companion object {
        private const val API_BASE_URL = "https://api.github.com"

        private var builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        private var retrofit = builder.build()

        private val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        private val httpClientBuilder = OkHttpClient.Builder()

        fun <T> createService(serviceClass: Class<T>): T {
            if (!httpClientBuilder.interceptors().contains(loggingInterceptor)) {
                httpClientBuilder.addInterceptor(loggingInterceptor)
                builder = builder.client(httpClientBuilder.build())
                retrofit = builder.build()
            }

            return retrofit.create(serviceClass)
        }
    }
}