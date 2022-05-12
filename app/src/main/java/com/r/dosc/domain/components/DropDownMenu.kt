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
    expanded: Boolean,
    onDeleteCheck:Boolean,
    modifier: Modifier,
    onDismissRequest: () -> Unit,
    onShare: () -> Unit,
    onDelete: @Composable (HomeListItemDropDownMenu) -> Unit,
) {
    var isExpanded by remember {
        mutableStateOf(expanded)
    }

    var onDeleteClicked by remember {
        mutableStateOf(onDeleteCheck)
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
                    when (item) {
                        is HomeListItemDropDownMenu.Share -> {
                            onShare()
                            isExpanded = false

                            onDismissRequest()

                        }
                        is HomeListItemDropDownMenu.Delete -> {
                            onDeleteClicked = true

                        }
                    }


                },
            ) {
                Text(text = item.name)
            }

        }

    }

    if (onDeleteClicked) {
        onDelete(HomeListItemDropDownMenu.Delete())

    }

}

