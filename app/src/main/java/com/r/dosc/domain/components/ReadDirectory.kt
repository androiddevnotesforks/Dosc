package com.r.dosc.domain.components

import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.util.PermissionViewModel

@ExperimentalPermissionsApi
@Composable
fun ReadDirectory(
    permissionViewModel: PermissionViewModel,
    readPermissionState: PermissionState,
    hasPermission: @Composable () -> Unit,
) {
    when (permissionViewModel.permissionsStorageRead.value) {
        Permissions.HAS_PERMISSION -> {
            hasPermission()
        }
        Permissions.SHOULD_SHOW_RATIONAL -> {
            hasPermission()

//            LaunchedEffect(key1 = true) {
//                readPermissionState.launchPermissionRequest()
//            }
        }
        Permissions.IS_PERMANENTLY_DENIED -> {
            hasPermission()
//
//            OnEmptyState(
//                "Storage permission is needed to display documents.\n Enable it from app settings.",
//            )
        }
        else -> Unit
    }
}


