package com.r.dosc.presentation.scanning

import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ScanningViewModel
@Inject constructor(

) : ViewModel() {

    val listOfImages = mutableStateListOf<Uri>()


    private val _uiEvent = MutableStateFlow<ScanningScreenEvents>(ScanningScreenEvents.CameraScreen)
    val uiEvent = _uiEvent

    val clickImage = MutableStateFlow(false)


    fun onEvent(events: ScanningScreenEvents) {
        when (events) {
            is ScanningScreenEvents.OpenDocPreview -> {
                viewModelScope.launch {
                    _uiEvent.emit(events)
                }
            }
            ScanningScreenEvents.CameraScreen -> {
                viewModelScope.launch {
                    _uiEvent.emit(events)
                }
            }
        }
    }

    fun addImage(uri: Uri) {
        listOfImages.add(uri)
    }

    fun clickImage(click: Boolean) {
        viewModelScope.launch {
            clickImage.emit(click)

        }
    }


}

