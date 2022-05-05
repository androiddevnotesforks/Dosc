package com.r.dosc.presentation.home

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.r.dosc.domain.models.PdfDocumentDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel
@Inject constructor(
    @Named("dosc") private val mainDirectory: File,
) : ViewModel() {

    private val _listOfPdfDocuments = mutableStateListOf<PdfDocumentDetails>()
    val listOfPdfDocuments = _listOfPdfDocuments

    init {
        getAllPdfDocuments()
    }

    private fun getAllPdfDocuments()  {
        mainDirectory.listFiles()?.forEach { file ->
            val pdfDocumentDetails = PdfDocumentDetails(
                documentName = file.name,
                filePath = file.absolutePath,
                noOfPages = "0",
                docSize = getFileSize(file.length()),
                dateCreated = getFileDate(file.lastModified()),
                timestamp = 0L,
                file= getPdfDocument(file.absolutePath)
            )
            listOfPdfDocuments.add(pdfDocumentDetails)
        }


    }

    private fun getFileSize(length: Long): String {
        val size = (length / 1024)
        return if (size < 1024) {
            "$size kB"
        } else {
            "${(size / 1024)} MB"
        }
    }

    private fun getFileDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        return sdf.format(timestamp)
    }

    fun getPdfDocument(path: String?): File = File(path.toString())


}