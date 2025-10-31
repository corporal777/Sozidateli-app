package com.example.ui.auth.registration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.Dimension
import com.example.app.R
import com.example.extensions.ColumnWithTopBar
import com.example.ui.auth.registration.components.FieldTitleItem
import com.example.ui.components.AppCheckBox
import com.example.ui.components.AppDateField
import com.example.ui.components.AppPhoneTextField
import com.example.ui.components.AppTextField
import com.example.ui.components.AppTopNavigation
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.DefaultHorizontalPadding
import com.example.ui.theme.EventDetailIconsBackgroundColor

@Composable
fun RegistrationScreen(padding: PaddingValues) {

    val scrollState = rememberScrollState()

    Column(
        Modifier
            .background(AppBackgroundColor)
            .padding(top = padding.calculateTopPadding(),)
            .fillMaxSize()
    ) {

        AppTopNavigation(Modifier, stringResource(R.string.auth_register))

        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(horizontal = DefaultHorizontalPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            FieldTitleItem(stringResource(R.string.user_profile_last_name))
            AppTextField(
                modifier = Modifier,
                hint = stringResource(R.string.auth_input_last_name_hint)
            ) { }

            FieldTitleItem(stringResource(R.string.user_profile_name))
            AppTextField(
                modifier = Modifier,
                hint = stringResource(R.string.auth_input_first_name_hint)
            ) { }

            FieldTitleItem(stringResource(R.string.user_profile_middle_name))
            AppTextField(
                modifier = Modifier,
                hint = stringResource(R.string.auth_input_middle_name_hint)
            ) { }
            AppCheckBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                text = stringResource(R.string.profile_edit_user_no_middle_name),
                textSize = 14.sp,
                textColor = EventDetailIconsBackgroundColor,
                checked = false,
                onCheckedChange = {  }
            )

            FieldTitleItem(stringResource(R.string.search_filter_phone))
            AppPhoneTextField(
                modifier = Modifier,
                hint = stringResource(R.string.auth_input_phone_hint)
            ) { }

            FieldTitleItem(stringResource(R.string.profile_birthday))
            AppDateField(
                modifier = Modifier,
                hint = stringResource(R.string.profile_birthday)
            ){

            }
        }
    }

}