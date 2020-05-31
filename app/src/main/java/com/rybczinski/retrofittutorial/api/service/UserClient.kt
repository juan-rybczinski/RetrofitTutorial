package com.rybczinski.retrofittutorial.api.service

import com.rybczinski.retrofittutorial.api.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface UserClient {
    @POST("user")
    fun createAccount(@Body user: User): Call<User>

    @Multipart
    @POST("upload")
    fun uploadPhoto(
        @PartMap data: Map<String, RequestBody>,
        @Part photo: MultipartBody.Part
    ): Call<ResponseBody>

    @Multipart
    @POST("upload")
    fun uploadPhotos(
        @Part profile: MultipartBody.Part,
        @Part panorama: MultipartBody.Part
    ): Call<ResponseBody>
}