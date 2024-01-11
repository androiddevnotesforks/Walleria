package com.andrii_a.walleria.ui.photos

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.PhotoListDisplayOrder
import com.andrii_a.walleria.domain.PhotosListLayoutType
import com.andrii_a.walleria.ui.common.components.WTitleDropdown
import com.andrii_a.walleria.ui.common.components.lists.PhotosGrid
import com.andrii_a.walleria.ui.common.components.lists.PhotosList
import com.andrii_a.walleria.ui.util.titleRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotosScreen(
    state: PhotosUiState,
    //photosListLayoutType: PhotosListLayoutType,
    //photosLoadQuality: PhotoQuality,
    onEvent: (PhotosEvent) -> Unit
) {
    val lazyPhotoItems = state.photos.collectAsLazyPagingItems()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val optionStringResources = PhotoListDisplayOrder.entries.map { it.titleRes }

                    WTitleDropdown(
                        selectedTitleRes = state.photosListDisplayOrder.titleRes,
                        titleTemplateRes = R.string.photos_title_template,
                        optionsStringRes = optionStringResources,
                        onItemSelected = { orderOptionOrdinalNum ->
                            onEvent(PhotosEvent.ChangeListOrder(orderOptionOrdinalNum))
                        }
                    )
                },
                actions = {
                    IconButton(onClick = { onEvent(PhotosEvent.SelectSearch) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(
                                id = R.string.search
                            )
                        )
                    }

                    IconButton(onClick = { onEvent(PhotosEvent.SelectPrivateUserProfile) }) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = stringResource(
                                id = R.string.user_profile_image
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        when (state.photosListLayoutType) {
            PhotosListLayoutType.DEFAULT -> {
                val listState = rememberLazyListState()

                PhotosList(
                    lazyPhotoItems = lazyPhotoItems,
                    onPhotoClicked = { photoId ->
                        onEvent(PhotosEvent.SelectPhoto(photoId))
                    },
                    onUserProfileClicked = { userNickname ->
                        onEvent(PhotosEvent.SelectUser(userNickname))
                    },
                    isCompact = false,
                    addNavigationBarPadding = true,
                    photosLoadQuality = state.photosLoadQuality,
                    listState = listState,
                    contentPadding = PaddingValues(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        top = innerPadding.calculateTopPadding() + dimensionResource(id = R.dimen.list_top_padding),
                        bottom = innerPadding.calculateBottomPadding() + dimensionResource(id = R.dimen.navigation_bar_height) * 2
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }

            PhotosListLayoutType.MINIMAL_LIST -> {
                val listState = rememberLazyListState()

                PhotosList(
                    lazyPhotoItems = lazyPhotoItems,
                    onPhotoClicked = { photoId ->
                        onEvent(PhotosEvent.SelectPhoto(photoId))
                    },
                    onUserProfileClicked = { userNickname ->
                        onEvent(PhotosEvent.SelectUser(userNickname))
                    },
                    isCompact = true,
                    addNavigationBarPadding = true,
                    photosLoadQuality = state.photosLoadQuality,
                    listState = listState,
                    contentPadding = PaddingValues(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        top = innerPadding.calculateTopPadding() + dimensionResource(id = R.dimen.list_top_padding),
                        bottom = innerPadding.calculateBottomPadding() + dimensionResource(id = R.dimen.navigation_bar_height) * 2
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }

            PhotosListLayoutType.STAGGERED_GRID -> {
                val gridState = rememberLazyStaggeredGridState()

                PhotosGrid(
                    lazyPhotoItems = lazyPhotoItems,
                    onPhotoClicked = { photoId ->
                        onEvent(PhotosEvent.SelectPhoto(photoId))
                    },
                    photosLoadQuality = state.photosLoadQuality,
                    gridState = gridState,
                    addNavigationBarPadding = true,
                    contentPadding = PaddingValues(
                        start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                        end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                        top = innerPadding.calculateTopPadding() + dimensionResource(id = R.dimen.list_top_padding),
                        bottom = innerPadding.calculateBottomPadding() + dimensionResource(id = R.dimen.navigation_bar_height) * 2
                    ),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
