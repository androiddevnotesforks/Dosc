package com.r.dosc.presentation.home

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.R
import com.r.dosc.domain.components.SetUpStatusBar
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.util.PermissionViewModel
import com.r.dosc.presentation.home.components.ReadDirectory
import com.r.dosc.presentation.main.MainViewModel
import com.ramcosta.composedestinations.annotation.Destination


@ExperimentalPermissionsApi
@Destination(start = true)
@Composable
fun HomeScreen(
    permissionViewModel: PermissionViewModel,
    mainViewModel: MainViewModel
) {

    val systemUiController = rememberSystemUiController()
    val lifecycleOwner = LocalLifecycleOwner.current
    SetUpStatusBar(systemUiController, lifecycleOwner, mainViewModel, true)

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.lottie_home_anim)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever,
    )


    val readPermissionState =
        rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)
    val writePermissionState =
        rememberPermissionState(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)


    ReadDirectory(
        permissionViewModel = permissionViewModel,
        composition = composition,
        progress = progress,
        readPermissionState
    )

    when (permissionViewModel.permissionsStorageWrite.value) {
        Permissions.HAS_PERMISSION -> {
            permissionViewModel.onEvent(HomeScreenEvents.DirectorySetup)
        }
        Permissions.SHOULD_SHOW_RATIONAL -> {
            //ask for permission
            LaunchedEffect(key1 = true) {
                writePermissionState.launchPermissionRequest()

            }

        }
        else -> Unit
    }


}





