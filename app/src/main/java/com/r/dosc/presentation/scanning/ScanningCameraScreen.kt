package com.r.dosc.presentation.scanning

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.domain.ui.theme.*
import com.r.dosc.presentation.scanning.components.*
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.abs


@ExperimentalAnimationApi
@Destination
@Composable
fun ScanningCameraScreen(
    fileName: String = "",
    navigator: DestinationsNavigator,
    scanningViewModel: ScanningViewModel = hiltViewModel()
) {

    val title = if (fileName.isEmpty()) "Document" else fileName

    val systemUiController = rememberSystemUiController()
    val lifecycleOwner = LocalLifecycleOwner.current


    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_START || event == Lifecycle.Event.ON_RESUME) {
                    systemUiController.setSystemBarsColor(
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

    val screenUiEvents by scanningViewModel.uiEvent.collectAsState(ScanningScreenEvents.CameraScreen)

    LaunchedEffect(Unit) {
        scanningViewModel.scrollIndex.collectLatest {
            imgListState.scrollToItem(it)
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
                                text = title,
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
                                    modifier = Modifier.size(30.dp),
                                    imageVector = Icons.Rounded.DoneAll,
                                    contentDescription = "save",
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
                    when (screenUiEvents) {
                        ScanningScreenEvents.CameraScreen -> {
                            CameraView(
                                modifier = Modifier
                                    .fillMaxSize(),
                                onImageCaptured = { imgUri ->
                                    scanningViewModel.addImage(imgUri)
                                },
                                onError = {

                                },
                                scanningViewModel = scanningViewModel
                            )
                        }
                        is ScanningScreenEvents.OpenDocPreview -> {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {

                                AsyncImage(
                                    modifier = Modifier.fillMaxSize(),
                                    model = (screenUiEvents as ScanningScreenEvents.OpenDocPreview).uri,
                                    contentDescription = "",
                                    alignment = Alignment.Center,
                                    contentScale = ContentScale.FillBounds
                                )

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
                            modifier = Modifier
                                .weight(8.8f)
                                .padding(end = 4.dp, start = 4.dp),
                            state = imgListState,
                            horizontalArrangement = Arrangement.End,
                        ) {
                            scanningViewModel.listOfImages.forEachIndexed { index, i ->
                                item {
                                    val count = abs(index + 1)
                                    ImagePreviewItem(
                                        uri = i,
                                        count,
                                        scanningViewModel,
                                        onImageClick = { sendUri, _ ->

                                            scanningViewModel.onEvent(
                                                ScanningScreenEvents.OpenDocPreview(
                                                    sendUri, index
                                                )
                                            )

                                        },
                                        removeImage = { indx ->
                                            scanningViewModel.onEvent(
                                                ScanningScreenEvents.RemoveImage(
                                                    indx
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

                            GotoCameraScreenButton(
                                isScanningMode = scanningViewModel.isScanningMode.collectAsState().value,
                            ) {
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
                        androidx.compose.animation.AnimatedVisibility(
                            visible = scanningViewModel.isScanningMode.collectAsState().value,
                            enter = slideInVertically { height -> height } + fadeIn(),
                            exit = slideOutVertically { height -> height } + fadeOut()

                        ) {

                            CaptureDocFloatingButton(
                                scanningViewModel
                            )


                        }

                        androidx.compose.animation.AnimatedVisibility(
                            visible = !scanningViewModel.isScanningMode.collectAsState().value,
                            enter = slideInVertically { height -> height } + fadeIn(),
                            exit = slideOutVertically { height -> height } + fadeOut()

                        ) {

                            EditImage()


                        }

                    }
                }
            }
        }
    }
}




