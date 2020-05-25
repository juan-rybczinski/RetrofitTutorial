package com.rybczinski.retrofittutorial.api.service

import com.rybczinski.retrofittutorial.api.model.GitHubRepo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GitHubClient {

    @GET("/users/{user}/repos")
    fun reposForUser(
        @Path("user") user: String
    ): Call<List<GitHubRepo>>

}