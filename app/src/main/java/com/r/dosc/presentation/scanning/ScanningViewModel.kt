package com.r.dosc.presentation.scanning

import android.net.Uri
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.LottieConstants
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.pdf.PdfWriter
import com.r.dosc.di.modules.CamX
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class ScanningViewModel
@Inject constructor(
    @Named("temp") private val tempDirectory: File,
    @Named("dosc") private val mainDirectory: File,
    private val cameraExecutor: ExecutorService,
    private val camX: CamX
) : ViewModel() {

    var docName = ""


    val listOfImages = mutableStateListOf<Uri>()

    private val _uiEvent = MutableStateFlow<ScanningScreenEvents>(ScanningScreenEvents.CameraScreen)
    val uiEvent = _uiEvent

    val close = MutableStateFlow(false)
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
            ScanningScreenEvents.SavePdf -> {
                val document = Document()

                //val dir = if (docName.isNotEmpty()) "$mainDirectory/$docName.pdf" else "$mainDirectory/${getDefaultName()}.pdf"
                val dir =
                    if (docName.isNotEmpty()) "$mainDirectory/${
                        checkFileExist(
                            docName,
                            0
                        )
                    }.pdf" else "$mainDirectory/${getDefaultName()}.pdf"

                PdfWriter.getInstance(
                    document,
                    FileOutputStream(dir)
                )

                document.open()

                listOfImages.forEach { directoryPath ->
                    val image: Image = Image.getInstance(directoryPath.toString())

                    val scale = (document.pageSize.width - document.leftMargin()
                            - document.rightMargin() - 0) / image.width * 100 // 0 means you have no indentation. If you have any, change it.

                    image.scalePercent(scale)

                    image.alignment = Image.ALIGN_CENTER or Image.ALIGN_TOP


                    document.add(image)
                }

                document.close()

                viewModelScope.launch {
                    close.emit(true)
                }

            }
            else -> Unit
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

    private fun getDefaultName(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        val timeStamp: String = dateFormat.format(currentTime)
        return "dosc-$timeStamp"
    }

    override fun onCleared() {
        super.onCleared()
        getTempOutputDirectory().deleteRecursively()
    }

    private fun checkFileExist(f: String, count: Int): String {

        val file = if (count > 0) File("$mainDirectory/$f($count).pdf") else File("$mainDirectory/$f.pdf")

        return if (!file.exists()) {
            if (count > 0) {
                "$f($count)"
            } else {
                f
            }

        } else {
            checkFileExist(f, count+1)
        }

    }


}

enum class CaptureButtonAnim {
    INITIAL,
    CLICKED
}

