package com.r.dosc.presentation.home

sealed class HomeScreenEvents{
    data class Delete(val index: Int): HomeScreenEvents()
}
