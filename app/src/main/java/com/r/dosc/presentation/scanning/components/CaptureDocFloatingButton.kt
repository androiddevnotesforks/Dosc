package com.r.dosc.presentation.scanning.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.*
import com.r.dosc.R
import com.r.dosc.domain.navigation.ClearRippleTheme
import com.r.dosc.presentation.scanning.ScanningViewModel

@Composable
fun CaptureDocFloatingButton(
    scanningViewModel: ScanningViewModel,
) {



    var count by remember {
        mutableStateOf(0)
    }


    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.camera_click_anim)
    )

    val progress by animateLottieCompositionAsState(
        composition,
        isPlaying = true,
        iterations = scanningViewModel.iterationsBtn.collectAsState().value,
        cancellationBehavior = LottieCancellationBehavior.Immediately,
    )

    CompositionLocalProvider(
        LocalRippleTheme provides ClearRippleTheme
    ) {
        LottieAnimation(
            composition,
            progress,
            modifier = Modifier
                .size(250.dp, 250.dp)
                .clickable {
                    scanningViewModel.clickImage(true)

                },
        )
    }
}


