package com.example.ui.auth.authorization

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.ui.auth.authorization.components.BackgroundItem
import com.example.ui.theme.AuthHorizontalPadding

@Composable
fun AuthorizationScreen(padding : PaddingValues) {

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (logo, filter, sort, sortIcon, spacer, content) = createRefs()

        BackgroundItem(logo, padding)
    }

}

