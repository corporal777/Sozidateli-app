package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.app.R
import com.example.extensions.clickable

@Composable
fun AppTopBar(modifier: Modifier) {

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .height(55.dp)
            .background(Color.White)
            //.padding(vertical = 5.dp, horizontal = 5.dp),
    ) {
        val (back) = createRefs()

        Icon(
            painter = painterResource(R.drawable.ic_back_black),
            tint = Color.Black,
            contentDescription = "",
            modifier = Modifier
                .clip(CircleShape)
                .size(40.dp)
                .constrainAs(back){
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start, 5.dp)
                }.clickable(Color.Black){

                }.padding(10.dp)
        )
    }
}

