package com.example.seoulclutureapp.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.seoulclutureapp.EventApplication
import com.example.seoulclutureapp.ui.screens.EventViewModel

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
    }
}
fun CreationExtras.eventApplication(): EventApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as EventApplication)