package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.examle.domain.model.event.EventActivityModel
import com.example.app.R
import com.example.extensions.clickable
import com.example.extensions.coloredString
import com.example.ui.theme.AmbientShadowColor
import com.example.ui.theme.BtnBackgroundGreenColor
import com.example.ui.theme.BtnBackgroundWhiteGhostColor
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.EventActivityBackgroundColor
import com.example.ui.theme.EventDetailDateColor
import com.example.ui.theme.SpotShadowColor
import com.example.ui.theme.SpotShadowHalfColor

@Composable
fun EventActivityItem(model: EventActivityModel) {

    ConstraintLayout(
        modifier = Modifier
            .padding(horizontal = DefaultHorizontalPadding, vertical = 10.dp)
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(12.dp),
                ambientColor = AmbientShadowColor,
                spotColor = SpotShadowColor
            )
            .background(EventActivityBackgroundColor)
            .fillMaxWidth()
    ) {

        val (time, name, auditory, desc, action) = createRefs()


        TextBold(
            text = model.holdingDate ?: "",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .constrainAs(time) { top.linkTo(parent.top, 15.dp) }
        )

        TextBold(
            text = model.title ?: "",
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .constrainAs(name) { top.linkTo(time.bottom, 8.dp) }
        )

        TextAnnotatedBold(
            text = buildAnnotatedString {
                append(coloredString(R.string.label_auditory, EventDetailDateColor))
                append(model.auditorium)
            },
            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .constrainAs(auditory) { top.linkTo(name.bottom, 8.dp) }
        )

        TextRegular(
            text = model.description ?: "",
            color = Color.Black,
            fontSize = 14.sp,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp)
                .constrainAs(desc) { top.linkTo(auditory.bottom, 8.dp) }
        )

        TextSemibold(
            text = stringResource(R.string.add_to_timetable),
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(BtnBackgroundGreenColor)
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .constrainAs(action) {
                    top.linkTo(desc.bottom, 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                    end.linkTo(parent.end, 15.dp)
                }
        )
    }


}
