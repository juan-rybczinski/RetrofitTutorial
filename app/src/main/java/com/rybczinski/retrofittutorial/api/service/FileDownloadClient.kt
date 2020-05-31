package com.rybczinski.retrofittutorial.api.service

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface FileDownloadClient {
    @GET("fileDownloadUrl")
    fun downloadFile(): Call<ResponseBody>

    @GET
    fun downloadFile(
        @Url url: String
    ): Call<ResponseBody>

    @Streaming
    @GET
    fun downloadFileStream(
        @Url url: String
    ): Call<ResponseBody>
}