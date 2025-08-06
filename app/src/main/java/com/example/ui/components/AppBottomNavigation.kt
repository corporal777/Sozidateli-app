package com.example.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.app.R
import com.example.data.models.BottomNavigationItem
import com.example.extensions.LaunchedAnimation
import com.example.navigation.Route
import com.example.ui.theme.BottomNavigationBarColor
import com.example.ui.theme.BottomNavigationNormalColor
import com.example.ui.theme.BottomNavigationSelectedColor
import com.example.ui.theme.CourseItemColor
import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun AppBottomNavigation(navController: NavController) {
    val listItems = listOf(
        BottomNavigationItem(R.drawable.ic_main_tab, stringResource(R.string.label_main_tab)),
        BottomNavigationItem(
            R.drawable.ic_my_events_tab,
            stringResource(R.string.label_my_events_tab)
        ),
        BottomNavigationItem(R.drawable.ic_chats_tab, stringResource(R.string.chat_list)),
        BottomNavigationItem(
            R.drawable.ic_notifications_tab,
            stringResource(R.string.notifications_tab_label)
        ),
        BottomNavigationItem(
            R.drawable.ic_profile_tab,
            stringResource(R.string.profile_current_user_label)
        ),
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    var selectedItem by rememberSaveable { mutableStateOf(0) }
    val currentRoute = backStackEntry?.destination?.route

    selectedItem = when (currentRoute) {
        Route.HomeScreen.route -> 0
        else -> 4
    }

    var isBottomBarVisible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(currentRoute) {
        isBottomBarVisible = currentRoute == Route.HomeScreen.route
    }

    if (isBottomBarVisible) {
        CustomNavigationBar() {
            listItems.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == selectedItem,
                    onClick = { },
                    icon = {
                        Icon(
                            modifier = Modifier.size(24.dp),
                            painter = painterResource(item.icon),
                            contentDescription = ""
                        )
                    },
                    label = {
                        TextMedium(
                            text = item.label,
                            fontSize = 10.sp,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    colors = NavigationBarItemColors(
                        selectedTextColor = BottomNavigationSelectedColor,
                        selectedIconColor = BottomNavigationSelectedColor,
                        unselectedTextColor = BottomNavigationNormalColor,
                        unselectedIconColor = BottomNavigationNormalColor,
                        selectedIndicatorColor = Color.Transparent,
                        disabledIconColor = BottomNavigationNormalColor,
                        disabledTextColor = BottomNavigationNormalColor
                    )
                )
            }
        }
    }
}

@Composable
private fun CustomNavigationBar(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .background(BottomNavigationBarColor)
            .fillMaxWidth()
            .windowInsetsPadding(NavigationBarDefaults.windowInsets)
            .height(60.dp)
            //.defaultMinSize(minHeight = 65.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )

}