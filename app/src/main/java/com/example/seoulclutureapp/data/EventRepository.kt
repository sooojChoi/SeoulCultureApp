package com.example.seoulclutureapp.data

import android.util.Log
import com.example.seoulclutureapp.BuildConfig
import com.example.seoulclutureapp.R
import com.example.seoulclutureapp.model.Event
import com.example.seoulclutureapp.network.EventApiService

interface EventRepository {
    suspend fun getEvents():List<Event>
}

class NetworkEventRepository(
    private val eventApiService: EventApiService
): EventRepository{
    override suspend fun getEvents(): List<Event> {
        val API_KEY  = BuildConfig.SEOUL_API_KEY
        return eventApiService.getEventResult(API_KEY).totalEventInfo.eventList
    }

}