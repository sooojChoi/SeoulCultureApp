package com.example.seoulclutureapp.data

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.seoulclutureapp.BuildConfig
import com.example.seoulclutureapp.R
import com.example.seoulclutureapp.model.Event
import com.example.seoulclutureapp.network.EventApiService
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.time.LocalDate

interface EventRepository {
    var totalEvents: List<Event>
    var todaysEvents: List<Event>
    suspend fun getEvents()

    fun getAllLikeEvent(): Flow<List<LikeEvent>>

    fun getLikeEventByTitleAndDate(title: String, date:String): Flow<LikeEvent>

    suspend fun insertLikeEvent(event: LikeEvent)

    suspend fun updateLikeEvent(event: LikeEvent)

    suspend fun deleteLikeEvent(event: LikeEvent)
}

class NetworkEventRepository(
    private val eventApiService: EventApiService,
    private val eventDao: EventDao
): EventRepository{
    override var totalEvents: List<Event> = listOf()
    override var todaysEvents: List<Event> = listOf()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getEvents() {
        val API_KEY  = BuildConfig.SEOUL_API_KEY

        var startIndex = 1
        var endIndex = 1000

        // 리스트를 초기화한다.
        val eventList:ArrayList<Event> = ArrayList()
        val todaysList:ArrayList<Event> = ArrayList()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val todayDate = sdf.parse(LocalDate.now().toString())

        // 먼저 1000개의 데이터를 불러온다. (한 번에 1000개만 불러올 수 있다. )
        var totalEventInfo = eventApiService.getEventResult(API_KEY, startIndex, endIndex)

        // 불러온 데이터를 리스트에 추가한다.
        for(i in totalEventInfo.totalEventInfo.eventList){
            eventList.add(i)
            // 시작 날짜가 오늘이라면, 오늘의 행사에 추가한다.
            val startDate = sdf.parse(i.startDate.substring(0, 10))

            if(todaysList.size<10 && todayDate.compareTo(startDate)==0){
                todaysList.add(i)
            }
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

                        // 시작 날짜가 오늘이라면, 오늘의 행사에 추가한다.
                        val startDate = sdf.parse(i.startDate.substring(0, 10))

                        if(todaysList.size<10 && todayDate.compareTo(startDate)==0){
                            todaysList.add(i)
                        }
                    }
                }
            }
        }

        totalEvents = eventList
        todaysEvents = todaysList

    }

    override fun getAllLikeEvent(): Flow<List<LikeEvent>> = eventDao.getAllEvents()

    override fun getLikeEventByTitleAndDate(title: String, date: String): Flow<LikeEvent>
    = eventDao.getEventByTitleAndDate(title, date)

    override suspend fun insertLikeEvent(event: LikeEvent) = eventDao.insert(event)

    override suspend fun updateLikeEvent(event: LikeEvent) = eventDao.update(event)

    override suspend fun deleteLikeEvent(event: LikeEvent) = eventDao.delete(event)

}