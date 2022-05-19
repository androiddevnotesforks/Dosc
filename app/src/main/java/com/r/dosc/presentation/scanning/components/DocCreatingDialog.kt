package com.r.dosc.presentation.scanning.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.r.dosc.R


@Composable
fun DocCreatingDialog() {

    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.pdf_generating))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE
    )

    Dialog(
        onDismissRequest = {},
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .background(shape = RoundedCornerShape(12.dp), color = Color.Unspecified)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                LottieAnimation(
                    modifier = Modifier.size(100.dp),
                    composition = composition,
                    progress = progress
                )


            }
        }
    }

}