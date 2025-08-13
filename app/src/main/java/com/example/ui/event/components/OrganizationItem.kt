package com.example.ui.event.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.constraintlayout.compose.Visibility
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.examle.domain.model.organization.OrganizationModel
import com.example.app.R
import com.example.common.parseColor
import com.example.ui.components.AppFavoriteButton
import com.example.ui.components.TextMedium
import com.example.ui.components.TextSemibold
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.ItemImageBorderColor

@Composable
fun OrganizationItem(organization: OrganizationModel?) {

    var isFavorite by remember { mutableStateOf(false) }
    isFavorite = organization?.userFavorite != null

    val backgroundColor = if (organization?.backgroundColor.isNullOrEmpty()) Color(0xFFF9F9F9)
    else Color(organization?.backgroundColor.parseColor()?.toLong() ?: 0xFFF9F9F9)

    ConstraintLayout(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = DefaultHorizontalPadding, vertical = 20.dp)
    ) {

        val (image, title, favorite, noImage) = createRefs()

        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .size(90.dp)
                .background(backgroundColor)
                .border(0.5.dp, ItemImageBorderColor, RoundedCornerShape(10.dp))
                .constrainAs(image) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            model = ImageRequest.Builder(LocalContext.current)
                .data(organization?.image)
                .crossfade(300)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        TextSemibold(
            text = organization?.name ?: "",
            color = Color.Black,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            fontSize = 17.sp,
            modifier = Modifier
                .constrainAs(title) {
                    top.linkTo(image.top)
                    start.linkTo(image.end, 12.dp)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        TextMedium(
            text = "Нет фото",
            color = Color.Black,
            fontSize = 12.sp,
            modifier = Modifier
                .constrainAs(noImage) {
                    top.linkTo(image.top)
                    bottom.linkTo(image.bottom)
                    start.linkTo(image.start)
                    end.linkTo(image.end)
                    visibility = if (organization?.image.isNullOrEmpty()) Visibility.Visible
                    else Visibility.Gone
                }
        )

        AppFavoriteButton(
            isFavorite = isFavorite,
            modifier = Modifier
                .constrainAs(favorite) {
                    bottom.linkTo(image.bottom)
                    start.linkTo(image.end, 12.dp)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }) { }
    }
}