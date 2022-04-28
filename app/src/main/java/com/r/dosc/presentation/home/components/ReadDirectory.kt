package com.r.dosc.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sort
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.domain.ui.theme.Helper_Text_Color
import com.r.dosc.domain.util.PermissionViewModel
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File

@ExperimentalPermissionsApi
@Composable
fun ReadDirectory(
    permissionViewModel: PermissionViewModel,
    readPermissionState: PermissionState,
    navigator: DestinationsNavigator

) {
    when (permissionViewModel.permissionsStorageRead.value) {
        Permissions.HAS_PERMISSION -> {
            if (permissionViewModel.listOfPdfs.isEmpty()) {
                OnEmptyState()

            } else {
                ShowPdfList(
                    listOfPdfs = permissionViewModel.listOfPdfs as ArrayList<File>,
                    navigator
                )
            }
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
            )

        }
        else -> Unit
    }
}

@Composable
fun ShowPdfList(
    listOfPdfs: ArrayList<File>,
    navigator: DestinationsNavigator

) {
    listOfPdfs.sortedBy { it.lastModified() }

    Column(
        modifier = Modifier.padding(bottom = 56.dp)
    ) {

        HelperTabLayout()
        LazyColumn {
            items(listOfPdfs.reversed()) { pdf ->
                PdfItem(file = pdf, navigator)
                Divider(
                    modifier = Modifier.padding(start = 50.dp, end = 12.dp),
                    color = Helper_Text_Color,
                    thickness = 0.5.dp
                )
            }
        }

    }
}

@Composable
fun HelperTabLayout() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, end = 12.dp, start = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(5f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Documents : ", color = Helper_Text_Color)
        }

        Row(
            modifier = Modifier.weight(5f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Icon(
                imageVector = Icons.Default.Sort,
                contentDescription = "filter_list",
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(text = "Sort")

        }
    }
}