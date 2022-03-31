package com.r.dosc.presentation.home

import android.Manifest
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.R
import com.r.dosc.domain.components.SetUpStatusBar
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.util.PermissionViewModel
import com.r.dosc.presentation.main.MainViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect
import java.io.File


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

    val context = LocalContext.current


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
            //setup directory
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

    LaunchedEffect(key1 = true) {

        permissionViewModel.uiEvent.collect { event ->
            when (event) {
                HomeScreenEvents.DirectorySetup -> {
                    val path = context.getExternalFilesDir("")
                    if (path?.exists() == false) {
                        path.mkdirs()
                    } else {
                        // directory available get all pdf files.

                    }
                }
            }

        }

    }


}

@ExperimentalPermissionsApi
@Composable
fun ReadDirectory(
    permissionViewModel: PermissionViewModel,
    composition: LottieComposition?,
    progress: Float,
    readPermissionState: PermissionState
) {
    when (permissionViewModel.permissionsStorageRead.value) {
        Permissions.HAS_PERMISSION -> {
            OnEmptyState(composition = composition, progress = progress)
        }
        Permissions.SHOULD_SHOW_RATIONAL -> {
            //ask for permission
            LaunchedEffect(key1 = true) {
                readPermissionState.launchPermissionRequest()

            }
        }
        Permissions.IS_PERMANENTLY_DENIED -> {
            OnEmptyState(
                "Storage permission is needed to access doc.\n Enable it form app settings.",
                composition,
                progress
            )

        }
        else -> Unit
    }
}

@Composable
fun OnEmptyState(
    text: String = "",
    composition: LottieComposition?,
    progress: Float
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        LottieAnimation(
            composition,
            progress,
            modifier = Modifier.size(250.dp, 250.dp),
        )

        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = text,
            style = TextStyle(
                textAlign = TextAlign.Center,
            ),
            color = Color.LightGray
        )

    }

}

