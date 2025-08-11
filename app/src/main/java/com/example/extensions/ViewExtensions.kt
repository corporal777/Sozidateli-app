package com.example.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.Layout
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.text.style.URLSpan
import android.util.DisplayMetrics
import android.util.Patterns
import android.util.TypedValue
import android.view.KeyEvent.ACTION_UP
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.ImageViewCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Scale
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import coil3.request.crossfade
import com.example.adapters.NoFilterArrayAdapter
import com.example.app.R
import com.example.common.dp
import com.example.common.parseColor
import com.example.ui.base.BaseVBFragment
import com.example.util.URLSpanNoUnderline
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import java.io.File
import java.util.Calendar
import java.util.Calendar.YEAR
import java.util.Date
import kotlin.math.roundToInt

fun TextView.changeTitleTextColor(show: Boolean){
    textColor = if (show) R.color.title_text_error_red else R.color.chat_list_date
}

fun TextView.showInputError(show: Boolean){
    if (show) {
        setRightDrawable(R.drawable.ic_input_error_icon)
        updatePadding(right = 10.dp)
    } else {
        setRightDrawable(0)
        updatePadding(right = 15.dp)
    }
}


fun ImageView.setTint(@ColorRes colorRes: Int) {
    ImageViewCompat.setImageTintList(
        this,
        ColorStateList.valueOf(ContextCompat.getColor(context, colorRes))
    )
}

fun View.getDrawable(res: Int): Drawable? {
    return ContextCompat.getDrawable(context, res)
}

fun View.getColor(res: Int): Int {
    return ContextCompat.getColor(context, res)
}

fun View.getColorStateList(res: Int): ColorStateList? {
    return ContextCompat.getColorStateList(context, res)
}

fun View.getColorStateList(res: String): ColorStateList {
    return ColorStateList.valueOf(res.parseColor() ?: getColor(R.color.colorAccent))
}

fun TextView.setLeftDrawable(res: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(res, 0, 0, 0)
}

fun TextView.setRightDrawable(res: Int) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, res, 0)
}




fun ImageView.setImage(
    image: Any?, crossfade: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = null,
    transformations: List<Transformation>? = null
) {
    val resImage: Any = image ?: ""
    when (resImage) {
        is Int -> load(resImage) {
            setParams(crossfade, placeholder, error, transformations)
        }

        is Uri -> load(resImage) {
            setParams(crossfade, placeholder, error, transformations)
        }

        is String ->
            if (Patterns.WEB_URL.matcher(resImage).matches())
                load(resImage) {
                    setParams(crossfade, placeholder, error, transformations)
                }
            else
                load(File(resImage)) {
                    setParams(crossfade, placeholder, error, transformations)
                }

        is Drawable ->
            load(resImage) {
                setParams(crossfade, placeholder, error, transformations)
            }

        is Bitmap -> load(resImage) {
            setParams(crossfade, placeholder, error, transformations)
        }
    }
}

fun ImageView.setCircleAvatar(
    image: Any?, crossFad: Int? = 500,
    placeholder: Int? = R.drawable.background_image_placeholder,
    error: Int? = R.drawable.avatar_placeholder_circle
) {
    val resImage: Any = image ?: ""
    when (resImage) {
        is Int -> load(resImage) {
            setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
        }

        is String ->
            if (Patterns.WEB_URL.matcher(resImage).matches())
                load(resImage) {
                    setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
                }
            else
                load(File(resImage)) {
                    setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
                }

        is Drawable ->
            load(resImage) {
                setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
            }

        is Bitmap -> load(resImage) {
            setParams(crossFad, placeholder, error, listOf(CircleCropTransformation()))
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
    if (!transformations.isNullOrEmpty()) transformations(transformations)
    scale(Scale.FILL)
    diskCachePolicy(CachePolicy.ENABLED)
    listener(
        onStart = {},
        onCancel = {},
        onError = { _, _ ->

        }
    )
}




fun LinearLayoutManager.smoothScrollToFirstItem(
    context: Context,
    appBar: AppBarLayout?,
    jumpToPosition: Int
) {
    val mSmoothScroller by lazy {
        object : LinearSmoothScroller(context) {
            override fun getVerticalSnapPreference(): Int {
                return SNAP_TO_END
            }

            override fun updateActionForInterimTarget(action: Action?) {
                action?.jumpTo(jumpToPosition)
            }

            override fun onStop() {
                super.onStop()
                appBar?.setExpanded(true)
            }

            override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                return 20f / displayMetrics.densityDpi
            }
        }
    }
    mSmoothScroller.targetPosition = 0
    this.startSmoothScroll(mSmoothScroller)
}

fun <T> AppCompatAutoCompleteTextView.initDropDownAdapter(list: MutableList<T>) {
    keyListener = null
    setAdapter(
        NoFilterArrayAdapter(
            context,
            android.R.layout.simple_list_item_1,
            list
        )
    )
}


fun PopupWindow.settings() {
    isOutsideTouchable = true
    softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
    inputMethodMode = PopupWindow.INPUT_METHOD_NEEDED
}


fun AppCompatCheckBox.initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
    isChecked = checked
    setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
}

