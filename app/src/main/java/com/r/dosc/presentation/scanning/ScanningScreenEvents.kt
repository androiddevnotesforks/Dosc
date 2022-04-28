package com.r.dosc.presentation.scanning

import android.net.Uri


sealed class ScanningScreenEvents {
    data class OpenDocPreview(val uri: Uri, val indx: Int) : ScanningScreenEvents()
    object CameraScreen : ScanningScreenEvents()
    object SavePdf : ScanningScreenEvents()
    object NavigateUp : ScanningScreenEvents()
    data class RemoveImage(val indx: Int) : ScanningScreenEvents()


}