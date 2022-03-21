package com.r.dosc.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.r.dosc.domain.components.SetUpStatusBar
import com.r.dosc.presentation.main.MainScreenEvents
import com.r.dosc.presentation.main.MainViewModel
import com.r.dosc.domain.ui.theme.Helper_Text_Color
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun SettingsScreen(
    mainViewModel: MainViewModel
) {

    val systemUiController = rememberSystemUiController()
    val lifecycleOwner = LocalLifecycleOwner.current
    SetUpStatusBar(systemUiController, lifecycleOwner, mainViewModel, true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = 3.dp

        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "General",
                    style = TextStyle(
                        textAlign = TextAlign.Start,
                    ),
                    color = Helper_Text_Color,
                    fontSize = 17.sp
                )

                RowSwitch(
                    title = "Use dark mode",
                    helperText = "Get that whiteness out my sight",
                    mainViewModel.isDarkThemeState.value
                ) { isDarkTheme ->
                    mainViewModel.onEvent(MainScreenEvents.IsDarkTheme(isDarkTheme))
                }

                Spacer(modifier = Modifier.height(12.dp))

                RowSwitch(
                    title = "Scanning",
                    helperText = "Start scanning with file name",
                    mainViewModel.isStartWithFileNameState.value
                ) { isStartWithFileName ->
                    mainViewModel.onEvent(MainScreenEvents.IsStartWithFileName(isStartWithFileName))
                }

                Spacer(modifier = Modifier.height(12.dp))


            }

        }

    }

}

@Composable
fun RowSwitch(
    title: String,
    helperText: String,
    toggle: Boolean,
    onCheckChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp, top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .padding(top = 13.dp)
                .weight(8f)
        ) {
            Text(
                text = title,
                fontSize = 21.sp,
                color = MaterialTheme.colors.onBackground,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp,
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = helperText,
                color = Color.LightGray,
                fontSize = 15.sp,

                )
        }

        Box(
            modifier = Modifier.weight(2f),
            contentAlignment = Alignment.TopCenter

        ) {

            Switch(
                checked = toggle,
                onCheckedChange = onCheckChange,
                colors = SwitchDefaults.colors(
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray
                )
            )

        }

    }

}