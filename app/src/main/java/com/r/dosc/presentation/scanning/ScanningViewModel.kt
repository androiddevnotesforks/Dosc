package com.r.dosc.presentation.scanning

import android.content.Context
import android.net.Uri
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r.dosc.di.modules.CamX
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@HiltViewModel
class ScanningViewModel
@Inject constructor(
    private val tempDirectory: File,
    private val cameraExecutor: ExecutorService,
    private val camX: CamX
) : ViewModel() {

    val listOfImages = mutableStateListOf<Uri>()


    private val _uiEvent = MutableStateFlow<ScanningScreenEvents>(ScanningScreenEvents.CameraScreen)
    val uiEvent = _uiEvent

    val clickImage = MutableStateFlow(false)

    init {
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs()
        }
    }

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

    fun getTempOutputDirectory(): File = tempDirectory

    fun getCameraExecutor(): ExecutorService = cameraExecutor

    suspend fun getCameraProvider() : ProcessCameraProvider = camX.getCameraProvider()


    override fun onCleared() {
        super.onCleared()
        getTempOutputDirectory().deleteRecursively()
    }

}

