package com.rybczinski.retrofittutorial.api.service

import com.rybczinski.retrofittutorial.api.model.AccessToken
import com.rybczinski.retrofittutorial.api.model.GitHubRepo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface GitHubClient {

    @GET("/users/{user}/repos")
    fun reposForUser(
        @Path("user") user: String
    ): Call<List<GitHubRepo>>

    @GET
    fun getUserProfilePhoto(
        @Url url: String
    ): Call<ResponseBody>

    @Headers("Accept: application/json")
    @POST("login/oauth/access_token")
    @FormUrlEncoded
    fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Call<AccessToken>

}