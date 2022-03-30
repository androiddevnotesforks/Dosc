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
import androidx.compose.ui.unit.dp
import com.r.dosc.domain.ui.theme.GrayShade_dark
import com.r.dosc.domain.ui.theme.GrayShade_light

@Composable
fun GotoCameraScreenButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .border(
                width = 2.dp,
                color = GrayShade_dark,
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
                tint = GrayShade_light
            )
        }
    }

}