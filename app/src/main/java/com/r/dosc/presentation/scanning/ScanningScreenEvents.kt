package com.r.dosc.presentation.scanning

import android.net.Uri


sealed class ScanningScreenEvents {
    data class OpenDocPreview(val uri: Uri) : ScanningScreenEvents()
    object CameraScreen : ScanningScreenEvents()
    object onClikCaptureDocument : ScanningScreenEvents()

}