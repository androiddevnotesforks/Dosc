package com.r.dosc.presentation.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.r.dosc.data.preference.PreferenceStorage
import com.r.dosc.presentation.destinations.SettingsScreenDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val HOME_SCREEN = "Dosc"
const val SETTINGS_SCREEN = "Settings"

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefStorage: PreferenceStorage,
) : ViewModel() {

    private val _duration = MutableStateFlow(true)
    val duration = _duration.asStateFlow()

    private val _updateDocList = MutableStateFlow(false)
    val updateDocList = _updateDocList.asStateFlow()

    private val _uiEvent = Channel<MainScreenEvents>()
    val uiEvent = _uiEvent.receiveAsFlow()

    val isDarkThemeState = mutableStateOf(false)
    val isStartWithFileNameState = mutableStateOf(false)
    val isOpenDialogBox = mutableStateOf(false)
    val scanningStart = mutableStateOf<Boolean?>(null)
    val topAppBarTitle = mutableStateOf("Dosc")
    val lifecycleEvent = mutableStateOf(Lifecycle.Event.ON_ANY)


    init {
        viewModelScope.launch {
            isDarkThemeState.value = prefStorage.isDarkTheme.first()
            isStartWithFileNameState.value = prefStorage.isStartWithFileName.first()

            delay(2500L)
            _duration.value = false

        }
    }

    fun onEvent(events: MainScreenEvents) {
        when (events) {
            is MainScreenEvents.TopAppBarTitle -> {
                if (events.route == SettingsScreenDestination.route) {
                    setTopAppBarTitle(SETTINGS_SCREEN)
                } else {
                    setTopAppBarTitle(HOME_SCREEN)
                }
            }

            is MainScreenEvents.ShowSnackBar -> {
                setUiEvent(MainScreenEvents.ShowSnackBar(events.uiText))
            }
            is MainScreenEvents.IsDarkTheme -> {
                viewModelScope.launch {
                    setDarkTheme(events.isDarkTheme)
                }
            }
            is MainScreenEvents.IsStartWithFileName -> {
                viewModelScope.launch {
                    setStartWithFileName(events.isStartWithFileName)
                }
            }
            is MainScreenEvents.OpenDialog -> {
                setDialogBox(events.open)
            }

            is MainScreenEvents.LifecycleEvents -> {
                setLifecycleEvent(events.lifecycleEvents)
            }

        }
    }

    private fun setTopAppBarTitle(title: String) {
        topAppBarTitle.value = title
    }

    fun scanningStart(start: Boolean?) {
        scanningStart.value = start
    }

    private fun setDialogBox(open: Boolean) {
        isOpenDialogBox.value = open
    }

    private fun setLifecycleEvent(lifecycleEvents: Lifecycle.Event) {
        lifecycleEvent.value = lifecycleEvents
    }

    private fun setUiEvent(event: MainScreenEvents) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

    private suspend fun setDarkTheme(isDarkTheme: Boolean) {
        isDarkThemeState.value = isDarkTheme
        prefStorage.setIsDarkTheme(isDarkTheme)
    }

    private suspend fun setStartWithFileName(isStartWithFileName: Boolean) {
        isStartWithFileNameState.value = isStartWithFileName
        prefStorage.setIsStartWithFileName(isStartWithFileName = isStartWithFileName)
    }

    fun updateDocList(isUpdate: Boolean) {
        viewModelScope.launch {
            _updateDocList.emit(isUpdate)
        }
    }



}