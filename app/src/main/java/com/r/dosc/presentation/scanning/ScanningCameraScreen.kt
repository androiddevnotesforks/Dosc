package com.r.dosc.presentation.scanning

import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.R
import com.r.dosc.domain.ui.theme.*
import com.r.dosc.presentation.scanning.components.CameraView
import com.r.dosc.presentation.scanning.components.CaptureDocFloatingButton
import com.r.dosc.presentation.scanning.components.GotoCameraScreenButton
import com.r.dosc.presentation.scanning.components.ImagePreviewItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collect


@ExperimentalAnimationApi
@Destination
@Composable
fun ScanningCameraScreen(
    navigator: DestinationsNavigator,
    scanningViewModel: ScanningViewModel = hiltViewModel()
) {

    val systemUiController = rememberSystemUiController()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME) {
                    systemUiController.setStatusBarColor(
                        color = DarkColorPalette.primarySurface
                    )
                    systemUiController.setNavigationBarColor(
                        color = DarkColorPalette.primarySurface
                    )
                }

            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

    val imgListState = rememberLazyListState()

    LaunchedEffect(key1 = true){
        scanningViewModel.uiEvent.collect { event ->
            when (event) {
                ScanningScreenEvents.onClikCaptureDocument -> {


                }
                ScanningScreenEvents.CameraScreen -> {
                    imgListState.scrollToItem(0)
                }
                else -> {}
            }
        }
    }


    DoscTheme(darkTheme = true) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AnimatedVisibility(
                    visible = true,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Document",
                                fontSize = 30.sp,
                                color = Color.White,

                                )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                navigator.navigateUp()

                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "close"
                                )
                            }
                        },
                        actions = {
                            IconButton(
                                onClick = {
                                    navigator.navigateUp()
                                }

                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.DoneAll,
                                    contentDescription = "close",
                                    tint = MaterialTheme.colors.secondary
                                )
                            }
                        }
                    )
                }
            },
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //centre of scanning screen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(70f)
                ) {
                    when (val event = scanningViewModel.screenEvents.value) {
                        ScanningScreenEvents.CameraScreen -> {
                            CameraView(
                                modifier = Modifier
                                    .fillMaxSize(),
                                cameraProviderFuture = cameraProviderFuture,
                                lifecycleOwner = lifecycleOwner
                            )
                        }
                        is ScanningScreenEvents.OpenDocPreview -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = event.docId.toString(), fontSize = 35.sp)
                            }
                        }
                        else -> Unit
                    }
                }


                //bottom of scanning screen
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(25f)
                        .background(color = MaterialTheme.colors.primarySurface)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(5f)
                            .padding(end = 4.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        LazyRow(
                            reverseLayout = true,
                            modifier = Modifier
                                .weight(8.8f)
                                .padding(end = 4.dp, start = 4.dp),
                            state = imgListState
                        ) {
                            scanningViewModel.listOfImages.forEachIndexed { index, i ->
                                item {
                                    val docId = scanningViewModel.listOfImages.size - index
                                    ImagePreviewItem(
                                        imageItem = docId,
                                        onImageClick = {
                                            scanningViewModel.onEvent(
                                                ScanningScreenEvents.OpenDocPreview(
                                                    docId
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                        }


                        //plus icon
                        Box(
                            modifier = Modifier
                                .weight(1.2f),
                            contentAlignment = Alignment.Center
                        ) {
                            GotoCameraScreenButton {
                                scanningViewModel.onEvent(ScanningScreenEvents.CameraScreen)
                            }
                        }
                    }

                    //Capture Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(5f),
                        contentAlignment = Alignment.Center
                    ) {
                        AnimatedContent(true) {

                            CaptureDocFloatingButton {
                                //on Capture button click...

                            }
                        }
                    }
                }
            }
        }
    }
}





