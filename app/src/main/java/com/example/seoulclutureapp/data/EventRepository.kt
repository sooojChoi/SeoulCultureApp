package com.example.seoulclutureapp.data

import android.util.Log
import com.example.seoulclutureapp.BuildConfig
import com.example.seoulclutureapp.R
import com.example.seoulclutureapp.model.Event
import com.example.seoulclutureapp.network.EventApiService
import kotlinx.coroutines.coroutineScope

interface EventRepository {
    suspend fun getEvents():List<Event>
}

class NetworkEventRepository(
    private val eventApiService: EventApiService
): EventRepository{
    override suspend fun getEvents(): List<Event> {
        val API_KEY  = BuildConfig.SEOUL_API_KEY

        var startIndex = 1
        var endIndex = 1000

        // 리스트를 초기화한다.
        val eventList:ArrayList<Event> = ArrayList()
        // 먼저 1000개의 데이터를 불러온다. (한 번에 1000개만 불러올 수 있다. )
        var totalEventInfo = eventApiService.getEventResult(API_KEY, startIndex, endIndex)

        // 불러온 데이터를 리스트에 추가한다.
        for(i in totalEventInfo.totalEventInfo.eventList){
            eventList.add(i)
        }

        // 총 몇 개의 데이터가 존재하는지 확인한다.
        var totalCount = totalEventInfo.totalEventInfo.totalCount ?: 0
        // 총 데이터 개수가 1000개가 넘는다면, 남은 데이터를 가져온다.
        if(totalCount > 1000){
            coroutineScope {
                while(endIndex!=totalCount){
                    startIndex += 1000
                    endIndex += 1000
                    if(endIndex>totalCount){
                        endIndex = totalCount
                    }
                    totalEventInfo = eventApiService.getEventResult(API_KEY, startIndex, endIndex)
                    for(i in totalEventInfo.totalEventInfo.eventList){
                        eventList.add(i)
                    }
                }
            }
        }


        return eventList
    }

}