package com.example.ui.home

import android.os.Parcelable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.app.R
import com.example.extensions.clickable
import com.example.ui.components.TextNormal
import com.example.ui.components.TextSemibold
import com.example.ui.home.components.CustomToolbar
import com.example.ui.home.components.rememberToolbarScrollBehavior
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.HomeSearchContainerColor
import com.example.ui.theme.InputPlaceholderColor
import com.example.ui.theme.MainBrownColor
import kotlinx.parcelize.Parcelize

@Composable
fun HomeScreen(paddingValues: PaddingValues) {

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
            rightContent = { AuthButtonItem() },
            scrollBehavior = scrollBehavior
        )

        LazyColumn() {
            scrollableItemsForSample()
        }
    }
}

@Composable
private fun AuthButtonItem() {
    TextSemibold(
        modifier = Modifier
            .clip(CircleShape)
            .wrapContentWidth()
            .background(MainBrownColor)
            .clickable(Color.White) {

            }
            .padding(horizontal = 15.dp, vertical = 6.dp),
        text = stringResource(R.string.auth_label_login),
        fontSize = 15.sp,
        color = Color.White,
        textAlign = TextAlign.Center
    )
}

@Composable
private fun SearchItem() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .fillMaxWidth()
            .height(40.dp)
            .background(HomeSearchContainerColor)
            .padding(start = 10.dp, end = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search_new),
            contentDescription = "",
            tint = Color.Unspecified
        )

        TextNormal(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp),
            text = stringResource(R.string.search_input_hint),
            fontSize = 17.sp,
            color = InputPlaceholderColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
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

