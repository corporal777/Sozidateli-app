package com.example.ui.home

import android.os.Parcelable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.app.R
import com.example.extensions.clickable
import com.example.navigation.Route
import com.example.ui.components.EventCardItem
import com.example.ui.components.TextNormal
import com.example.ui.components.TextSemibold
import com.example.ui.home.components.AuthButtonItem
import com.example.ui.home.components.CustomToolbar
import com.example.ui.home.components.SearchItem
import com.example.ui.home.components.rememberToolbarScrollBehavior
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.AuthHorizontalPadding
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.HomeSearchContainerColor
import com.example.ui.theme.InputPlaceholderColor
import com.example.ui.theme.MainBrownColor
import kotlinx.parcelize.Parcelize

@Composable
fun HomeScreen(
    paddingValues: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    onAuthClick: (String) -> Unit
) {

    val events = viewModel.events.collectAsLazyPagingItems()
    val scrollBehavior = rememberToolbarScrollBehavior()

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
            items(items = events.itemSnapshotList, key = { it?.id ?: 0 }) {
                it?.let { event -> EventCardItem(event, false) }
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

