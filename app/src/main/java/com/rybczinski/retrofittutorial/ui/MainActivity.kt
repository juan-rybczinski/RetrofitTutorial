package com.rybczinski.retrofittutorial.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.rybczinski.retrofittutorial.R
import com.rybczinski.retrofittutorial.api.model.GitHubRepo
import com.rybczinski.retrofittutorial.api.service.GitHubClient
import okhttp3.OkHttpClient
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private val API_BASE_URL = "https://api.github.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getGitHubRepos()
    }

    private fun getGitHubRepos() {
        val httpClient = OkHttpClient.Builder()
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )

        val retrofit = builder.client(
            httpClient.build()
        ).build()

        val client = retrofit.create(GitHubClient::class.java)

        val call = client.reposForUser("juan-rybczinski")
        call.enqueue(object : Callback<List<GitHubRepo>> {
            override fun onFailure(call: Call<List<GitHubRepo>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "error :(", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<List<GitHubRepo>>,
                response: Response<List<GitHubRepo>>
            ) {
                Toast.makeText(this@MainActivity, "success XD", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
