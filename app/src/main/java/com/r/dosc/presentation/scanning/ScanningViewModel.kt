package com.r.dosc.presentation.scanning

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScanningViewModel
@Inject constructor(

) : ViewModel() {

    val listOfImages = mutableStateListOf<Int>()

    val screenEvents = mutableStateOf<ScanningScreenEvents>(ScanningScreenEvents.CameraScreen)

    private val _uiEvent = Channel<ScanningScreenEvents>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(events: ScanningScreenEvents) {
        when (events) {
            is ScanningScreenEvents.OpenDocPreview -> {
                screenEvents.value = events
            }
            ScanningScreenEvents.CameraScreen -> {
                screenEvents.value = events
            }
            ScanningScreenEvents.onClikCaptureDocument -> {
                viewModelScope.launch {
                    setEvents(events)
                }
            }
        }
    }

    fun addImage() {
        val addCount = if (listOfImages.lastOrNull() == null) 1 else listOfImages.last() + 1
        listOfImages.add(addCount)
    }

    private suspend fun setEvents(scanningScreenEvents: ScanningScreenEvents) {
        withContext(Dispatchers.Default) {
            _uiEvent.send(scanningScreenEvents)
        }
    }


}

