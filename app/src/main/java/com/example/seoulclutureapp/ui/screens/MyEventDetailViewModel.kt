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

data class MyEventDetailsUiState(
    val event: Event?
)
class MyEventDetailViewModel(savedStateHandle: SavedStateHandle,
                             private val eventRepository: EventRepository): ViewModel() {
    val eventId: Int? = savedStateHandle[EventDetailDestination.eventIdArg]

    val uiState: StateFlow<MyEventDetailsUiState>
            = eventRepository.getAllLikeEvent().filterNotNull().map {
        MyEventDetailsUiState(event = it[eventId ?: 0].toEvent())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(MyEventDetailViewModel.TIMEOUT_MILLIS),
        initialValue = MyEventDetailsUiState(null)
    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}