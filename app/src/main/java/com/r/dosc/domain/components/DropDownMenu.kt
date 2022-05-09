package com.r.dosc.domain.components

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.r.dosc.domain.util.HomeListItemDropDownMenu

val itemList = listOf(
    HomeListItemDropDownMenu.Share(),
    HomeListItemDropDownMenu.Delete(),
)

@Composable
fun DropDownMenu(
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    onSelected: (HomeListItemDropDownMenu) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(true)
    }

    DropdownMenu(
        modifier = modifier,
        expanded = isExpanded,
        onDismissRequest = {
            isExpanded = false

            onDismissRequest()
        }
    ) {

        itemList.forEach { item ->
            DropdownMenuItem(
                onClick = {
                    isExpanded = false
                    onSelected(item)
                },
            ) {
                Text(text = item.name)
            }

        }

    }

}