package com.example.seoulclutureapp.ui.screens

import android.util.Log
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

sealed interface EventUiState {
    data class Success(val events: List<Event>) : EventUiState
    object Error : EventUiState
    object Loading : EventUiState
}

class EventViewModel(private val eventRepository: EventRepository):ViewModel() {
    var eventUiState: EventUiState by mutableStateOf(EventUiState.Loading)
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
    fun getEvents() {
        viewModelScope.launch {
            eventUiState = EventUiState.Loading
            eventUiState = try {
                EventUiState.Success(eventRepository.getEvents())
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