package com.r.dosc.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r.dosc.data.preference.PreferenceStorage
import com.r.dosc.domain.models.PdfDocumentDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named


@HiltViewModel
class HomeViewModel
@Inject constructor(
    @Named("dosc") private val mainDirectory: File,
    private val prefStorage: PreferenceStorage,
) : ViewModel() {


    private val sortTypeId = MutableStateFlow(2)
    private val listOfPdfDocuments = MutableStateFlow<List<PdfDocumentDetails>>(emptyList())

    val listPdf = combine(listOfPdfDocuments, sortTypeId) { list, id ->
        if (id == 1) {
            list.sortedBy {
                it.documentName
            }
        } else {
            list.sortedByDescending {
                it.timestamp
            }
        }

    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    init {
        viewModelScope.launch {
            sortTypeId.value = prefStorage.sortTypeId.first()

        }
        getAllPdfDocuments()
    }


    fun deleteDocument(index: Int?) {
        if (index != null) {
            val file = File("${listOfPdfDocuments.value[index].filePath}")
            file.deleteRecursively()
            updateDocList()

        }
    }

    private fun getAllPdfDocuments() {
        val list = mutableListOf<PdfDocumentDetails>()
        mainDirectory.listFiles()?.forEach { file ->
            val pdfDocumentDetails = PdfDocumentDetails(
                documentName = file.name,
                filePath = file.absolutePath,
                noOfPages = "",
                docSize = getFileSize(file.length()),
                dateCreated = getFileDate(file.lastModified()),
                timestamp = file.lastModified(),
                file = getPdfDocument(file.absolutePath)
            )
            list.add(pdfDocumentDetails)
        }
        viewModelScope.launch {
            listOfPdfDocuments.emit(list)
        }

    }

    fun updateDocList() {
        viewModelScope.launch {
            listOfPdfDocuments.emit(emptyList())
            getAllPdfDocuments()
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
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return sdf.format(timestamp)
    }

    private fun getPdfDocument(path: String?): File = File(path.toString())

}