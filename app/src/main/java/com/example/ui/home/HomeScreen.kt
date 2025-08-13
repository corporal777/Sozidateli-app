package com.example.ui.home

import android.util.Log
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.examle.domain.model.event.EventActionStatus
import com.examle.domain.model.event.EventModel
import com.example.app.R
import com.example.extensions.showEventAgreementDialog
import com.example.navigation.Route
import com.example.ui.agreement.EventAgreementBottomSheet
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
    onEventClick: (String) -> Unit
) {

    val scrollBehavior = rememberToolbarScrollBehavior()
    val events = viewModel.events.collectAsLazyPagingItems()
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
            rightContent = { AuthButtonItem() { onEventClick(Route.AuthorizationScreen.route) } },
            scrollBehavior = scrollBehavior
        )

        LazyColumn(
            contentPadding = PaddingValues(
                top = 10.dp,
                bottom = 150.dp,
//                start = DefaultHorizontalPadding,
//                end = DefaultHorizontalPadding
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(items = events.itemSnapshotList, key = { it?.id ?: 0 }) { event ->
                if (event == null) return@items
                EventCardItem(
                    event,
                    onItem = { onEventClick.invoke(Route.EventDetailScreen.createWay(it)) },
                    onAction = {
                        when (event.actionStatus) {
                            EventActionStatus.REGISTER -> viewModel.onActionRegister(event)
                            EventActionStatus.WITHDRAW -> viewModel.onActionCancel(event)
                            else -> onEventClick(Route.AuthorizationScreen.route)
                        }
                    })
            }
        }
    }
}


