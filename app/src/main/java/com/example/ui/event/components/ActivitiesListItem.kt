package com.example.ui.event.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.examle.domain.model.event.EventActivityModel
import com.example.app.R
import com.example.ui.components.AppLabelItem
import com.example.ui.components.EventActivityItem
import com.example.ui.theme.DefaultHorizontalPadding

@Composable
fun ActivitiesListItem(activities : List<EventActivityModel>) {

    AppLabelItem(
        text = stringResource(R.string.event_program),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = DefaultHorizontalPadding,
                end = DefaultHorizontalPadding,
                top = 30.dp,
                bottom = 5.dp
            )
    )

    activities.forEach {
        EventActivityItem(it)
    }
}