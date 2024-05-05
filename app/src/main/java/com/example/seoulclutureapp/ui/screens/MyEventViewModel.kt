package com.example.seoulclutureapp.ui.screens

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.seoulclutureapp.data.EventRepository
import com.example.seoulclutureapp.model.Event
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class MyEventUiState(
    val events: List<Event>
)
class MyEventViewModel(savedStateHandle: SavedStateHandle,
                       private val eventRepository: EventRepository):ViewModel() {

    val uiState: StateFlow<MyEventUiState>
            = eventRepository.getAllLikeEvent().filterNotNull().map {
        val list = ArrayList<Event>()
        for(e in it){
            list.add(e.toEvent())
        }

        MyEventUiState(events=list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(MyEventViewModel.TIMEOUT_MILLIS),
        initialValue = MyEventUiState(listOf())
    )
    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}