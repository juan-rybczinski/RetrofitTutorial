package com.rybczinski.retrofittutorial.api.service

import com.rybczinski.retrofittutorial.api.model.Login
import com.rybczinski.retrofittutorial.api.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserClient {
    @Headers(
        "Cache-Controls: max-age=3600",
        "User-Agent: Android"
    )
    @POST("user")
    fun createAccount(
        @Header("UserName") userName: String,
        @Body user: User
    ): Call<User>

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

    @Multipart
    @POST("upload")
    fun uploadAlbum(
        @Part("description") description: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Call<ResponseBody>

    @GET("user/{user}")
    fun getUserByName(
        @Path("user") user: String
    ): Call<User>

    @FormUrlEncoded
    @POST("feedback/")
    fun sendUserFeedback(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("age") age: String,
        @Field("topics") topics: List<String>
    ): Call<ResponseBody>

    @FormUrlEncoded
    @POST("feedback/")
    fun sendUserFeedback(
        @FieldMap map: Map<String, String>,
        @Field("topics") topics: List<String>
    ): Call<ResponseBody>

    @POST("message")
    fun sendMessage(@Body message: String): Call<String>

    @POST("message")
    fun sendMessage(@Body message: RequestBody): Call<String>

    @GET("user")
    fun searchForUser(
        @Query("id") id: Int,
        @Query("sort") order: String,
        @Query("page") page: Int
    ): Call<ResponseBody>

    @GET("user")
    fun searchForUser(
        @Query("id") id: Int,
        @QueryMap queries: Map<String, Any>
    ): Call<ResponseBody>

    @GET("basic")
    fun getUser(@Header("Authorization") authHeader: String): Call<User>

    @POST("login")
    fun login(@Body login: Login): Call<User>

    @GET("secretinfo")
    fun getSecret(@Header("Authorization") authToken: String): Call<ResponseBody>

    @GET("users")
    fun searchForUsers(
        @Query("id")id: Int,
        @Query("order")order: String,
        @Query("page")page: Int
    ): Call<ResponseBody>

    @GET("users")
    fun searchForUsers(
        @Query("id")id: Int?,
        @Query("order")order: String?,
        @Query("page")page: Int?
    ): Call<ResponseBody>

    @GET("users")
    fun searchForUsers(
        @Query("id")id: List<Int>,
        @Query("order")order: String?,
        @Query("page")page: Int?
    ): Call<ResponseBody>

    @GET("users")
    fun searchForUsers(
        @Query("id")id: Int,
        @QueryMap map: Map<String, Any>
    ): Call<ResponseBody>
}