fun EditText.initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
    setText(text)
    onTextChanged(onTextChanged)
}

fun AppCompatImageButton.setFiltersBackground(isChosen: Boolean) {
    if (isChosen) setImageResource(R.drawable.ic_filters_selected)
    else setImageResource(R.drawable.ic_filters_new)
}

fun TextView.setTextCustomSize(res: Int) {
    setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(res))
}

fun TextView.removeUrlUnderline(textColor: Int? = null) {
    if (text.isNullOrEmpty()) return
    text.toSpannable().apply {
        val urls = getSpans<URLSpan>()
        urls.forEach {
            val start = getSpanStart(it)
            val end = getSpanEnd(it)
            removeSpan(it)
            set(start..end, URLSpanNoUnderline(it.url, textColor))
        }
    }
}

fun AppCompatToggleButton.onCheckedChanged(onCheckedChanged: (checked: Boolean) -> Unit): CompoundButton.OnCheckedChangeListener {
    val listener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            onCheckedChanged(p1)
        }
    }
    setOnCheckedChangeListener(listener)
    return listener
}

fun AppCompatCheckBox.onCheckedChanged(onCheckedChanged: (checked: Boolean) -> Unit): CompoundButton.OnCheckedChangeListener {
    val listener = object : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
            onCheckedChanged(p1)
        }
    }
    setOnCheckedChangeListener(listener)
    return listener
}

fun TextView.onKeyDoneClick(onKeyClick: () -> Unit){
    setOnKeyListener { _, keyCode, _ ->
        when (keyCode) {
            EditorInfo.IME_ACTION_DONE -> {
                onKeyClick.invoke()
                true
            }
            else -> false
        }
    }
}

fun TextView.onTextChanged(onTextChanged: (text: CharSequence?) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            onTextChanged(s)
        }
    }
    addTextChangedListener(watcher)
    return watcher
}

fun TextView.onFocusChanged(onFocusChanged: (hasFocus: Boolean) -> Unit): View.OnFocusChangeListener {
    val watcher = View.OnFocusChangeListener { v, hasFocus ->
        onFocusChanged(hasFocus)
    }
    onFocusChangeListener = watcher
    return watcher
}

fun EditText.showHidePasswordText(show: Boolean) {
    if (!show) this.transformationMethod = PasswordTransformationMethod()
    else this.transformationMethod = null
    this.setSelection(this.length());
}

fun ViewPager2.onPageStateChanged(onPageChanged: (state: Int) -> Unit) {
    val listener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            onPageChanged(state)
        }
    }
    registerOnPageChangeCallback(listener)
}

fun onPageSelected(onPageChanged: (position: Int) -> Unit): ViewPager2.OnPageChangeCallback {
    val listener = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            onPageChanged(position)
        }
    }
    return listener
}

fun onPageChanged(onPageChanged: (position: Int) -> Unit): ViewPager.SimpleOnPageChangeListener {
    val pageChangeListener = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(position: Int) {
            onPageChanged(position)
        }
    }
    return pageChangeListener
}

