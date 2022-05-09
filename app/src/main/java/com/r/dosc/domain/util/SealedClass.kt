package com.r.dosc.domain.util

sealed class HomeListItemDropDownMenu(
    val name: String
) {
    class Share : HomeListItemDropDownMenu("Share")
    class Delete : HomeListItemDropDownMenu("Delete")
}