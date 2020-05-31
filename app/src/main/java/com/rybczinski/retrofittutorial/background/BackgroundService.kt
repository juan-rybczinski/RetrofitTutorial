package com.rybczinski.retrofittutorial.background

import android.app.IntentService
import android.content.Intent
import android.util.Log
import com.rybczinski.retrofittutorial.api.model.User
import com.rybczinski.retrofittutorial.api.service.UserClient
import com.rybczinski.retrofittutorial.ui.MainActivity
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class BackgroundService : IntentService("BackgroundService") {

    override fun onHandleIntent(intent: Intent?) {
        val user = User("Rybczinski", "jhreplay.lee@gmail.com", 34, arrayOf("Android", "Kotlin"))

        val builder = Retrofit.Builder()
            .baseUrl(MainActivity.API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val retrofit = builder.build()

        // Get client & call object for the request
        val client = retrofit.create(UserClient::class.java)
        val call = client.createAccount("Rybczinski Header", user)

        try {
            val result = call.execute()
            Log.i("Retrofit", "Success!")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("Retrofit", "Failure!")
        }
    }
}
