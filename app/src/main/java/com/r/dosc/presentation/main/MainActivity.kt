package com.r.dosc.presentation.main

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import com.r.dosc.presentation.destinations.HomeScreenDestination
import com.r.dosc.presentation.main.components.OpenDialogBox
import com.r.dosc.presentation.main.components.SetupPermissions
import com.r.dosc.domain.util.PermissionViewModel
import com.r.dosc.presentation.destinations.ScanningCameraScreenDestination
import com.r.dosc.domain.components.SetUpStatusBar
import com.r.dosc.domain.ui.theme.DarkColorPalette
import com.r.dosc.presentation.main.components.ScanningFloatingButton
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.navigation.navigateTo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@ExperimentalPermissionsApi
@ExperimentalAnimationApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().apply {
            setKeepOnScreenCondition { mainViewModel.duration.value }
        }
        setContent {
            DoscTheme(
                darkTheme = mainViewModel.isDarkThemeState.value || isSystemInDarkTheme()
            ) {
                val systemUiController = rememberSystemUiController()
                val lifecycleOwner = LocalLifecycleOwner.current
                SetUpStatusBar(systemUiController, lifecycleOwner, mainViewModel, false)

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


                    val topBarColor = animateColorAsState(
                        if (shouldShowBottomNavBarTopBarFloatBtn(navBackStackEntry)) {
                            MaterialTheme.colors.primarySurface
                        } else {
                            Color.Black
                        }
                    )


                    navController.addOnDestinationChangedListener(listener = { _, dest, _ ->
                        mainViewModel.onEvent(MainScreenEvents.TopAppBarTitle(dest.route))

                    })

                    Scaffold(
                        topBar = {
                            AnimatedVisibility(
                                visible = shouldShowBottomNavBarTopBarFloatBtn(navBackStackEntry),
                                enter = expandVertically(),
                                exit = shrinkVertically()
                            ) {
                                TopAppBar(
                                    title = {
                                        Text(
                                            text = mainViewModel.topAppBarTitle.value,
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
                                    mainViewModel = mainViewModel,
                                    cameraPermissionState = cameraPermissionState,
                                    onClick = {
                                        mainViewModel.scanningStart(true)

                                        systemUiController.setStatusBarColor(
                                            color = DarkColorPalette.primarySurface
                                        )
                                        systemUiController.setNavigationBarColor(
                                            color = DarkColorPalette.primarySurface
                                        )
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
                                    dependency(mainViewModel)
                                }
                                if (destination is SettingsScreenDestination) {
                                    dependency(mainViewModel)
                                }


                            }
                        )
                    }

                    if (mainViewModel.scanningStart.value == true) {

                        navController.navigateTo(ScanningCameraScreenDestination("")) {
                            launchSingleTop = true
                            popUpTo(HomeScreenDestination.route)

                        }
                        mainViewModel.scanningStart(null)

                    }


                    if (mainViewModel.isOpenDialogBox.value) {
                        OpenDialogBox(
                            viewModel = mainViewModel,
                            onSubmit = { fileName ->
                                navController.navigateTo(ScanningCameraScreenDestination(fileName)) {
                                    launchSingleTop = true
                                    popUpTo(HomeScreenDestination.route)

                                }
                            }
                        )
                    }

                    LaunchedEffect(key1 = true) {
                        mainViewModel.uiEvent.collect { event ->
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



