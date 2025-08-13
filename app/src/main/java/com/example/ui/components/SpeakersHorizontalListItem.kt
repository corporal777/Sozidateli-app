package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.examle.domain.model.user.SpeakerModel
import com.example.ui.theme.DefaultHorizontalPadding

@Composable
fun SpeakersHorizontalListItem(speakers : List<SpeakerModel>) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = DefaultHorizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(items = speakers, key = { it.id ?: 0 }) {
            SpeakerItem(it)
        }
    }
}