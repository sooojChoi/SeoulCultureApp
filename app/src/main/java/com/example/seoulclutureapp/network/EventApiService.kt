package com.example.seoulclutureapp.network

import com.example.seoulclutureapp.model.EventResult
import com.example.seoulclutureapp.model.TotalEventInfo
import retrofit2.http.GET
import retrofit2.http.Path

interface EventApiService {
//    @GET("594d684d4f636873373442565a484c/json/culturalEventInfo/1/5/ / /2024-01-01/")
//suspend fun getEventList(
//
//) : List<Event>

@GET("{apiKey}/json/culturalEventInfo/1/5")
suspend fun getEventResult(@Path("apiKey") apiKey:String): TotalEventInfo
}
