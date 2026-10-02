package es.unex.natureconnect.network


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton entry point that builds and exposes the Retrofit service.
 *
 * Provides a shared [natureconectAPI] instance configured with Gson and OkHttp,
 * using generous timeouts to tolerate slow uploads.
 */
object RetrofitClient {
    private const val BASE_URL = "http://192.168.1.44:8080/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)  
        .build()

    /** Service used by every repository to talk to the NatureConnect backend. */
    val api: natureconectAPI = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
        .create(natureconectAPI::class.java)


}