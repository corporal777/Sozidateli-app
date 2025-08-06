package com.example.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app.R
import com.example.ui.components.TextNormal
import com.example.ui.theme.HomeSearchContainerColor
import com.example.ui.theme.InputPlaceholderColor

@Composable
fun SearchItem() {
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