package com.example.ui.event.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.examle.domain.model.user.SpeakerModel
import com.example.app.R
import com.example.ui.components.AppLabelItem
import com.example.ui.components.SpeakerItem
import com.example.ui.theme.DefaultHorizontalPadding

@Composable
fun SpeakersListItem(speakers: List<SpeakerModel>) {
    if (speakers.isNotEmpty()){
        AppLabelItem(
            text = stringResource(R.string.speakers),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = DefaultHorizontalPadding,
                    end = DefaultHorizontalPadding,
                    top = 20.dp,
                    bottom = 10.dp
                )
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = DefaultHorizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = if (speakers.size > 5) speakers.subList(0, 5) else speakers,
                key = { it.id ?: 0 }
            ) {
                SpeakerItem(it)
            }

            if (speakers.size > 5) item(100L) { ShowSpeakerItem {  } }
        }
    }
}