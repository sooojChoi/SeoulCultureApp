package com.example.seoulclutureapp.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.seoulclutureapp.EventApplication
import com.example.seoulclutureapp.data.EventRepository
import com.example.seoulclutureapp.model.Event
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

sealed interface EventUiState {
    data class Success(val events: List<Event>) : EventUiState
    object Error : EventUiState
    object Loading : EventUiState
}

@RequiresApi(Build.VERSION_CODES.O)
class EventViewModel(private val eventRepository: EventRepository):ViewModel() {
    var eventUiState: EventUiState by mutableStateOf(EventUiState.Loading)
        private set

    var todaysEvent:List<Event> = listOf()
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getEvents()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getEvents() {
        viewModelScope.launch {
            eventUiState = EventUiState.Loading
            eventUiState = try {
                val events = eventRepository.getEvents()
                val sdf = SimpleDateFormat("yyyy-MM-dd")
                sdf.parse(LocalDate.now().toString())?.let { todaysEvent = getEventsByDate(it, events) }
                EventUiState.Success(events)
            } catch (e: IOException) {
                Log.e("RetrofitError",e.toString())
                EventUiState.Error

            } catch (e: HttpException) {
                Log.e("RetrofitError",e.toString())
                EventUiState.Error
            } catch (e: Exception){
                Log.e("RetrofitError",e.toString())
                EventUiState.Error
            }
        }
    }

    // 특정 날짜가 행사 기간에 포함되는 event 의 리스트를 반환한다.
    fun getEventsByDate(date: Date, events: List<Event>): List<Event>{
        val list = ArrayList<Event>()
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        for(event in events){
            val startDate = sdf.parse(event.startDate.substring(0, 10))
            val endDate = sdf.parse(event.endDate.substring(0,10))

            if(date.compareTo(startDate)>=0 && date.compareTo(endDate) <= 0){
                list.add(event)
            }
        }
        return list
    }

    /**
     * Factory for [MarsViewModel] that takes [MarsPhotosRepository] as a dependency
     */
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[APPLICATION_KEY] as EventApplication)
                val eventRepository = application.container.eventRepository
                EventViewModel(eventRepository = eventRepository)
            }
        }
    }
}