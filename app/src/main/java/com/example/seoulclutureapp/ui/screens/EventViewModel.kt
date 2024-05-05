package com.example.seoulclutureapp.ui.screens

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.seoulclutureapp.EventApplication
import com.example.seoulclutureapp.data.EventRepository
import com.example.seoulclutureapp.model.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Date

sealed interface EventUiState {
    data class Success(val events: List<Event>, val todaysEvent:List<Event>) : EventUiState
    object Error : EventUiState
    object Loading : EventUiState
}

@RequiresApi(Build.VERSION_CODES.O)
class EventViewModel(savedStateHandle: SavedStateHandle,
                     private val eventRepository: EventRepository):ViewModel() {
    private var _uiState = MutableStateFlow<EventUiState>(EventUiState.Loading)
    val uiState: StateFlow<EventUiState> = _uiState.asStateFlow()

    init {
        getEvents()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getEvents() {
        viewModelScope.launch {
            _uiState.update {
                try {
                    eventRepository.getEvents()
                    val totalEvents = eventRepository.totalEvents
                    val todaysEvent = eventRepository.todaysEvents

                    EventUiState.Success(totalEvents, todaysEvent)
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

}