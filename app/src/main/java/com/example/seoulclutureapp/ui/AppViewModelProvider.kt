package com.example.seoulclutureapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.seoulclutureapp.EventApplication
import com.example.seoulclutureapp.ui.screens.EventDetailViewModel
import com.example.seoulclutureapp.ui.screens.EventViewModel
import com.example.seoulclutureapp.ui.screens.MyEventDetailViewModel
import com.example.seoulclutureapp.ui.screens.MyEventViewModel

object AppViewModelProvider {
    @RequiresApi(Build.VERSION_CODES.O)
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            EventViewModel(
                this.createSavedStateHandle(),
                eventApplication().container.eventRepository
            )
        }
        initializer {
            EventDetailViewModel(
                this.createSavedStateHandle(),
                eventApplication().container.eventRepository
            )
        }
        initializer {
            MyEventViewModel(
                this.createSavedStateHandle(),
                eventApplication().container.eventRepository
            )
        }
        initializer {
            MyEventDetailViewModel(
                this.createSavedStateHandle(),
                eventApplication().container.eventRepository
            )
        }
    }
}
fun CreationExtras.eventApplication(): EventApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EventApplication)