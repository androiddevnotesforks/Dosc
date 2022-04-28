package com.r.dosc.presentation.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.r.dosc.R
import com.r.dosc.domain.ui.theme.GrayShade_dark
import com.r.dosc.presentation.destinations.PdfDocViewerDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PdfItem(
    file: File,
    navigator: DestinationsNavigator
) {
    var cliked by remember {
        mutableStateOf(false)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .clickable {
                cliked = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .weight(17f)
                .size(25.dp),
            painter = painterResource(id = R.drawable.ic_pdf_2),
            contentDescription = "icon",
            tint = Color.Unspecified
        )

        Spacer(modifier = Modifier.weight(1f))

        Column(
            modifier = Modifier.weight(66f)
        ) {
            Text(text = file.name, fontSize = 19.sp)

            Row {
                Text(
                    text = getFileDate(file.lastModified()),
                    fontSize = 14.sp,
                    color = GrayShade_dark
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(text = getFileSize(file.length()), fontSize = 14.sp, color = GrayShade_dark)

            }


        }

        Box(
            modifier = Modifier.weight(16f),
            contentAlignment = Alignment.CenterStart
        ) {
            IconButton(onClick = {

            }) {
                Icon(
                    modifier = Modifier.size(25.dp),
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "",
                    tint = GrayShade_dark
                )
            }

        }

    }

    if (cliked){
        navigator.navigate(PdfDocViewerDestination(file = file))
        cliked = false
    }

}

fun getFileSize(length: Long): String {
    val size = (length / 1024)
    return if (size < 1024) {
        "$size kB"
    } else {
        "${(size / 1024)} MB"
    }
}

fun getFileDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return sdf.format(timestamp)
}