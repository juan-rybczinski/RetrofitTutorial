package com.rybczinski.retrofittutorial.api.service

import com.rybczinski.retrofittutorial.api.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserClient {
    @POST("user")
    fun createAccount(@Body user: User): Call<User>
}