package com.r.dosc.presentation.scanning

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.domain.ui.theme.DoscTheme
import com.r.dosc.presentation.destinations.HomeScreenDestination
import com.r.dosc.presentation.main.MainScreenEvents
import com.r.dosc.presentation.main.MainViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator


@ExperimentalPermissionsApi
@Destination
@Composable
fun ScanningCameraScreen(
    navigator: DestinationsNavigator
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
                        color = Color.Black
                    )
                    systemUiController.setNavigationBarColor(
                        color = Color.Black
                    )
                }

            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )

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
                        }
                    )

                }


            },

            ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                AndroidView(
                    factory = { ctx ->
                        val previewView = PreviewView(ctx)
                        val executor = ContextCompat.getMainExecutor(ctx)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            val preview = Preview.Builder().build().also {
                                it.setSurfaceProvider(previewView.surfaceProvider)
                            }

                            val cameraSelector = CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                .build()

                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        }, executor)
                        previewView
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(70f),
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(25f)
                        .background(color = MaterialTheme.colors.primarySurface)
                ) {

                }


            }


        }


    }


}

