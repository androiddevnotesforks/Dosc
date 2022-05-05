package com.r.dosc.domain.util

import androidx.compose.foundation.lazy.LazyListState

fun LazyListState.pageIndex(count: Int): String {
    val lastItem = layoutInfo.visibleItemsInfo.lastOrNull()
    if (lastItem != null) {
        return if (lastItem.size + lastItem.offset <= layoutInfo.viewportEndOffset && isScrolledToEnd()) {
            count.toString()
        } else {
            lastItem.index.toString()
        }
    }
    return "1"
}

fun LazyListState.pageIndexHorizontal(): String {
    val ind = firstVisibleItemIndex + 1
    return ind.toString()
}

fun LazyListState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
