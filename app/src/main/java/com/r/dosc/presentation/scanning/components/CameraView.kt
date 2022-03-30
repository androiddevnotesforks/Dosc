package com.r.dosc.presentation.scanning.components

import android.content.Context
import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.r.dosc.presentation.scanning.ScanningScreenEvents
import com.r.dosc.presentation.scanning.ScanningViewModel
import kotlinx.coroutines.flow.collect
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@Composable
fun CameraView(
    modifier: Modifier,
    outputDir: File,
    executorService: ExecutorService,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    scanningViewModel: ScanningViewModel
) {
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val preview = Preview.Builder().build()
    val previewView = remember { PreviewView(context) }
    val imageCapture: ImageCapture = remember { ImageCapture.Builder().build() }
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture
        )
        preview.setSurfaceProvider(previewView.surfaceProvider)

        scanningViewModel.uiEvent.collect { event ->
            when (event) {

                ScanningScreenEvents.onClikCaptureDocument -> {
                    takePhoto(
                        imageCapture = imageCapture,
                        outputDir = outputDir,
                        executorService = executorService,
                        onImageCaptured = onImageCaptured,
                        onError = onError
                    )
                }
                else -> {}
            }
        }
    }


    AndroidView(
        {
            previewView
        },
        modifier = modifier
    )
}

private fun takePhoto(
    imageCapture: ImageCapture,
    outputDir: File,
    executorService: ExecutorService,
    onImageCaptured: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit
) {

    val photoFile = File(
        outputDir,
        SimpleDateFormat(
            "yyy-MM-dd-HH-ss-SSS",
            Locale.getDefault()
        ).format(System.currentTimeMillis()) + ".jpg"
    )

    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

    imageCapture.takePicture(
        outputOptions,
        executorService,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val saveUri = Uri.fromFile(photoFile)
                onImageCaptured(saveUri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        })

}

private suspend fun Context.getCameraProvider(): ProcessCameraProvider = suspendCoroutine { cnt ->
    ProcessCameraProvider.getInstance(this).also { cameraProvider ->
        cameraProvider.addListener({
            cnt.resume(cameraProvider.get())
        }, ContextCompat.getMainExecutor(this))
    }

}