package com.rybczinski.retrofittutorial.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.widget.EditText
import android.widget.Toast
import androidx.core.net.toUri
import com.rybczinski.retrofittutorial.BuildConfig
import com.rybczinski.retrofittutorial.R
import com.rybczinski.retrofittutorial.api.model.GitHubRepo
import com.rybczinski.retrofittutorial.api.model.User
import com.rybczinski.retrofittutorial.api.service.GitHubClient
import com.rybczinski.retrofittutorial.api.service.UserClient
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MainActivity : AppCompatActivity() {

    private val API_BASE_URL = "https://api.github.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        getGitHubRepos()
//        sendUserPost()
    }

    private fun uploadFile(fileUri: Uri) {
        val description = "description"

        val descriptionPart = RequestBody.create(MultipartBody.FORM, description)

        val originalFile = File(fileUri.toString())
        val filePart = RequestBody.create(
            MediaType.parse(contentResolver.getType(fileUri)),
            originalFile
        )

        val file = MultipartBody.Part.createFormData("photo", originalFile.name, filePart)

        // Create retrofit instance
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val retrofit = builder.build()

        // Get client & call object for the request
        val client = retrofit.create(UserClient::class.java)

        val call = client.uploadPhoto(descriptionPart, file)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "no :(", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(this@MainActivity, "yeah XD", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendUserPost() {
        val user = User("Rybczinski", "jhreplay.lee@gmail.com", 34, arrayOf("Android", "Kotlin"))
        sendNetworkRequest(user)
    }

    private fun sendNetworkRequest(user: User) {
        // create OkHttp client
        val okHttpClientBuilder = OkHttpClient.Builder()

        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

        if (BuildConfig.DEBUG) {
            okHttpClientBuilder.addInterceptor(logging)
        }

        // Create Retrofit instance
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClientBuilder.build())

        val retrofit = builder.build()

        // Get client & call object for the request
        val client = retrofit.create(UserClient::class.java)
        val call = client.createAccount(user)
        call.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@MainActivity, "error :(", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                Toast.makeText(this@MainActivity, "User ID: ${response.body()?.id}", Toast.LENGTH_SHORT).show()
            }

        })
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
