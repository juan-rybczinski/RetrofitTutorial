package com.rybczinski.retrofittutorial.api.service

import com.rybczinski.retrofittutorial.api.model.Task
import retrofit2.Call
import retrofit2.http.GET

interface GithubGistService {
    @GET("api_uri")
    fun getGithubGist(): Call<Task>
}