fun AppBarLayout.offsetChangedListener(
    offsetChanged: (appBarLayout: AppBarLayout, offset: Int) -> Unit
): AppBarLayout.OnOffsetChangedListener {
    val listener = AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        offsetChanged(appBarLayout, verticalOffset)
    }
    addOnOffsetChangedListener(listener)
    return listener
}

fun RecyclerView.onScrolled(
    onScrolled: (dx: Int, dy: Int) -> Unit,
): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) = onScrolled(dx, dy)
    }
    addOnScrollListener(listener)
    return listener
}

fun RecyclerView.onScrollStateChanged(
    onStateChange: (recyclerView: RecyclerView, newState: Int) -> Unit
): RecyclerView.OnScrollListener {
    val listener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) =
            onStateChange(recyclerView, newState)

    }
    addOnScrollListener(listener)
    return listener
}

fun NestedScrollView.onScrolled(onScrolled: (scrollY: Int, oldScrollY: Int, scrollX: Int, oldScrollX: Int) -> Unit): NestedScrollView.OnScrollChangeListener {
    val listener =
        NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            onScrolled(scrollY, oldScrollY, scrollX, oldScrollX)
        }
    setOnScrollChangeListener(listener)
    return listener
}



fun TextView.checkIsEllipsized(onChecked: (Boolean) -> Unit) {
    val check: (Layout) -> Unit = {
        val lines = layout.lineCount
        val ellipsizeCount = layout.getEllipsisCount(lines - 1)
        onChecked(ellipsizeCount > 0)
    }
    val layout = this.layout
    if (layout != null) check(layout) else doOnLayout { check(this.layout) }
}

fun TextView.calculateTextLinesCount(text: String): Int {
    val width = width - paddingStart - paddingLeft
    if (width <= 0) return 0
    val textWidth = with(paint) {
        textSize = this@calculateTextLinesCount.textSize
        measureText(text)
    }

    return (textWidth / width).roundToInt()
}


fun TextView.setMaxLength(max: Int) {
    filters = arrayOf(InputFilter.LengthFilter(max))
}

fun TextView.setMinMaxLines(min: Int, max: Int) {
    minLines = min
    maxLines = max
}

fun Group.setTextDataOrHide(textField: TextView, dataText: CharSequence?, isVisible: Boolean?) {
    if (dataText.isNullOrBlank() || isVisible == false) {
        visibility = View.GONE
        textField.text = null
    } else {
        textField.apply {
            text = dataText
            removeUrlUnderline()
        }
        visibility = View.VISIBLE
    }
}


fun TextView.additionalNumber(number: String?) {
    text = if (!number.isNullOrEmpty()) " (доб.$number)" else ""
}

fun View.setSelectableItemBackgroundBorderless() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

//fun Bitmap.toBodyPart(
//    name: String,
//    fileName: String,
//    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
//): MultipartBody.Part {
//    return let { bitmap ->
//        val byteArray = ByteArrayOutputStream().let {
//            bitmap.compress(compressFormat, 100, it)
//            it.toByteArray()
//        }
//
//        val body = byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
//        MultipartBody.Part.createFormData(name, fileName, body)
//    }
//}


fun TextInputLayout.initAsMonthYearPicker(
    startDate: Date?,
    minDate: Date? = null,
    maxDate: Date? = null,
    transformDate: (year: Int, month: Int, day: Int) -> String?
) {
    initAsDatePicker(
        startDate,
        minDate,
        maxDate,
        includeTime = false,
        showDates = false
    ) { year, month, dayOfMonth, _, _ -> transformDate(year, month, dayOfMonth) }
}

fun TextInputLayout.initAsDatePicker(
    startDate: Date?,
    minDate: Date? = null,
    maxDate: Date? = null,
    transformDate: (year: Int, month: Int, day: Int) -> String?
) {
    initAsDatePicker(
        startDate,
        minDate,
        maxDate,
        includeTime = false,
        showDates = true
    ) { year, month, dayOfMonth, _, _ -> transformDate(year, month, dayOfMonth) }
}

