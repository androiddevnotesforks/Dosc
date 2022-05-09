package com.r.dosc.presentation.home

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.domain.components.DeleteDialogBox
import com.r.dosc.domain.components.ReadDirectory
import com.r.dosc.domain.components.SetUpStatusBar
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.util.PermissionViewModel
import com.r.dosc.domain.util.getPdfUri
import com.r.dosc.presentation.destinations.PdfDocViewerDestination
import com.r.dosc.presentation.home.components.OnEmptyState
import com.r.dosc.presentation.home.components.ShowPdfList
import com.r.dosc.presentation.main.MainViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@ExperimentalPermissionsApi
@Destination(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    permissionViewModel: PermissionViewModel,
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel
) {
    val context = LocalContext.current

    val systemUiController = rememberSystemUiController()
    val lifecycleOwner = LocalLifecycleOwner.current
    SetUpStatusBar(systemUiController, lifecycleOwner, mainViewModel, true)

    val readPermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val writePermissionState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)



    LaunchedEffect(true) {
        mainViewModel.updateDocList.collect { isUpdate ->
            if (isUpdate) {
                homeViewModel.updateDocList()
                mainViewModel.updateDocList(false)
            }
        }


    }

    ReadDirectory(
        permissionViewModel = permissionViewModel,
        readPermissionState = readPermissionState,
        hasPermission = {
            if (homeViewModel.listOfPdfDocuments.isNotEmpty()) {
                ShowPdfList(
                    listOfPdfs = homeViewModel.listOfPdfDocuments,
                    onDelete = { indx ->
                        homeViewModel.showDialog(true)
                        //homeViewModel.deleteDocument(indx)
                    },
                    onShare = { file ->
                        val pdfUri: Uri = file.getPdfUri(context)

                        val shareDocument = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, pdfUri)
                            type = "application/pdf"
                        }

                        val share = Intent.createChooser(shareDocument, "share_pdf_document")
                        context.startActivity(share)

                    },
                    openDocument = { doc, indx ->
                        navigator.navigate(
                            direction = PdfDocViewerDestination(
                                isDarkTheme = mainViewModel.isDarkThemeState.value,
                                file = doc.file,
                            )
                        )
                    }
                )
            } else {
                OnEmptyState()
            }
        },
    )

    if (homeViewModel.showDialog.collectAsState().value) {
        DeleteDialogBox(
            isShowDialog = homeViewModel.showDialog.collectAsState().value,
            onDismissRequest = {
                homeViewModel.showDialog(false)

            }
        )
    }

    when (permissionViewModel.permissionsStorageWrite.value) {
        Permissions.SHOULD_SHOW_RATIONAL -> {
            LaunchedEffect(key1 = true) {
                writePermissionState.launchPermissionRequest()
            }

        }
        else -> Unit
    }


}





