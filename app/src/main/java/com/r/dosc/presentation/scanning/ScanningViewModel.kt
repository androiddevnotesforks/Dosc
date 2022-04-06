package com.r.dosc.presentation.scanning

import android.net.Uri
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.LottieConstants
import com.r.dosc.di.modules.CamX
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
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
    val isScanningMode = MutableStateFlow(true)
    val iterationsBtn = MutableStateFlow(LottieConstants.IterateForever)
    var scrollIndex = MutableStateFlow(0)
    private val isClickedFirstTime = MutableStateFlow(CaptureButtonAnim.INITIAL)

    init {
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs()
        }
    }

    fun onEvent(events: ScanningScreenEvents) {
        when (events) {
            is ScanningScreenEvents.OpenDocPreview -> {
                iterationsBtn.value = 2
                isScanningMode.value = false
                viewModelScope.launch {
                    _uiEvent.emit(events)
                }
            }
            ScanningScreenEvents.CameraScreen -> {
                isScanningMode.value = true
                viewModelScope.launch {
                    _uiEvent.emit(events)

                }
            }
            is ScanningScreenEvents.RemoveImage -> {
                listOfImages.removeAt(events.indx)
                if (!isScanningMode.value) {
                    onEvent(ScanningScreenEvents.CameraScreen)

                }

            }
        }

    }

    fun addImage(uri: Uri) {
        viewModelScope.launch {
            listOfImages.add(uri)
            scrollIndex.emit(listOfImages.size)
        }


    }

    fun clickImage(click: Boolean) {
        viewModelScope.launch {
            clickImage.emit(click)

            if (click) {

                if (isClickedFirstTime.value == CaptureButtonAnim.INITIAL) {
                    iterationsBtn.value = 1
                    iterationsBtn.value++
                } else {
                    iterationsBtn.value++

                }

            }
            isClickedFirstTime.emit(CaptureButtonAnim.CLICKED)


        }
    }


    fun getTempOutputDirectory(): File = tempDirectory

    fun getCameraExecutor(): ExecutorService = cameraExecutor

    suspend fun getCameraProvider(): ProcessCameraProvider = camX.getCameraProvider()


    override fun onCleared() {
        super.onCleared()
        getTempOutputDirectory().deleteRecursively()
    }

}

enum class CaptureButtonAnim {
    INITIAL,
    CLICKED
}

