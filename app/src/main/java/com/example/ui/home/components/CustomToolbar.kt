package com.example.ui.home.components

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.extensions.bottomShadow
import com.example.ui.components.TextExtraBold
import com.example.ui.components.TextRegular
import com.example.ui.components.TextSemibold
import com.example.ui.theme.AppBackgroundColor
import kotlin.math.roundToInt

@Composable
fun CustomToolbar(
    modifier: Modifier = Modifier,
    additionalContent: (@Composable () -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
    collapsingTitle: String? = null,
    scrollBehavior: CustomToolbarScrollBehavior? = null,
    collapsedElevation: Dp = DefaultCollapsedElevation,
) {

    val collapsedFraction = when {
        scrollBehavior != null -> scrollBehavior.state.collapsedFraction
        else -> 1f
    }

    val lineHeight = MaterialTheme.typography.headlineLarge.lineHeight.value
    val fullyCollapsedTitleScale = when {
        collapsingTitle != null -> CollapsedTitleLineHeight.value / lineHeight
        else -> 1f
    }

    val showElevation = when {
        scrollBehavior == null -> false
        scrollBehavior.state.contentOffset <= 0 && collapsedFraction == 1f -> true
        else -> false
    }

    val scrollOffset =
        if (scrollBehavior == null) 0f
        else if (scrollBehavior.state.contentOffset >= 0f) 0f
        else scrollBehavior.state.contentOffset / 10


    val scrollElevation = if (showElevation) {
        if ((-1 - scrollOffset) < 0f) 0.dp
        else if ((-1 - scrollOffset) < 4) (-1 - scrollOffset).dp
        else 4.dp
    } else 0.dp


    //val elevationState = animateDpAsState(if (showElevation) collapsedElevation else 0.dp)
    val elevationState = animateDpAsState(scrollElevation)

    Surface(
        modifier = modifier.bottomShadow(elevationState.value),
        color = AppBackgroundColor,
        shadowElevation = 0.dp,
    ) {
        Layout(
            content = {
                TextSemibold(
                    modifier = Modifier
                        .layoutId(TopTitleId)
                        .wrapContentHeight(align = Alignment.Top)
                        .alpha(collapsedFraction),
                    color = Color.Black,
                    fontSize = 17.sp,
                    text = collapsingTitle ?: ""
                )


                if (collapsingTitle != null) {
                    val textAlpha =
                        if (collapsedFraction >= 1.0f) 0f
                        else if (collapsedFraction <= 0.0f) 1.0f
                        else 0.6f - collapsedFraction

                    TextExtraBold(
                        modifier = Modifier
                            .layoutId(ExpandedTitleId)
                            .wrapContentHeight(align = Alignment.Top)
                            .alpha(textAlpha),
                        fontSize = 34.sp,
                        color = Color.Black,
                        text = collapsingTitle
                    )

                    Text(
                        modifier = Modifier
                            .layoutId(CollapsedTitleId)
                            .wrapContentHeight(align = Alignment.Top),
                        text = "",
                        style = MaterialTheme.typography.headlineLarge,
                        maxLines = 1,
                    )
                }


                if (additionalContent != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .layoutId(AdditionalContentId)
                    ) {
                        additionalContent()
                    }
                }
                if (rightContent != null) {
                    Box(
                        modifier = Modifier
                            .wrapContentWidth()
                            .layoutId(RightContentId)
                    ) {
                        rightContent()
                    }
                }
            },
            modifier = modifier.then(
                Modifier
                    .heightIn(min = MinCollapsedHeight)
                    .padding(horizontal = 16.dp)
            )
        ) { measurables, constraints ->
            val horizontalPaddingPx = HorizontalPadding.toPx()
            val expandedTitleBottomPaddingPx = ExpandedTitleBottomPadding.toPx()


            // Measuring widgets inside toolbar:

            val topTitlePlaceable = measurables.firstOrNull { it.layoutId == TopTitleId }
                ?.measure(constraints)

            val expandedTitlePlaceable = measurables.firstOrNull { it.layoutId == ExpandedTitleId }
                ?.measure(
                    constraints.copy(
                        maxWidth = (constraints.maxWidth - 2 * horizontalPaddingPx).roundToInt(),
                        minWidth = 0,
                        minHeight = 0
                    )
                )

            val additionalContentPlaceable =
                measurables.firstOrNull { it.layoutId == AdditionalContentId }?.measure(constraints)

            val rightContentPlaceable =
                measurables.firstOrNull { it.layoutId == RightContentId }?.measure(constraints)


            val collapsedTitleMaxWidthPx = (constraints.maxWidth) / fullyCollapsedTitleScale

            val collapsedTitlePlaceable =
                measurables.firstOrNull { it.layoutId == CollapsedTitleId }
                    ?.measure(
                        constraints.copy(
                            maxWidth = collapsedTitleMaxWidthPx.roundToInt(),
                            minWidth = 0,
                            minHeight = 0
                        )
                    )

            val collapsedHeightPx = MinCollapsedHeight.toPx()

            var layoutHeightPx = collapsedHeightPx


            // Calculating coordinates of widgets inside toolbar:

            // Current coordinates of title
            var collapsingTitleY = 0
            var collapsingTitleX = 0


            if (expandedTitlePlaceable != null && collapsedTitlePlaceable != null) {
                // Measuring toolbar collapsing distance
                val heightOffsetLimitPx =
                    expandedTitlePlaceable.height + expandedTitleBottomPaddingPx

                scrollBehavior?.state?.heightOffsetLimit = -heightOffsetLimitPx

                // Toolbar height at fully expanded state
                val fullyExpandedHeightPx = MinCollapsedHeight.toPx() + heightOffsetLimitPx


                // Coordinates of fully expanded title
                val fullyExpandedTitleY =
                    fullyExpandedHeightPx - expandedTitlePlaceable.height - expandedTitleBottomPaddingPx

                // Coordinates of fully collapsed title
                val fullyCollapsedTitleY =
                    collapsedHeightPx / 2 - CollapsedTitleLineHeight.toPx().roundToInt() / 2

                // Current height of toolbar
                layoutHeightPx = lerp(
                    fullyExpandedHeightPx,
                    collapsedHeightPx / 2,
                    (collapsedFraction).toFloat()
                )

                // Current coordinates of collapsing title
                collapsingTitleY =
                    lerp(fullyExpandedTitleY, fullyCollapsedTitleY, collapsedFraction).roundToInt()
            } else {
                scrollBehavior?.state?.heightOffsetLimit = -1f
            }


            //val toolbarHeightPx = layoutHeightPx.roundToInt() + (additionalContentPlaceable?.height ?: 0)
            val heightPx = (layoutHeightPx.roundToInt() + (additionalContentPlaceable?.height ?: 0))
            val toolbarHeightPx = heightPx - 20

            // Placing toolbar widgets:

            layout(constraints.maxWidth, toolbarHeightPx) {

                val rightHeight = (rightContentPlaceable?.height ?: 0)
                val rightY = ((collapsedHeightPx - rightHeight) / 2).roundToInt()
                val rightX = constraints.maxWidth - (rightContentPlaceable?.width ?: 0)
                rightContentPlaceable?.placeRelative(rightX, rightY)

                val topHeight = (topTitlePlaceable?.height ?: 0) / 10
                val y = ((collapsedHeightPx - (topTitlePlaceable?.height ?: 0)) / 2).roundToInt()
                val x = (constraints.maxWidth / 2) - ((topTitlePlaceable?.width?.div(2)) ?: 0)
                topTitlePlaceable?.placeRelative(x, y + topHeight)

                val expandY = collapsingTitleY - 50
                expandedTitlePlaceable?.placeRelative(0, expandY)


                val offset = if (collapsedFraction <= 0) 0f else 100 * collapsedFraction
                val value = ((65 - offset) / 3)

                //old value
                val contentY = (layoutHeightPx - value).roundToInt()
                //new value
                val expandHeightY = collapsingTitleY + expandedTitlePlaceable!!.height - 20
                val refContentY = if (contentY >= expandHeightY) expandHeightY else contentY
                additionalContentPlaceable?.placeRelative(0, refContentY)
            }
        }

    }
}


private fun lerp(a: Float, b: Float, fraction: Float): Float {
    return a + fraction * (b - a)
}


private val MinCollapsedHeight = 60.dp
private val HorizontalPadding = 16.dp
private val ExpandedTitleBottomPadding = 0.dp
private val CollapsedTitleLineHeight = 28.sp
private val DefaultCollapsedElevation = 4.dp

private const val ExpandedTitleId = "expandedTitle"
private const val CollapsedTitleId = "collapsedTitle"
private const val TopTitleId = "topTitle"
private const val ActionsId = "actions"
private const val RightContentId = "rightContent"
private const val AdditionalContentId = "additionalContent"