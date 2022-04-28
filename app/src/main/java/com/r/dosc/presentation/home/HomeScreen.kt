package com.r.dosc.presentation.home

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.domain.components.SetUpStatusBar
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.util.PermissionViewModel
import com.r.dosc.presentation.home.components.ReadDirectory
import com.r.dosc.presentation.main.MainViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@ExperimentalPermissionsApi
@Destination(start = true)
@Composable
fun HomeScreen(
    navigator: DestinationsNavigator,
    permissionViewModel: PermissionViewModel,
    mainViewModel: MainViewModel
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
        navigator
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





