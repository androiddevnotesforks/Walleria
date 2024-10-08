package com.andrii_a.walleria.ui.topics

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.paging.compose.collectAsLazyPagingItems
import com.andrii_a.walleria.R
import com.andrii_a.walleria.domain.TopicsDisplayOrder
import com.andrii_a.walleria.ui.common.components.WTitleDropdown
import com.andrii_a.walleria.ui.common.components.lists.TopicsList
import com.andrii_a.walleria.ui.util.titleRes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicsScreen(
    state: TopicsUiState,
    onEvent: (TopicsEvent) -> Unit
) {
    val lazyTopicItems by rememberUpdatedState(newValue = state.topics.collectAsLazyPagingItems())

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    val optionStringResources =
                        TopicsDisplayOrder.entries.map { it.titleRes }

                    WTitleDropdown(
                        selectedTitleRes = state.topicsDisplayOrder.titleRes,
                        titleTemplateRes = R.string.topics_title_template,
                        optionsStringRes = optionStringResources,
                        onItemSelected = { orderOptionOrdinalNum ->
                            onEvent(TopicsEvent.ChangeListOrder(orderOptionOrdinalNum))
                        }
                    )
                },
                actions = {
                    IconButton(onClick = { onEvent(TopicsEvent.SelectSearch) }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(
                                id = R.string.search
                            )
                        )
                    }

                    IconButton(onClick = { onEvent(TopicsEvent.SelectPrivateUserProfile) }) {
                        Icon(
                            imageVector = Icons.Outlined.AccountCircle,
                            contentDescription = stringResource(
                                id = R.string.user_profile_image
                            )
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ) { innerPadding ->
        val listState = rememberLazyListState()

        TopicsList(
            lazyTopicItems = lazyTopicItems,
            onClick = { id ->
                onEvent(TopicsEvent.SelectTopic(id))
            },
            addNavigationBarPadding = true,
            listState = listState,
            contentPadding = PaddingValues(
                start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                end = innerPadding.calculateEndPadding(LayoutDirection.Ltr),
                top = innerPadding.calculateTopPadding() + dimensionResource(id = R.dimen.list_top_padding),
                bottom = innerPadding.calculateBottomPadding() + dimensionResource(id = R.dimen.navigation_bar_height) * 2
            )
        )
    }
}