fun TextInputLayout.initAsDateTimePicker(
    startDate: Date?,
    minDate: Date? = null,
    maxDate: Date? = null,
    transformDate: (year: Int, month: Int, day: Int, hour: Int, minute: Int) -> String?
) {
    initAsDatePicker(
        startDate,
        minDate,
        maxDate,
        includeTime = true,
        showDates = true
    ) { year, month, dayOfMonth, hour, minute ->
        transformDate(
            year,
            month,
            dayOfMonth,
            hour,
            minute
        )
    }
}

@SuppressLint("ClickableViewAccessibility")
private fun TextInputLayout.initAsDatePicker(
    startDate: Date?,
    minDate: Date?,
    maxDate: Date?,
    includeTime: Boolean,
    showDates: Boolean,
    transformDate: (year: Int, month: Int, day: Int, hour: Int, minute: Int) -> String?
) {
    val calendar = Calendar.getInstance().apply { time = startDate ?: Date() }
    val showTimePicker: (year: Int, month: Int, day: Int, startHour: Int, startMinute: Int) -> Unit =
        { year, month, day, startHour: Int, startMinute: Int ->
            TimePickerDialog(
                context,
                R.style.AlertDialogTheme,
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    calendar.set(year, month, day, hour, minute)
                    editText?.setText(transformDate(year, month, day, hour, minute))
                },
                startHour,
                startMinute,
                true
            )
                .show()
        }

    val showDatePicker = {
        DatePickerDialog(
            context,
            if (showDates) R.style.AlertDialogTheme else R.style.AlertDialogTheme_DatePickerSpinner,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                if (includeTime) showTimePicker(
                    year,
                    month,
                    dayOfMonth,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE)
                )
                else {
                    calendar.set(year, month, dayOfMonth)
                    editText?.setText(transformDate(year, month, dayOfMonth, 0, 0))
                }
            },
            calendar.get(YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
            .apply {
                minDate?.let { datePicker.minDate = it.time }
                maxDate?.let { datePicker.maxDate = it.time }
                if (!showDates) {
                    val yearRes = context.resources.getIdentifier("android:id/day", null, null)
                    if (yearRes != 0) {
                        datePicker.findViewById<View>(yearRes)?.isVisible = false
                    }
                }
            }
            .show()
    }

    //setEndIconDrawable(R.drawable.ic_calendar)
    //setEndIconTintMode(PorterDuff.Mode.MULTIPLY)
    setEndIconOnClickListener { showDatePicker() }
    errorIconDrawable = null
    editText?.apply {
        isCursorVisible = false
        isFocusableInTouchMode = false
        setOnTouchListener { _, event ->
            if (event.action == ACTION_UP) showDatePicker()
            false
        }
    }
}

fun <T> initDropDownView(
    textView: AutoCompleteTextView,
    variants: Collection<String>,
    selectedVariant: String?,
    notSelectedVariant: String? = null,
    findValue: (String?) -> T?,
    onVariantChange: (T?) -> Unit
) {
    val variantsMap = linkedMapOf<String, T?>()
    variants.associateWithTo(variantsMap) { findValue(it) }
    initDropDownView(textView, variantsMap, selectedVariant, notSelectedVariant, onVariantChange)
}

fun <K, V> initDropDownView(
    textView: AutoCompleteTextView,
    variants: Collection<K>,
    selectedVariant: String?,
    notSelectedVariant: String? = null,
    transformKey: (K) -> String,
    findValue: (K?) -> V?,
    onVariantChange: (V?) -> Unit
) {
    val variantsMap = linkedMapOf<String, V?>()
    variants.associateTo(variantsMap, { transformKey(it) to findValue(it) })
    initDropDownView(textView, variantsMap, selectedVariant, notSelectedVariant, onVariantChange)
}

