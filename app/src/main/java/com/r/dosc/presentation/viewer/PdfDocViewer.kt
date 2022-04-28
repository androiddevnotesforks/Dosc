package com.r.dosc.presentation.viewer

import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.imageLoader
import com.r.dosc.domain.ui.theme.GrayShade_light
import com.r.dosc.presentation.viewer.components.PdfListPages
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
import kotlin.math.sqrt

@Destination
@Composable
fun PdfDocViewer(
    navigator: DestinationsNavigator,
    file: File,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp)
) {

    val docListState = rememberLazyListState()

    val topPadding by remember {
        derivedStateOf {
            docListState.firstVisibleItemScrollOffset <= 0
        }
    }

    val rendererScope = rememberCoroutineScope()
    val mutex = remember { Mutex() }
    val renderer by produceState<PdfRenderer?>(null, file) {
        rendererScope.launch(Dispatchers.IO) {
            val input = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            value = PdfRenderer(input)
        }
        awaitDispose {
            val currentRenderer = value
            rendererScope.launch(Dispatchers.IO) {
                mutex.withLock {
                    currentRenderer?.close()
                }
            }
        }
    }
    val context = LocalContext.current
    val imageLoader = LocalContext.current.imageLoader
    val imageLoadingScope = rememberCoroutineScope()
    val pageCount by remember(renderer) { derivedStateOf { renderer?.pageCount ?: 0 } }

    val pageCountText: String by remember {
        derivedStateOf {
            docListState.pageIndex(pageCount)

        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = file.name,
                        color = Color.White,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navigator.navigateUp()
                    }) {
                        Icon(Icons.Filled.ArrowBack, "backIcon")
                    }
                }
            )
        },

        floatingActionButton = {
            FloatingActionButton(
                onClick = {

                },
                modifier = Modifier
                    .height(35.dp)
                    .offset(x = (20).dp),
                shape = RoundedCornerShape(
                    topStart = 20.dp,
                    bottomStart = 20.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                ),
                backgroundColor = GrayShade_light.copy(alpha = 0.5f),
                elevation = FloatingActionButtonDefaults.elevation(0.dp)

            ) {
                Text(text = "$pageCountText/$pageCount", fontSize = 15.sp)

            }
        }
    ) {
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val width = with(LocalDensity.current) { maxWidth.toPx() }.toInt()
            val height = (width * sqrt(2f)).toInt()

            Column(
                modifier = Modifier.padding(bottom = 8.dp)
            ) {

                AnimatedVisibility(visible = topPadding) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                    )
                }

                PdfListPages(
                    docListState,
                    verticalArrangement,
                    pageCount,
                    height,
                    file,
                    context,
                    imageLoader,
                    imageLoadingScope,
                    width,
                    mutex,
                    renderer,
                )

            }
        }
    }

}

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

fun LazyListState.isScrolledToEnd() =
    layoutInfo.visibleItemsInfo.lastOrNull()?.index == layoutInfo.totalItemsCount - 1
