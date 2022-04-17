package com.r.dosc.presentation.scanning.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.r.dosc.R
import com.r.dosc.domain.ui.theme.Dark_1

@Composable
fun DocCreatingDialog() {

    val composition by rememberLottieComposition(spec = LottieCompositionSpec.RawRes(R.raw.dark_printing))
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
                .height(110.dp)
                .fillMaxWidth()
                .background(shape = RoundedCornerShape(12.dp), color = Dark_1)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                LottieAnimation(
                    modifier = Modifier
                        .size(110.dp),
                    composition = composition,
                    progress = progress
                )

                Text(
                    modifier = Modifier
                        .padding(top = 20.dp),
                    text = "Creating Document . . .",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp
                )


            }
        }


    }


}