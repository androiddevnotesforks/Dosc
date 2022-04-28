package com.r.dosc.presentation.scanning

import android.net.Uri
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.compose.LottieConstants
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.Rectangle
import com.r.dosc.di.modules.CamX
import com.r.dosc.domain.util.DocumentEssential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
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
    private val camX: CamX,
    private val documentEssential: DocumentEssential,
    private val iDocument: Document
) : ViewModel() {

    var docName = ""

    val listOfImages = mutableStateListOf<Uri>()

    private val _uiEvent = MutableStateFlow<ScanningScreenEvents>(ScanningScreenEvents.CameraScreen)
    val uiEvent = _uiEvent

    val closeScanningScreen = MutableStateFlow(false)
    val showDialog = MutableStateFlow(false)
    val captureImage = MutableStateFlow(false)
    val isScanningMode = MutableStateFlow(true)
    val isDocumentPreviewMode = MutableStateFlow(false)
    val iterationsBtn = MutableStateFlow(LottieConstants.IterateForever)
    var scrollIndex = MutableStateFlow(0)
    private val isClickedFirstTime = MutableStateFlow(CaptureButtonAnim.INITIAL)

    init {
        if (!tempDirectory.exists()) {
            tempDirectory.mkdirs()
        }
        if (!mainDirectory.exists()) {
            mainDirectory.mkdirs()
        }
    }

    fun onEvent(events: ScanningScreenEvents) {
        when (events) {
            is ScanningScreenEvents.OpenDocPreview -> {
                iterationsBtn.value = 2
                isDocumentPreviewMode.value = true
                isScanningMode.value = false
                viewModelScope.launch {
                    _uiEvent.emit(events)
                }
            }
            ScanningScreenEvents.CameraScreen -> {
                isDocumentPreviewMode.value = false
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
                showDialog.value = true
                createPdfDocument()
            }

            else -> Unit
        }

    }

    private fun createPdfDocument() {
        documentEssential.pdfWriter(iDocument, getFileName())

        iDocument.open()

        var count = 0
        viewModelScope.launch {

            val creatingPdf = async {
                listOfImages.forEach { imFile ->
                    count++

                    val image: Image =
                        Image.getInstance(documentEssential.compressImage(count, imFile))

                    image.setAbsolutePosition(0f, 0f)

                    iDocument.pageSize = Rectangle(image.width, image.height)


                    iDocument.newPage()

                    iDocument.add(image)

                }


            }
            creatingPdf.await()
            delay(3000L)
            showDialog.value = false
            closeScanningScreen.emit(true)

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
            captureImage.emit(click)

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

    private fun checkFileExist(f: String, count: Int): String {

        val file =
            if (count > 0) File("$mainDirectory/$f($count).pdf") else File("$mainDirectory/$f.pdf")

        return if (!file.exists()) {
            if (count > 0) {
                "$f($count)"
            } else {
                f
            }

        } else {
            checkFileExist(f, count + 1)
        }

    }

    private fun getFileName(): String = if (docName.isNotEmpty()) "$mainDirectory/${
        checkFileExist(
            docName,
            0
        )
    }.pdf" else "$mainDirectory/${getDefaultName()}.pdf"

    private fun getDefaultName(): String {
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyyMMdd-HHmmss", Locale.getDefault())
        val timeStamp: String = dateFormat.format(currentTime)
        return "dosc-$timeStamp"
    }

    override fun onCleared() {
        super.onCleared()
        iDocument.close()
        getTempOutputDirectory().deleteRecursively()
    }

}

enum class CaptureButtonAnim {
    INITIAL,
    CLICKED
}

