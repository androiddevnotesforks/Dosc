package com.r.dosc.domain.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.util.PermissionViewModel
import com.r.dosc.presentation.home.components.OnEmptyState

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
            LaunchedEffect(key1 = true) {
                readPermissionState.launchPermissionRequest()
            }
        }
        Permissions.IS_PERMANENTLY_DENIED -> {
            OnEmptyState(
                "Storage permission is needed to display documents.\n Enable it form app settings.",
            )
        }
        else -> Unit
    }
}


