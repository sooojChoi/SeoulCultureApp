package com.example.seoulclutureapp.data

import com.example.seoulclutureapp.network.EventApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

interface AppContainer {
    val eventRepository: EventRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "http://openapi.seoul.go.kr:8088"


    private val json = Json {
        ignoreUnknownKeys = true
    }

    /**
     * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
     */
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()



    /**
     * Retrofit service object for creating api calls
     */
    private val retrofitService: EventApiService by lazy {
        retrofit.create(EventApiService::class.java)
    }

    /**
     * DI implementation for Mars photos repository
     */
    override val eventRepository: EventRepository by lazy {
        NetworkEventRepository(retrofitService)
    }
}