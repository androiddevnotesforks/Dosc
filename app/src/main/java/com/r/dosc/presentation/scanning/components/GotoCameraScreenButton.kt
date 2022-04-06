package com.r.dosc.presentation.scanning.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.r.dosc.domain.ui.theme.GrayShade_dark
import com.r.dosc.domain.ui.theme.GrayShade_light
import com.r.dosc.domain.ui.theme.Green_Shade
import com.r.dosc.domain.ui.theme.Ocean_Green

@Composable
fun GotoCameraScreenButton(
    isScanningMode: Boolean = true,
    onClick: () -> Unit
) {

    val color = if (isScanningMode) Green_Shade else GrayShade_light

    Box(
        modifier = Modifier
            .size(40.dp)
            .border(
                width = 2.dp,
                color = color,
                shape = RoundedCornerShape(10.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = {
            onClick()
        }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "",
                tint = Color.White
            )
        }
    }

}