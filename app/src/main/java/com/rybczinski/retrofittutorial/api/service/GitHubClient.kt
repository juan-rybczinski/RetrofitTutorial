package com.rybczinski.retrofittutorial.api.service

import com.rybczinski.retrofittutorial.api.model.GitHubRepo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url

interface GitHubClient {

    @GET("/users/{user}/repos")
    fun reposForUser(
        @Path("user") user: String
    ): Call<List<GitHubRepo>>

    @GET
    fun getUserProfilePhoto(
        @Url url: String
    ): Call<ResponseBody>

}