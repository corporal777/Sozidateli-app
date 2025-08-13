package com.example.ui.event.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.examle.domain.model.user.SpeakerModel
import com.example.app.R
import com.example.extensions.clickable
import com.example.extensions.verticalGradientBrush
import com.example.ui.components.TextBold
import com.example.ui.theme.EndSpeakerGradientColor
import com.example.ui.theme.MainBrownColor
import com.example.ui.theme.TopSpeakerGradientColor

@Composable
fun ShowSpeakerItem(onClick :() -> Unit) {
    ConstraintLayout(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .width(140.dp)
            .height(180.dp)
            .background(MainBrownColor)
            .clickable(Color.White, onClick = onClick)
    ) {

        val (name, gradient, icon) = createRefs()

        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .background(verticalGradientBrush(listOf(TopSpeakerGradientColor, EndSpeakerGradientColor)))
                .constrainAs(gradient) { bottom.linkTo(parent.bottom) }
        )

        TextBold(
            text = stringResource(R.string.show_all_speakers),
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .constrainAs(name){
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )

        Icon(
            painter = painterResource(R.drawable.ic_show_speakers),
            tint = Color.Unspecified,
            contentDescription = "",
            modifier = Modifier.constrainAs(icon){
                top.linkTo(name.bottom, 10.dp)
                start.linkTo(parent.start, 10.dp)
            }
        )
    }


}