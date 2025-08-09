package com.example.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.app.R
import com.example.extensions.showEventAgreementDialog
import com.example.navigation.Route
import com.example.ui.components.EventAction
import com.example.ui.components.EventCardItem
import com.example.ui.home.components.AuthButtonItem
import com.example.ui.home.components.CustomToolbar
import com.example.ui.home.components.SearchItem
import com.example.ui.home.components.rememberToolbarScrollBehavior
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.DefaultHorizontalPadding

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    onAuthClick: (String) -> Unit
) {

    val context = LocalContext.current
    val events = viewModel.events.collectAsLazyPagingItems()
    val scrollBehavior = rememberToolbarScrollBehavior()

    LaunchedEffect(events.itemSnapshotList) {
        viewModel.setEventsLocal(events.itemSnapshotList.items)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundColor)
            .padding(top = paddingValues.calculateTopPadding())
            .nestedScroll(scrollBehavior.nestedScrollConnection),
    ) {

        CustomToolbar(
            modifier = Modifier,
            collapsingTitle = stringResource(R.string.tab_recommended_title),
            additionalContent = { SearchItem() },
            rightContent = { AuthButtonItem() { onAuthClick(Route.AuthorizationScreen.route) } },
            scrollBehavior = scrollBehavior
        )


        LazyColumn(
            contentPadding = PaddingValues(
                top = 10.dp,
                bottom = 50.dp,
                start = DefaultHorizontalPadding,
                end = DefaultHorizontalPadding
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(items = events.itemSnapshotList, key = { it?.id ?: 0 }) { event ->
                if (event == null) return@items
                EventCardItem(event, false) {
                    when (it) {
                        EventAction.REGISTER -> {
                            showEventAgreementDialog(context, event) {
                                viewModel.onActionRegister(event, it)
                            }
                        }

                        EventAction.CANCEL -> viewModel.onActionCancel(event)
                        else -> {

                        }
                    }
                }
            }
        }
    }
}


private fun LazyListScope.scrollableItemsForSample() {
    for (i in 0..100) {
        item("scroll_test_$i") {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                text = "Item for scroll testing #$i"
            )
        }
    }
}

