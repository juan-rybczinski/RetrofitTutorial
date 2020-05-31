package com.rybczinski.retrofittutorial.api.service

import com.rybczinski.retrofittutorial.api.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface UserClient {
    @POST("user")
    fun createAccount(@Body user: User): Call<User>

    @Multipart
    @POST("upload")
    fun uploadPhoto(
        @Part("description") description: RequestBody,
        @Part("photographer") photographer: RequestBody,
        @Part("year") year: RequestBody,
        @Part("location") location: RequestBody,
        @Part photo: MultipartBody.Part
    ): Call<ResponseBody>
}