package com.example.ui.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.examle.data.models.DataState
import com.examle.domain.model.event.EventDetailModel
import com.example.ui.event.components.HeaderItem
import com.example.ui.event.components.TopBarItem
import com.example.ui.theme.AppBackgroundColor


@Composable
fun EventDetailScreen(
    eventId: String,
    padding: PaddingValues,
    viewModel: EventDetailViewModel = hiltViewModel()
) {

    val scrollState = rememberScrollState()
    val eventState by viewModel.eventDetail.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getEventDetail(eventId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackgroundColor)
    ) {

        when (eventState) {
            is DataState.Loading -> {}
            is DataState.Error -> {}
            is DataState.Success -> {
                Column(
                    modifier = Modifier.verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    HeaderItem((eventState as DataState.Success).data)
                    ScrollableItemsForSample()
                }
                TopBarItem(
                    padding.calculateTopPadding(),
                    scrollState,
                    { },
                    { }
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.ScrollableItemsForSample() {
    for (i in 0..20) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = "Item for scroll testing #$i"
        )
    }
}