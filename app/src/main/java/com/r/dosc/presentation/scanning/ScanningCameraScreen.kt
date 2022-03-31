package com.r.dosc.presentation.scanning

import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.domain.ui.theme.*
import com.r.dosc.presentation.scanning.components.CameraView
import com.r.dosc.presentation.scanning.components.CaptureDocFloatingButton
import com.r.dosc.presentation.scanning.components.GotoCameraScreenButton
import com.r.dosc.presentation.scanning.components.ImagePreviewItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

private lateinit var outputDirectory: File
private lateinit var cameraExecutor: ExecutorService

@ExperimentalAnimationApi
@Destination
@Composable
fun ScanningCameraScreen(
    navigator: DestinationsNavigator,
    scanningViewModel: ScanningViewModel = hiltViewModel()
) {
    val context = LocalContext.current


    outputDirectory = getOutputDirectory(context)
    cameraExecutor = Executors.newSingleThreadExecutor()

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

                if (event == Lifecycle.Event.ON_DESTROY) {
                    deleteTemp(context)
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
        scanningViewModel.clickImage.collect {
            imgListState.scrollToItem(0)

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
                    when (screenUiEvents) {
                        ScanningScreenEvents.CameraScreen -> {
                            CameraView(
                                modifier = Modifier
                                    .fillMaxSize(),
                                outputDir = outputDirectory,
                                executorService = cameraExecutor,
                                onImageCaptured = { imgUri ->
                                    scanningViewModel.addImage(imgUri)
                                },
                                onError = {
                                    println("imge ex => $it")
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
                            reverseLayout = true,
                        ) {
                            scanningViewModel.listOfImages.reversed().forEachIndexed { index, i ->
                                val count = abs(index - scanningViewModel.listOfImages.size)
                                item {
                                    ImagePreviewItem(
                                        uri = i,
                                        count,
                                        onImageClick = { getUri ->

                                            scanningViewModel.onEvent(
                                                ScanningScreenEvents.OpenDocPreview(
                                                    getUri
                                                )
                                            )
                                        },
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
                                scanningViewModel.clickImage(true)
                            }
                        }
                    }
                }
            }
        }
    }
}


private fun getOutputDirectory(context: Context): File {
    val mediaDir1 = context.getExternalFilesDir("").let {
        File(it, "temp").apply { mkdirs() }
    }
    return mediaDir1
}

private fun deleteTemp(context: Context) {
    val mediaDir = context.getExternalFilesDir("").let {
        File(it, "temp").apply { mkdirs() }
    }
    mediaDir.deleteRecursively()

}