package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.app.R
import com.example.common.calendar
import com.example.common.getCalendarDay
import com.example.common.getCalendarMonth
import com.example.common.getCalendarYear
import com.example.common.parseToDefaultDateFormat
import com.example.extensions.clickable
import com.example.ui.theme.AppBackgroundColor
import com.example.ui.theme.EventDetailIconsBackgroundColor
import com.example.ui.theme.InputContainerFocusedColor
import com.example.ui.theme.InputContainerUnFocusedColor
import com.example.ui.theme.InputPlaceholderColor
import com.example.ui.theme.InputTextColor
import com.example.ui.theme.MainBrownColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDateField(
    modifier: Modifier,
    hint: String,
    text: String = "",
    onTextChange: (text: String) -> Unit
) {

    var inputText by rememberSaveable { mutableStateOf(text) }
    val interactionSource = remember { MutableInteractionSource() }
    var datePickerShown by rememberSaveable { mutableStateOf(false) }

    BasicTextField(
        value = inputText,
        onValueChange = { onTextChange.invoke(it) },
        modifier = modifier
            .defaultMinSize(
                minWidth = Dp.Unspecified,
                minHeight = dimensionResource(R.dimen.common_edit_text_min_height)
            )
            .fillMaxWidth(),
        interactionSource = interactionSource,
        singleLine = true,
        textStyle = TextStyle(
            fontSize = dimensionResource(R.dimen.common_edit_text_size).value.sp,
            fontWeight = FontWeight.Normal,
            letterSpacing = TextUnit(-0.01F, TextUnitType.Sp)
        ),
        readOnly = true
    ) { innerTextField ->
        TextFieldDefaults.DecorationBox(
            value = inputText,
            visualTransformation = VisualTransformation.None,
            innerTextField = innerTextField,
            singleLine = true,
            enabled = true,
            interactionSource = interactionSource,
            contentPadding = PaddingValues(horizontal = 15.dp, vertical = 0.dp),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedTextColor = InputTextColor,
                unfocusedTextColor = InputTextColor,
                focusedContainerColor = InputContainerFocusedColor,
                unfocusedContainerColor = InputContainerUnFocusedColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            placeholder = {
                if (inputText.isBlank())
                    Text(
                        modifier = Modifier.alpha(0.5f),
                        text = hint,
                        color = InputPlaceholderColor,
                        fontSize = dimensionResource(R.dimen.common_edit_text_size).value.sp,
                        fontWeight = FontWeight.Normal,
                        letterSpacing = TextUnit(-0.01F, TextUnitType.Sp)
                    )

            },
            trailingIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_calendar_new),
                    contentDescription = "",
                    tint = Color.Unspecified,
                    modifier = Modifier.clickable(Color.Black) {
                        datePickerShown = true
                    }
                )
            }

        )
    }

    if (datePickerShown) {
        DatePickerModal(
            onDateSelected = {
                inputText = it.parseToDefaultDateFormat() ?: ""
            },
            onDismiss = { datePickerShown = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    currentDate : Long? = System.currentTimeMillis(),
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(currentDate)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .requiredWidth(360.dp)
                .wrapContentHeight()
                .clip(DatePickerDefaults.shape)
                .background(AppBackgroundColor)
        ) {

            val (date, accept, cancel) = createRefs()

            DatePicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(date) { top.linkTo(parent.top) },
                showModeToggle = false,
                title = null,
                headline = {
                    DatePickerDefaults.DatePickerHeadline(
                        selectedDateMillis = datePickerState.selectedDateMillis,
                        displayMode = datePickerState.displayMode,
                        dateFormatter = remember { DatePickerDefaults.dateFormatter() },
                        modifier = Modifier.padding(
                            start = 24.dp,
                            end = 12.dp,
                            top = 20.dp,
                            bottom = 5.dp
                        )
                    )
                },
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = AppBackgroundColor,
                    titleContentColor = MainBrownColor,
                    headlineContentColor = MainBrownColor,

                    yearContentColor = EventDetailIconsBackgroundColor,
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = MainBrownColor,

                    weekdayContentColor = EventDetailIconsBackgroundColor,
                    subheadContentColor = EventDetailIconsBackgroundColor,
                    dividerColor = AppBackgroundColor,
                    dayContentColor = EventDetailIconsBackgroundColor,
                    currentYearContentColor = EventDetailIconsBackgroundColor,
                    todayDateBorderColor = MainBrownColor,
                    navigationContentColor = EventDetailIconsBackgroundColor,
                    todayContentColor = EventDetailIconsBackgroundColor,
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = MainBrownColor
                )
            )

            TextSemibold(
                text = stringResource(R.string.cancel),
                color = MainBrownColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .constrainAs(cancel) {
                        top.linkTo(date.bottom)
                        end.linkTo(accept.start, 25.dp)
                        bottom.linkTo(parent.bottom, 20.dp)
                    }
                    .clickable(Color.Black, onClick = onDismiss)
            )

            TextSemibold(
                text = "OK",
                color = MainBrownColor,
                fontSize = 16.sp,
                modifier = Modifier
                    .constrainAs(accept) {
                        top.linkTo(date.bottom)
                        end.linkTo(parent.end, 24.dp)
                        bottom.linkTo(parent.bottom, 20.dp)
                    }
                    .clickable(Color.Black) {
                        onDateSelected(datePickerState.selectedDateMillis)
                        onDismiss()
                    }
            )
        }
    }

}