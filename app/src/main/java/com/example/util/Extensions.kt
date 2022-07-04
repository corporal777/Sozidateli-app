package com.example.util

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.annotation.ColorInt
import androidx.core.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import coil.load
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.Transformation
import com.example.R
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import onTextChanged
import java.io.File
import java.util.*

fun String.firstLetterToUppercase(): String {
    return if (this.isNotBlank())
        this.substring(0, 1).toUpperCase() + this.substring(1).toLowerCase()
    else
        this
}

fun CheckBox.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
    isChecked = checked
    setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
}

fun EditText.initInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
    setText(text)
    onTextChanged(onTextChanged)
}

fun FragmentManager.showDatePicker(
    currentDate: String?,
    onDateSelected: (date: Long) -> Unit
) {
    var selection = if (!currentDate.isNullOrBlank())
        serverDateToMilliseconds(currentDate, DATE_FORMAT_SHORT_MONTH_FULL_YEAR)
    else {
        val current = Calendar.getInstance()
        current.add(Calendar.YEAR, -14)
        current.timeInMillis
    }
    val timezone = TimeZone.getDefault()
    selection += timezone.getOffset(selection)
    val endDate = Calendar.getInstance()
    endDate.add(Calendar.YEAR, -14)

    val picker = MaterialDatePicker
        .Builder
        .datePicker()
        .setTitleText(R.string.profile_birthday)
        .setTheme(R.style.DatePickerStyle)
        .setSelection(selection)
        .setCalendarConstraints(
            CalendarConstraints.Builder()
                .setEnd(endDate.timeInMillis)
                .setOpenAt(selection)
                .setValidator(WeekDayValidator()).build()
        )
        .build()
    picker.addOnPositiveButtonClickListener {
        onDateSelected.invoke(it)
    }
    picker.show(this, "")
}

fun PopupWindow.settings() {
    isOutsideTouchable = true
    softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
    inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
}

fun String?.phoneToServer() = this?.replace("-", "")?.replace(" ", "")


@TargetApi(21)
fun Activity.setWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.removeSystemInsets(window.decorView, listener)
//    window.navigationBarColor = Color.TRANSPARENT
//    window.statusBarColor = Color.TRANSPARENT
}

fun Activity.cancelWindowTransparency(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.returnSystemInsets(window.decorView, listener)
}

fun Activity.doEdgeWindow(listener: OnSystemInsetsChangedListener = { _, _ -> }) {
    InsetUtil.doEdgeDisplay(window.decorView, listener)
}

typealias OnSystemInsetsChangedListener = (statusBarSize: Int, navigationBarSize: Int) -> Unit

object InsetUtil {

    fun doEdgeDisplay(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, 0)
            )
        }
    }

    fun removeSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(0, 0, 0, insets.systemWindowInsetBottom)
            )
        }
    }

    fun returnSystemInsets(view: View, listener: OnSystemInsetsChangedListener) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { _, insets ->
            ViewCompat.onApplyWindowInsets(
                view,
                insets.replaceSystemWindowInsets(
                    0,
                    insets.systemWindowInsetTop,
                    0,
                    insets.systemWindowInsetBottom
                )
            )
        }
    }

}


fun ImageView.setImage(
    image: Any?, crossfad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    //error: Int? = R.drawable.ic_profile,
    error: Int? = null,
    transformations: List<Transformation>? = null
) {
    val resImage: Any = image ?: ""
    when (resImage) {
        is Int -> load(resImage) {
            setParams(crossfad, placeholder, error, transformations)
        }
        is String ->
            if (Patterns.WEB_URL.matcher(resImage).matches())
                load(resImage) {
                    setParams(crossfad, placeholder, error, transformations)
                }
            else
                load(File(resImage)) {
                    setParams(crossfad, placeholder, error, transformations)
                }
        is Drawable ->
            load(resImage) {
                setParams(crossfad, placeholder, error, transformations)
            }
        is Bitmap -> load(resImage) {
            setParams(crossfad, placeholder, error, transformations)
        }
    }
}


fun ImageRequest.Builder.setParams(
    crossfad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = R.drawable.background_image_placeholder,
    transformations: List<Transformation>? = null
) {
    if (crossfad != null) crossfade(crossfad)
    if (placeholder != null) placeholder(placeholder)
    if (error != null) error(error)
    if (!transformations.isNullOrEmpty())
        transformations(transformations)
    scale(Scale.FILL)
}

@ColorInt
fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
    val alpha = Math.round(Color.alpha(color) * factor)
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Color.argb(alpha, red, green, blue)
}

fun getMonthName(month: Int): String {
    val monthNames = arrayOf(
        "Январь",
        "Февраль",
        "Март",
        "Апрель",
        "Май",
        "Июнь",
        "Июль",
        "Август",
        "Сентябрь",
        "Октябрь",
        "Ноябрь",
        "Декабрь"
    )
    return monthNames[month]
}

@SuppressLint("HardwareIds")
fun getDeviceId(context: Context): String {
    return Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
    )
}

fun getDeviceName(): String {
    val manufacturer: String = Build.MANUFACTURER
    val model: String = Build.MODEL
    return if (model.startsWith(manufacturer)) {
        capitalize(model)
    } else capitalize(manufacturer) + " " + model
}

private fun capitalize(str: String): String {
    if (TextUtils.isEmpty(str)) {
        return str
    }
    val arr = str.toCharArray()
    var capitalizeNext = true
    var phrase = ""
    for (c in arr) {
        if (capitalizeNext && Character.isLetter(c)) {
            phrase += Character.toUpperCase(c)
            capitalizeNext = false
            continue
        } else if (Character.isWhitespace(c)) {
            capitalizeNext = true
        }
        phrase += c
    }
    return phrase
}