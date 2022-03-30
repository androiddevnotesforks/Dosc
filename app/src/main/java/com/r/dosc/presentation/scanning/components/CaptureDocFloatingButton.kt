package com.r.dosc.presentation.scanning.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.r.dosc.R

@Composable
fun CaptureDocFloatingButton(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = {
                  onClick()
        },
        backgroundColor = MaterialTheme.colors.primary,
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
            painter = painterResource(id = R.drawable.ic_dot_circle),
            contentDescription = ""
        )
    }
}