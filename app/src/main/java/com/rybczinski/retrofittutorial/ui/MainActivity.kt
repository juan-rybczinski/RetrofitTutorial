package com.rybczinski.retrofittutorial.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rybczinski.retrofittutorial.BuildConfig
import com.rybczinski.retrofittutorial.R
import com.rybczinski.retrofittutorial.api.model.GitHubRepo
import com.rybczinski.retrofittutorial.api.model.User
import com.rybczinski.retrofittutorial.api.service.FileDownloadClient
import com.rybczinski.retrofittutorial.api.service.GitHubClient
import com.rybczinski.retrofittutorial.api.service.UserClient
import com.rybczinski.retrofittutorial.background.BackgroundService
import com.rybczinski.retrofittutorial.helpers.ErrorUtils
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.jetbrains.annotations.NotNull
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    companion object {
        val API_BASE_URL = "https://api.github.com"
        val LOCALHOST_EMUL = "10.0.2.2"
        val MY_PERMMISSIONS_REQUEST = 100

        val builder: Retrofit.Builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
        val retrofit = builder.build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            MY_PERMMISSIONS_REQUEST)
        }

//        getGitHubRepos()
//        sendUserPost()

        var downloadUrl = "downloadUrl"
        downloadFile(downloadUrl)
    }

    private fun executeSearch() {
        val apiKey = "super-secret"

        // Create retrofit instance
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor {
                val original = it.request()
                val httpUrl = original.url()

                val newHttpUrl = httpUrl.newBuilder().addQueryParameter("apikey", apiKey).build()

                val requestBuilder = original.newBuilder().url(newHttpUrl)

                val request = requestBuilder.build()
                it.proceed(request)
            }
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())

        val retrofit = builder.build()

        val userClient = retrofit.create(UserClient::class.java)

        val queryMap = mutableMapOf<String, Any>()
        queryMap["id"] = 4
        queryMap["order"] = "asc"

        val call = userClient.searchForUser(
            12,
            queryMap
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

    private fun executeSendMessage(message: String) {
        // Create retrofit instance
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())

        val retrofit = builder.build()

        val userClient = retrofit.create(UserClient::class.java)
        val body = RequestBody.create(
            MediaType.parse("text/plain"),
            message
        )
//        val call = userClient.sendMessage(message)
        val call = userClient.sendMessage(body)
        call.enqueue(object : Callback<String> {
            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(this@MainActivity, "no :(", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<String>, response: Response<String>) {
                Toast.makeText(this@MainActivity, response.body(), Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun executeSendFeedbackForm(name: String, email: String, age: String, topics: String) {
        val userClient = retrofit.create(UserClient::class.java)

        /*
        val call = userClient.sendUserFeedback(
            name,
            email,
            age,
            topics.split(",")
        )
         */

        val map = mutableMapOf<String, String>()
        map["name"] = name
        map["email"] = email
        map["age"] = age
        val call = userClient.sendUserFeedback(
            map,
            topics.split(",")
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

    private fun executeGetUserRequest(user: String) {
        val userClient = retrofit.create(UserClient::class.java)

        val call = userClient.getUserByName(user)

        call.enqueue(object : Callback<User> {
            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@MainActivity, "no :(", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@MainActivity,
                        "server returned user: ${response.body()?.name}",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    /*
                    when (response.code()) {
                        404 -> {
                            Toast.makeText(
                                this@MainActivity,
                                "server returned error: user not found",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        500 -> {
                            Toast.makeText(
                                this@MainActivity,
                                "server returned error: server is broken",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        else -> {
                            Toast.makeText(
                                this@MainActivity,
                                "server returned error: unknown error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                     */
                    /*
                    Toast.makeText(
                        this@MainActivity,
                        "server returned error: ${response.errorBody().toString()}",
                        Toast.LENGTH_SHORT
                    ).show()
                     */
                    val apiError = ErrorUtils.parseError(response)
                    Toast.makeText(
                        this@MainActivity,
                        apiError?.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        })
    }

    private fun downloadFile(url: String) {
        // Create retrofit instance
        val builder = Retrofit.Builder()
            .baseUrl(API_BASE_URL)

        val retrofit = builder.build()

        val client = retrofit.create(FileDownloadClient::class.java)
        val call = client.downloadFileStream(
            url
        )
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@MainActivity, "no :(", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                object : AsyncTask<Void, Void, Void>() {
                    override fun doInBackground(vararg params: Void?): Void? {
                        val success = response.body()?.let { writeResponseToDisk(it) } ?: false
                        return null
                    }
                }.execute()
            }

        })
    }

    private fun writeResponseToDisk(body: ResponseBody): Boolean {
        try {
            val downloadedFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                "output.jpg"
            )
            var istream: InputStream? = null
            var ostream: OutputStream? = null
            try {
                val fileReader = ByteArray(4096)
                val fileSize = body.contentLength()
                var fileSizeDownloaded = 0

                istream = body.byteStream()
                ostream = FileOutputStream(downloadedFile)

                while (true) {
                    val read = istream.read(fileReader)
                    if (read == -1) {
                        break
                    }
                    ostream.write(fileReader, 0, read)
                    fileSizeDownloaded += read
                    Log.d(TAG, "file downloaded: $fileSizeDownloaded of $fileSize")
                }
                ostream.flush()
                return true
            } catch (e: IOException) {
                return false
            } finally {
                istream?.close()
                ostream?.close()
            }
        } catch (e: IOException) {
            return false
        }

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

    class UserLoginTask(private val mEmail: String, private val mPassword: String) :
        AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            val userClient = retrofit.create(UserClient::class.java)

            val userName = mEmail
            val password = mPassword
            val base = "$userName : $password"
            val authHeader = "Basic ${Base64.encodeToString(base.toByteArray(), Base64.NO_WRAP)}"
            val call = userClient.getUser(authHeader)

            try {
                val response = call.execute()
                if (response.isSuccessful) {
                    return true
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return false
        }

    }
}
