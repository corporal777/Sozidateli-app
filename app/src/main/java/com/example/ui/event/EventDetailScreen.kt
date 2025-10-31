package com.example.ui.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examle.data.models.DataState
import com.examle.data.models.ResponseState
import com.examle.domain.model.event.EventActionStatus
import com.examle.domain.model.event.EventModel
import com.example.app.R
import com.example.ui.agreement.EventAgreementBottomSheet
import com.example.ui.components.AppLabelItem
import com.example.ui.event.components.HeaderItem
import com.example.ui.event.components.HeaderShimmerItem
import com.example.ui.event.components.OrganizationItem
import com.example.ui.event.components.ActivitiesListItem
import com.example.ui.event.components.SpeakersListItem
import com.example.ui.event.components.TopBarItem
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.DefaultHorizontalPadding


@Composable
fun EventDetailScreen(
    eventId: String,
    padding: PaddingValues,
    viewModel: EventDetailViewModel = hiltViewModel()
) {

    val scrollState = rememberScrollState()
    val eventState = viewModel.eventDetail.collectAsState().value
    LaunchedEffect(Unit) { viewModel.getEventDetail(eventId) }

    val isLoadingState by viewModel.loading.collectAsState()
    var isLoading by remember { mutableStateOf(false) }
    LaunchedEffect(isLoadingState) { isLoading = isLoadingState }

    Box(
        Modifier
            .fillMaxSize()
            .background(AppBackgroundColor)
    ) {

        when (eventState) {
            is DataState.Loading -> HeaderShimmerItem(padding)
            is DataState.Error -> {}
            is DataState.Success -> {
                Column(
                    modifier = Modifier.verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    HeaderItem(eventState.data, isLoading){
                        when (it) {
                            EventActionStatus.REGISTER -> viewModel.onActionRegister(eventState.data)
                            EventActionStatus.WITHDRAW -> viewModel.onActionCancel(eventState.data)
                            else -> { }
                        }
                    }

                    OrganizationItem(eventState.data.organization)

                    SpeakersListItem(eventState.data.eventSpeakers)

                    ActivitiesListItem(eventState.data.activities)

                    ScrollableItemsForSample()
                }

                TopBarItem(
                    padding.calculateTopPadding(),
                    scrollState,
                    eventState.data,
                    onBackClick = { },
                    onAddClick = { viewModel.addOrRemoveEventFavorite(eventState.data) }
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.ScrollableItemsForSample() {
    for (i in 0..10) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = "Item for scroll testing #$i"
        )
    }
}