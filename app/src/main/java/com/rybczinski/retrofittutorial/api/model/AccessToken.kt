package com.rybczinski.retrofittutorial.api.model

import com.google.gson.annotations.SerializedName

class AccessToken(
    @SerializedName("access_token") private val accessToken: String,
    @SerializedName("token_type") private val tokenType: String
)