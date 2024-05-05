package com.example.seoulclutureapp.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.PrimaryKey
import com.example.seoulclutureapp.data.EventRepository
import com.example.seoulclutureapp.data.LikeEvent
import com.example.seoulclutureapp.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class EventDetailsUiState(
    val todayEvent:Event?,
    val isLike:Boolean=false
)

fun Event.toLikeEvent(): LikeEvent = LikeEvent(
    title, date, classification, borough, place, orgName, useTarget, useFee, link, imgSrc, longitude, latitude
)
fun LikeEvent.toEvent():Event = Event(
    title=title,
    date=date,
    classification = classification,
    borough = borough,
    place = place,
    orgName = orgName,
    useTarget = useTarget,
    useFee = useFee,
    link = link,
    imgSrc = imgSrc,
    longitude = longitude,
    latitude = latitude,
    isFree = isFree,
    registerDate = "",
    startDate = date.substring(0,10),
    endDate = date.substring(11, date.length)
)
@RequiresApi(Build.VERSION_CODES.O)
class EventDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val eventRepository: EventRepository
): ViewModel() {
    val eventId: Int? = savedStateHandle[EventDetailDestination.eventIdArg]
    val todayEvent = eventRepository.todaysEvents[eventId ?: 0]

   // private var _uiState = MutableStateFlow<EventDetailsUiState>(EventDetailsUiState(null))
    val uiState: StateFlow<EventDetailsUiState>
    = eventRepository.getAllLikeEvent().filterNotNull().map {
       var isLike = false
       for(e in it){
           if(e.title == todayEvent.title && e.date == todayEvent.date){
               isLike = true
               break
           }
       }
       EventDetailsUiState(todayEvent = todayEvent,
           isLike = isLike)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = EventDetailsUiState(todayEvent, false)
    )


    suspend fun updateEventLike(){
        if(uiState.value.isLike){
            eventRepository.deleteLikeEvent(uiState.value.todayEvent!!.toLikeEvent())
        }
        else{
            eventRepository.insertLikeEvent(uiState.value.todayEvent!!.toLikeEvent())
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}