package com.r.dosc.domain.models

sealed class SortDropDownList(
    val id: Int,
    val name: String,
    var isSelected: Boolean
) {
    class Title : SortDropDownList(1,"Title", false)
    class Date : SortDropDownList(2,"Date", true)
}