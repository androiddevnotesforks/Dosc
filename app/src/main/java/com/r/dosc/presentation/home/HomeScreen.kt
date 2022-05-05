package com.r.dosc.presentation.home

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.domain.components.SetUpStatusBar
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.util.PermissionViewModel
import com.r.dosc.presentation.home.components.OnEmptyState
import com.r.dosc.presentation.home.components.ReadDirectory
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
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val lifecycleOwner = LocalLifecycleOwner.current
    SetUpStatusBar(systemUiController, lifecycleOwner, mainViewModel, true)

    val readPermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val writePermissionState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)


    ReadDirectory(
        permissionViewModel = permissionViewModel,
        readPermissionState = readPermissionState,
        hasPermission = {
            if (homeViewModel.listOfPdfDocuments.isNotEmpty()) {
                ShowPdfList(
                    listOfPdfs = homeViewModel.documentsSortByDate(),
                    navigator = navigator,
                    onClick = { ind ->
                        homeViewModel.removeElement(ind)
                    }
                )
            } else {
                OnEmptyState()
            }
        },

    )

    when (permissionViewModel.permissionsStorageWrite.value) {
        Permissions.SHOULD_SHOW_RATIONAL -> {
            LaunchedEffect(key1 = true) {
                writePermissionState.launchPermissionRequest()
            }

        }
        else -> Unit
    }


}





