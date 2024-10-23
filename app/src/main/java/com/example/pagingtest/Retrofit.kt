package com.example.pagingtest

import android.util.Log
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://api.openalex.org/"

private val client = OkHttpClient.Builder()
    .addInterceptor {
        val request = it.request()
        Log.d("network_log", "request: $request")

        val response = it.proceed(request)
        Log.d("network_log", "response: $response")

        return@addInterceptor response
    }
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .addConverterFactory(ScalarsConverterFactory.create())
    .build()

val service = retrofit.create(DataService::class.java)

interface DataService {

    @GET("/works")
    suspend fun getList(
        @Query("filter")
        filter: String = "publication_year:2023",
        @Query("per-page")
        perPage: Int = 10,
        @Query("cursor")
        cursor: String = "*"
    ): ResponseDto
}

data class ResponseDto(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("results") val results: List<DataDto>
) {
    data class Meta(
        @SerializedName("next_cursor") val nextCursor: String,
    )

    data class DataDto(
        @SerializedName("id") val id: String,
        @SerializedName("display_name") val displayName: String
    )
}