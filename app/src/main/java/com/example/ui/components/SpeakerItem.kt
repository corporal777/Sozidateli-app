package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.examle.domain.model.user.SpeakerModel
import com.example.extensions.verticalGradientBrush
import com.example.ui.theme.EndSpeakerGradientColor
import com.example.ui.theme.TopSpeakerGradientColor

@Composable
fun SpeakerItem(speaker: SpeakerModel) {
    ConstraintLayout(
        Modifier
            .clip(RoundedCornerShape(10.dp))
            .width(140.dp)
            .height(180.dp)
    ) {

        val (image, name, gradient) = createRefs()

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(image) { },
            model = ImageRequest.Builder(LocalContext.current)
                .data(speaker.image)
                .crossfade(300)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .height(120.dp)
                .fillMaxWidth()
                .background(verticalGradientBrush(listOf(TopSpeakerGradientColor, EndSpeakerGradientColor)))
                .constrainAs(gradient) { bottom.linkTo(parent.bottom) }
        )

        TextBold(
            text = "${speaker.name} ${speaker.lastName}",
            color = Color.White,
            fontSize = 16.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
                .constrainAs(name){
                    bottom.linkTo(parent.bottom, 10.dp)
                }
        )
    }


}