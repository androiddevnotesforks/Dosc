package com.r.dosc.presentation.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itextpdf.text.pdf.PdfReader
import com.r.dosc.domain.models.PdfDocumentDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.io.File
import java.lang.reflect.InvocationTargetException
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

    val dismissDropDown = mutableStateOf(false)


    init {
        getAllPdfDocuments()
    }

    fun onEvent(events: HomeScreenEvents) {
        when (events) {
            is HomeScreenEvents.DismissDropDown -> {
                setDismissDropDown(events.dismiss)
            }
        }
    }

    private fun setDismissDropDown(open: Boolean) {
        dismissDropDown.value = open
    }

    fun deleteDocument(index: Int?) {
        if (index != null) {
            val file = File("${_listOfPdfDocuments[index].filePath}")
            file.deleteRecursively()
            listOfPdfDocuments.removeAt(index)
        }
    }

    private fun getAllPdfDocuments() {
        mainDirectory.listFiles()?.forEach { file ->
            val pdfDocumentDetails = PdfDocumentDetails(
                documentName = file.name,
                filePath = file.absolutePath,
                noOfPages = "",
                docSize = getFileSize(file.length()),
                dateCreated = getFileDate(file.lastModified()),
                timestamp = 0L,
                file = getPdfDocument(file.absolutePath)
            )
            listOfPdfDocuments.add(pdfDocumentDetails)
        }

    }

    fun updateDocList() {
        _listOfPdfDocuments.removeAll(_listOfPdfDocuments)
        getAllPdfDocuments()
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
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(timestamp)
    }

    private fun getPdfDocument(path: String?): File = File(path.toString())

    private fun getNoOfDocPages(file: File): String {
        return try {
            val pdfReader = PdfReader(file.absolutePath)
            pdfReader.numberOfPages.toString()

        } catch (e: InvocationTargetException) {
            ""
        } catch (e: Exception) {
            ""
        }

    }


}