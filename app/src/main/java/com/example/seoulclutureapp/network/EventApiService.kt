package com.example.seoulclutureapp.network

import com.example.seoulclutureapp.model.EventResult
import com.example.seoulclutureapp.model.TotalEventInfo
import retrofit2.http.GET
import retrofit2.http.Path

interface EventApiService {
@GET("{apiKey}/json/culturalEventInfo/{startIndex}/{endIndex}")
suspend fun getEventResult(@Path("apiKey") apiKey:String,
                           @Path("startIndex") startIndex:Int,
                           @Path("endIndex") endIndex:Int): TotalEventInfo
}
