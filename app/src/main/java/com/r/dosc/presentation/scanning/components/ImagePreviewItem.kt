package com.r.dosc.presentation.scanning.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.r.dosc.domain.ui.theme.GrayShade_dark
import com.r.dosc.domain.ui.theme.Green_Shade
import com.r.dosc.domain.ui.theme.Ocean_Red_2
import com.r.dosc.domain.ui.theme.White_Shade
import com.r.dosc.presentation.scanning.ScanningScreenEvents
import com.r.dosc.presentation.scanning.ScanningViewModel


@Composable
fun ImagePreviewItem(
    uri: Uri,
    count: Int,
    scanningViewModel: ScanningViewModel,
    removeImage: (Int) -> Unit,
    onImageClick: (Uri, Int) -> Unit,
) {

    var isSelected by remember {
        mutableStateOf(Color.LightGray)

    }

    LaunchedEffect(Unit) {
        scanningViewModel.uiEvent.collect { uiEvent ->
            isSelected = when (uiEvent) {
                is ScanningScreenEvents.OpenDocPreview -> {
                    if (uiEvent.indx == count - 1) {
                        Ocean_Red_2
                    } else {
                        Color.LightGray
                    }
                }
                else -> {
                    Color.LightGray
                }
            }

        }

    }

    Box(
        modifier = Modifier
            .size(68.dp)
            .padding(end = 12.dp, top = 6.dp, bottom = 6.dp)
            .clickable {
                onImageClick(uri, count - 1)

            },
        contentAlignment = Alignment.Center
    ) {


        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    color = isSelected,
                    shape = RoundedCornerShape(10.dp)
                )
                .clip(RoundedCornerShape(10.dp)),
            model = uri,
            contentDescription = "",
            contentScale = ContentScale.FillBounds,
        )

        Icon(
            modifier = Modifier
                .size(13.dp)
                .offset(x = 26.dp, y = (-27).dp)
                .clip(RoundedCornerShape(100))
                .background(GrayShade_dark)
                .clickable {
                    removeImage(count - 1)
                },
            imageVector = Icons.Rounded.Remove,
            contentDescription = "delete",
            tint = White_Shade
        )


        Box(
            modifier = Modifier
                .size(15.dp)
                .offset(x = (-25).dp, y = 27.dp)
                .clip(RoundedCornerShape(100))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            Text(text = count.toString(), color = White_Shade, fontSize = 10.sp)
        }

    }

}