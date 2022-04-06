package com.r.dosc.presentation.home.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.airbnb.lottie.LottieComposition
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.util.PermissionViewModel

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