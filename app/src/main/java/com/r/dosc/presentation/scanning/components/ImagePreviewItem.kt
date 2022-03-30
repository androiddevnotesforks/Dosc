package com.r.dosc.presentation.scanning.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.r.dosc.domain.ui.theme.White_Shade


@Composable
fun ImagePreviewItem(
    imageItem: Int,
    onImageClick: ()  -> Unit

) {

    Box(
        modifier = Modifier
            .size(68.dp)
            .padding(end = 8.dp, top = 4.dp, bottom = 4.dp)
            .border(
                width = 1.dp,
                color = White_Shade,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                       onImageClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = imageItem.toString())

    }

}