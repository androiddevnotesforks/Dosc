package com.r.dosc.presentation.scanning


sealed class ScanningScreenEvents {
    data class OpenDocPreview(val docId: Int?) : ScanningScreenEvents()
    object CameraScreen : ScanningScreenEvents()
    object onClikCaptureDocument : ScanningScreenEvents()

}