package com.example.vkdz2.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.vkdz2.AppModule
import com.example.vkdz2.GiphyUiState
import com.example.vkdz2.data.GifItem
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.vkdz2.AppViewModel
import kotlinx.coroutines.delay


@Composable
fun GifCard(
    gif: GifItem,
    index: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .padding(4.dp)
            .aspectRatio(gif.width.toFloat() / gif.height.toFloat())
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .clickable { onClick(index) }
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(gif.url)
                .decoderFactory(GifDecoder.Factory())
                .crossfade(true)
                .build(),
            contentDescription = gif.title,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
    }
}

@Composable
fun MainScreen() {

    val viewModel = viewModel<AppViewModel>(factory = viewModelFactory {
        initializer {
            AppViewModel(AppModule.repository)
        }
    })
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    val gridState = rememberLazyStaggeredGridState()

    val shouldLoadMore = remember {
        derivedStateOf {
            val layoutInfo = gridState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()

            lastVisibleItem?.index?.let { lastIndex ->
                totalItems > 0 && lastIndex >= totalItems - 5
            } ?: false
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && uiState is GiphyUiState.Success) {
            delay(300)
            viewModel.loadMore()
        }
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        when (val state = uiState) {

            is GiphyUiState.Loading -> {
                LoadingIndicator()
            }

            is GiphyUiState.Success -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Adaptive(minSize = 150.dp),
                        state = gridState,
                        modifier = Modifier.weight(1f)
                    ) {
                        items(state.gifs.size) { index ->
                            val gif = state.gifs[index]
                            GifCard(
                                gif = gif,
                                index = index,
                                onClick = { clickedIndex ->
                                    Toast.makeText(
                                        context,
                                        "GIF #${clickedIndex + 1}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            )
                        }

                        if (state.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                    }
                }
            }

            is GiphyUiState.Error -> {
                ErrorScreen(
                    message = state.message,
                    onRetry = viewModel::retry
                )
            }
        }
    }
}