fun <T> initDropDownView(
    textView: AutoCompleteTextView,
    variants: Map<String, T?>,
    selectedVariant: String?,
    notSelectedVariant: String? = null,
    onVariantChange: (T?) -> Unit
) {
    val fullFilter =
        if (notSelectedVariant != null) mutableMapOf<String, T?>(notSelectedVariant to null).apply {
            putAll(variants)
        }
        else variants

    textView.apply {
        keyListener = null
        (tag as? TextWatcher)?.let { removeTextChangedListener(it) }
        setAdapter(
            NoFilterArrayAdapter(
                context,
                R.layout.item_dropdown,
                R.id.tvText,
                fullFilter.keys.toMutableList()
            )
        )
        setText(selectedVariant ?: notSelectedVariant, false)
        tag = onTextChanged {
            if (notSelectedVariant != null && it.toString() == notSelectedVariant) {
                val watcher = tag as? TextWatcher
                removeTextChangedListener(watcher)
                setText(notSelectedVariant)
                addTextChangedListener(watcher)
            }
            val variant = it?.toString()
            onVariantChange(fullFilter[variant])
        }

        isCursorVisible = false
        isFocusableInTouchMode = false
    }
}

fun View.setOnClickListener(listener: () -> Unit) {
    setOnClickListener { listener() }
}

fun View.onClickListener(listener: (() -> Unit)?) {
    if (listener != null) setOnClickListener { listener.invoke() }
}

var View.topMargin : Int
    get() = marginTop
    set(value) {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (topMargin == value || marginTop == value) return
            this.topMargin = value
        }
    }

@RequiresApi(Build.VERSION_CODES.R)
fun Activity.setSystemBarsAppearance(start: Int, end: Int) {
    window.insetsController?.setSystemBarsAppearance(start, end)
}

@RequiresApi(Build.VERSION_CODES.R)
fun Activity.setSystemBarsAppearance(start: Int) {
    window.insetsController?.setSystemBarsAppearance(start, start)
}

var Activity.statusBarColorValue: Int
    get() = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    set(value) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.Q) {
            setSystemBarsAppearance(value, APPEARANCE_LIGHT_STATUS_BARS)
        } else {
            if (window.decorView.systemUiVisibility == value) return
            else window.decorView.systemUiVisibility = value
        }
    }

var View.isVisibleAnim: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        if (value) {
            alpha = 0F
            visibility = View.VISIBLE
            animate().setDuration(500).alpha(1.0f)
        } else visibility = View.GONE
    }

var TextView.textColor : Int
    get() = currentTextColor
    set(value) {
        setTextColor(getColor(value))
    }

fun Fragment.onBackPressedCallback(
    enabled: Boolean,
    onBackClick: () -> Unit
) {
    requireActivity().onBackPressedDispatcher.addCallback(
        viewLifecycleOwner,
        object : OnBackPressedCallback(enabled) {
            override fun handleOnBackPressed() {
                onBackClick.invoke()
            }
        })
}

fun Activity.onBackPressedCallback(
    enabled: Boolean,
    onBackClick: () -> Unit
): OnBackPressedCallback {
    return object : OnBackPressedCallback(enabled) {
        override fun handleOnBackPressed() {
            onBackClick.invoke()
        }
    }
}

fun View?.getLocationOfView(): Pair<Int, Int> {
    if (this == null) return Pair(0, 0)
    val location = IntArray(2)
    this.getLocationOnScreen(location)
    val x = location[0]
    val y = location[1]
    return Pair(x, y)
}

fun ViewGroup.setChildSelected(position : Int){
    for (p in 0 until childCount) getChildAt(p).isSelected = p == position
}

fun BaseVBFragment<*>.startIntent(type : String, intent : Intent.() -> Unit){
    try {
        val newIntent = Intent(type).apply(intent)
        requireContext().startActivity(newIntent)
    } catch (e: Exception) {
        showRequestErrorMessage()
    }
}

fun BaseVBFragment<*>.startChooserIntent(intent : Intent.() -> Unit){
    try {
        val newIntent = Intent().apply(intent)
        requireContext().startActivity(Intent.createChooser(newIntent, "Choose one of the:"))
    } catch (e: Exception) {
        showRequestErrorMessage()
    }
}