package com.rybczinski.retrofittutorial.ui

import android.content.Intent
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
import com.rybczinski.retrofittutorial.background.BackgroundService
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.NotNull
import retrofit2.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOError
import java.io.IOException

class MainActivity : AppCompatActivity() {

    companion object {
        val API_BASE_URL = "https://api.github.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        getGitHubRepos()
//        sendUserPost()
    }

    @NotNull
    private fun createPartFromString(string: String): RequestBody {
        return RequestBody.create(
            MultipartBody.FORM, string
        )
    }

    @NotNull
    private fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part {
        val file = File(fileUri.toString())

        val requestFile = RequestBody.create(
            MediaType.parse(contentResolver.getType(fileUri)),
            file
        )

        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    private fun uploadFile(filePath: String) {
        val description: String? = "description"
        val photographer: String? = "photographer"
        val year: String? = "year"
        val location: String? = "location"

        // Create retrofit instance
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val retrofit = builder.build()

        // Get client & call object for the request
        val client = retrofit.create(UserClient::class.java)

        val partMap = mutableMapOf<String, RequestBody>()
        description?.let { partMap["description"] = createPartFromString(description) }
        photographer?.let { partMap["photographer"] = createPartFromString(photographer) }
        year?.let { partMap["year"] = createPartFromString(year) }
        location?.let { partMap["location"] = createPartFromString(location) }

        val call = client.uploadPhoto(
            partMap,
            prepareFilePart("photo", Uri.parse(filePath)))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "no :(", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(this@MainActivity, "yeah XD", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadFiles(filePathProfile: String, filePathPanorama: String) {
        // Create retrofit instance
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val retrofit = builder.build()

        // Get client & call object for the request
        val client = retrofit.create(UserClient::class.java)

        val call = client.uploadPhotos(
            prepareFilePart("profile", Uri.parse(filePathProfile)),
            prepareFilePart("panorama", Uri.parse(filePathPanorama)))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "no :(", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(this@MainActivity, "yeah XD", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadAlbum(fileUris: List<Uri>) {
        val description: String = "description"

        // Create retrofit instance
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val retrofit = builder.build()

        // Get client & call object for the request
        val client = retrofit.create(UserClient::class.java)

        val parts = mutableListOf<MultipartBody.Part>()
        fileUris.forEachIndexed { index, uri ->
            parts.add(prepareFilePart(""+index, uri))
        }

        val call = client.uploadAlbum(
            createPartFromString(description),
            parts
        )

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

        okHttpClientBuilder.addInterceptor { chain ->
            val request = chain.request()
            val newRequest = request.newBuilder().header("Authentication", "secret-key")
            chain.proceed(newRequest.build())
        }

        // Create Retrofit instance
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClientBuilder.build())

        val retrofit = builder.build()

        // Get client & call object for the request
        val client = retrofit.create(UserClient::class.java)
        val call = client.createAccount("Rybczinski Header", user)

        // Asynchronous Request
//        call.enqueue(object : Callback<User> {
//            override fun onFailure(call: Call<User>, t: Throwable) {
//                Toast.makeText(this@MainActivity, "error :(", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onResponse(call: Call<User>, response: Response<User>) {
//                Toast.makeText(this@MainActivity, "User ID: ${response.body()?.id}", Toast.LENGTH_SHORT).show()
//            }
//
//        })

        // Synchronous Request
        val intent = Intent(this, BackgroundService::class.java)
        startService(intent)
    }

    private fun getDynamicUrl(user: String) {
        // Create retrofit instance
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())

        val retrofit = builder.build()

        val profilePhoto = "url"

        // Get client & call object for the request
        val client = retrofit.create(GitHubClient::class.java)
        val call = client.getUserProfilePhoto(profilePhoto)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "error :(", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Toast.makeText(this@MainActivity, "success XD", Toast.LENGTH_SHORT).show()
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
