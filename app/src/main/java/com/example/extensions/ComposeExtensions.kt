package com.example.extensions

import android.os.Bundle
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.composable

fun Modifier.clickable(
    rippleColor: Color? = null,
    enabled: Boolean = true,
    source: MutableInteractionSource? = null,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["rippleColor"] = rippleColor
        properties["onClick"] = onClick
    }
) {
    Modifier.clickable(
        onClick = onClick,
        indication = rippleColor?.let {
            ripple(
                color = it
            )
        } ?: LocalIndication.current,
        interactionSource = source ?: remember { MutableInteractionSource() },
        enabled = enabled
    )
}

fun NavController.navigate(
    route: String,
    args: Bundle,
    navOptions: NavOptions? = null,
    navigatorExtras: Navigator.Extras? = null
) {
    val nodeId = graph.findNode(route = route)?.id
    if (nodeId != null) {
        navigate(nodeId, args, navOptions, navigatorExtras)
    }
}

fun Modifier.bottomShadow(shadow: Dp) =
    this
        .clip(GenericShape { size, _ ->
            lineTo(size.width, 0f)
            lineTo(size.width, Float.MAX_VALUE)
            lineTo(0f, Float.MAX_VALUE)
        })
        .shadow(shadow)

fun horizontalGradientBrush(colors: List<Color>): Brush {
    return Brush.horizontalGradient(colors)
}

fun verticalGradientBrush(colors: List<Color>): Brush {
    return Brush.verticalGradient(colors)
}

@Composable
fun LaunchedAnimation(show : Boolean, onAnim: (alpha: Float) -> Unit) {
    LaunchedEffect(show) {
        animate(
            initialValue = if (show) 0f else 1f,
            targetValue = if (show) 1f else 0f,
            animationSpec = tween(100)
        ) { value, vel ->
            onAnim.invoke(value)
        }
    }
}


fun NavGraphBuilder.animComposable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = enterTransition,
        popExitTransition = popExitTransition,
        //exitTransition = exitTransition,
        exitTransition = null,
        //popEnterTransition = popEnterTransition,
        popEnterTransition = null,
        content = content
    )
}

val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    scaleIn(
        initialScale = 0.92f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(230))
}

val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    scaleOut(
        targetScale = 0.9f,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(200))
}

val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
    slideOutHorizontally(
        targetOffsetX = { (-it / 7).toInt() },
        animationSpec = tween(durationMillis = 400, easing = LinearOutSlowInEasing)
    ) + fadeOut(animationSpec = tween(300, easing = LinearOutSlowInEasing))
}

val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
    slideInHorizontally(
        initialOffsetX = { -it / 5 },
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing)
    ) + fadeIn(animationSpec = tween(280, easing = LinearOutSlowInEasing))
}