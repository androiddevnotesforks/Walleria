package com.andrii_a.walleria.ui.topic_details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.ui.common.components.ErrorBanner
import com.andrii_a.walleria.ui.common.components.lists.PhotosGrid
import com.andrii_a.walleria.ui.common.components.lists.PhotosList
import kotlinx.coroutines.launch

@Composable
fun TopicDetailsScreen(
    state: TopicDetailsUiState,
    onEvent: (TopicDetailsEvent) -> Unit
) {
    when {
        state.isLoading -> {
            LoadingStateContent(
                onNavigateBack = { onEvent(TopicDetailsEvent.GoBack) }
            )
        }

        !state.isLoading && state.error == null && state.topic != null -> {
            SuccessStateContent(
                state = state,
                onEvent = onEvent,
            )
        }

        else -> {
            ErrorStateContent(
                onRetry = {
                    state.error?.onRetry?.invoke()
                },
                onNavigateBack = { onEvent(TopicDetailsEvent.GoBack) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoadingStateContent(onNavigateBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorStateContent(
    onRetry: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back),
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        ErrorBanner(
            onRetry = onRetry,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuccessStateContent(
    state: TopicDetailsUiState,
    onEvent: (TopicDetailsEvent) -> Unit,
) {
    val topic = state.topic!!

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = topic.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onEvent(TopicDetailsEvent.GoBack) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onEvent(TopicDetailsEvent.OpenFilterDialog) }) {
                        Icon(
                            imageVector = Icons.Outlined.FilterList,
                            contentDescription = stringResource(id = R.string.filter)
                        )
                    }

                    IconButton(
                        onClick = {
                            onEvent(TopicDetailsEvent.OpenInBrowser(topic.links?.html))
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.OpenInBrowser,
                            contentDescription = stringResource(id = R.string.open_in_browser)
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val listState = rememberLazyListState()
        val gridState = rememberLazyStaggeredGridState()

        val topicPhotosLazyItems = state.topicPhotos.collectAsLazyPagingItems()

        when (state.photosListLayoutType) {
            PhotosListLayoutType.DEFAULT -> {
                PhotosList(
                    lazyPhotoItems = topicPhotosLazyItems,
                    headerContent = {
                        TopicDetailsDescriptionHeader(
                            topic = topic,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    },
                    onPhotoClicked = { id ->
                        onEvent(TopicDetailsEvent.SelectPhoto(id))
                    },
                    onUserProfileClicked = { nickname ->
                        onEvent(TopicDetailsEvent.SelectUser(nickname))
                    },
                    isCompact = false,
                    photosLoadQuality = state.photosLoadQuality,
                    listState = listState,
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            PhotosListLayoutType.MINIMAL_LIST -> {
                PhotosList(
                    lazyPhotoItems = topicPhotosLazyItems,
                    headerContent = {
                        TopicDetailsDescriptionHeader(
                            topic = topic,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    },
                    onPhotoClicked = { id ->
                        onEvent(TopicDetailsEvent.SelectPhoto(id))
                    },
                    onUserProfileClicked = { nickname ->
                        onEvent(TopicDetailsEvent.SelectUser(nickname))
                    },
                    isCompact = true,
                    photosLoadQuality = state.photosLoadQuality,
                    listState = listState,
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            PhotosListLayoutType.STAGGERED_GRID -> {
                PhotosGrid(
                    lazyPhotoItems = topicPhotosLazyItems,
                    headerContent = {
                        TopicDetailsDescriptionHeader(
                            topic = topic,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    },
                    onPhotoClicked = { id ->
                        onEvent(TopicDetailsEvent.SelectPhoto(id))
                    },
                    photosLoadQuality = state.photosLoadQuality,
                    gridState = gridState,
                    contentPadding = innerPadding,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        val scope = rememberCoroutineScope()

        if (state.isFilterDialogOpened) {
            val bottomPadding =
                WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

            ModalBottomSheet(
                onDismissRequest = { onEvent(TopicDetailsEvent.DismissFilterDialog) },
                sheetState = bottomSheetState,
                windowInsets = WindowInsets(0)
            ) {
                TopicPhotosFilterBottomSheet(
                    topicPhotosFilters = state.topicPhotosFilters,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = bottomPadding
                    ),
                    onApplyClick = onEvent,
                    onDismiss = {
                        scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                            if (!bottomSheetState.isVisible) {
                                onEvent(TopicDetailsEvent.DismissFilterDialog)
                            }
                        }
                    }
                )
            }
        }
    }
}