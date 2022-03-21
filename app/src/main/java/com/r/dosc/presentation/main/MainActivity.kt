package com.r.dosc.presentation.main

import android.Manifest
import android.os.Bundle
import android.view.animation.BounceInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.domain.navigation.BottomBar
import com.r.dosc.presentation.NavGraphs
import com.r.dosc.presentation.destinations.SettingsScreenDestination
import com.r.dosc.domain.ui.theme.DoscTheme
import com.r.dosc.R
import com.r.dosc.domain.constants.Permissions
import com.r.dosc.presentation.destinations.HomeScreenDestination
import com.r.dosc.presentation.main.components.OpenDialogBox
import com.r.dosc.presentation.main.components.SetupPermissions
import com.r.dosc.domain.util.PermissionViewModel
import com.r.dosc.presentation.destinations.ScanningCameraScreenDestination
import com.r.dosc.domain.components.SetUpStatusBar
import com.r.dosc.presentation.main.components.ScanningFloatingButton
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigateTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ExperimentalPermissionsApi
@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { viewModel.duration.value }
        }
        setContent {
            DoscTheme(
                darkTheme = viewModel.isDarkThemeState.value
            ) {
                val systemUiController = rememberSystemUiController()
                val lifecycleOwner = LocalLifecycleOwner.current
                SetUpStatusBar(systemUiController, lifecycleOwner, viewModel, false)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val permissionViewModel: PermissionViewModel by viewModels()
                    SetupPermissions(permissionViewModel)
                    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)


                    val navController = rememberAnimatedNavController()
                    val scaffoldState = rememberScaffoldState()
                    val coroutineScope = rememberCoroutineScope()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()


                    var topBarTitle by rememberSaveable {
                        mutableStateOf("Dosc")
                    }
                    val topBarColor = animateColorAsState(
                        if (shouldShowBottomNavBarTopBarFloatBtn(navBackStackEntry)) {
                            MaterialTheme.colors.primarySurface
                        } else {
                            Color.Black
                        }
                    )

                    var startScanning by rememberSaveable {
                        mutableStateOf(false)
                    }

                    navController.addOnDestinationChangedListener(listener = { _, dest, _ ->
                        topBarTitle = if (dest.route == SettingsScreenDestination.route) {
                            "Settings"
                        } else {
                            "Dosc"
                        }

                    })

                    Scaffold(
                        topBar = {
                            AnimatedVisibility (
                                visible = shouldShowBottomNavBarTopBarFloatBtn(navBackStackEntry),
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = topBarTitle,
                                            fontSize = 30.sp,
                                            color = Color.White,
                                        )
                                    },
                                    backgroundColor = topBarColor.value
                                )
                            }
                        },
                        bottomBar = {
                            AnimatedVisibility(
                                visible = shouldShowBottomNavBarTopBarFloatBtn(navBackStackEntry),
                                enter = slideInVertically { height -> height } + fadeIn(),
                                exit = slideOutVertically { height -> height } + fadeOut()

                            ) {
                                BottomBar(navController = navController)

                            }

                        },
                        floatingActionButton = {
                            AnimatedVisibility(
                                visible = shouldShowBottomNavBarTopBarFloatBtn(navBackStackEntry),
                                enter = slideInVertically { height -> height },
                                exit = slideOutVertically { height -> height } + fadeOut()
                            ) {
                                ScanningFloatingButton(
                                    permissionViewModel = permissionViewModel,
                                    mainViewModel = viewModel,
                                    cameraPermissionState = cameraPermissionState,
                                    onClick = {
                                        startScanning = true
                                    }
                                )
                            }

                        },
                        floatingActionButtonPosition = FabPosition.Center,
                        isFloatingActionButtonDocked = true,
                        scaffoldState = scaffoldState
                    ) {
                        DestinationsNavHost(
                            navGraph = NavGraphs.root,
                            navController = navController,
                            dependenciesContainerBuilder = {
                                if (destination is HomeScreenDestination) {
                                    dependency(permissionViewModel)
                                    dependency(viewModel)
                                }
                                if (destination is SettingsScreenDestination) {
                                    dependency(viewModel)
                                }
                            }
                        )
                    }



                    if (startScanning) {
                        systemUiController.setStatusBarColor(
                            color = Color.Black
                        )
                        navController.navigateTo(ScanningCameraScreenDestination) {
                            launchSingleTop = true

                        }
                        startScanning = false
                    }


                    if (viewModel.isOpenDialogBox.value) {
                        OpenDialogBox(viewModel)
                    }

                    LaunchedEffect(key1 = true) {
                        viewModel.uiEvent.collect { event ->
                            when (event) {
                                is MainScreenEvents.ShowSnackBar -> {
                                    showSnackBar(
                                        event,
                                        scaffoldState,
                                        coroutineScope
                                    )
                                }
                                else -> Unit
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalPermissionsApi
private fun shouldShowBottomNavBarTopBarFloatBtn(backstackEntry: NavBackStackEntry?): Boolean {
    return backstackEntry?.destination?.route in listOf(
        HomeScreenDestination.route,
        SettingsScreenDestination.route,
    )
}


fun showSnackBar(
    event: MainScreenEvents.ShowSnackBar,
    scaffoldState: ScaffoldState,
    coroutineScope: CoroutineScope,
) {
    coroutineScope.launch {

        scaffoldState.snackbarHostState.showSnackbar(
            message = event.uiText
        )
    }
}
