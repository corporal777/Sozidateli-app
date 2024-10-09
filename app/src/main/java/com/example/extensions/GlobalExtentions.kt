package com.example.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.*
import android.text.method.PasswordTransformationMethod
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import android.util.SparseArray
import android.util.TypedValue
import android.view.KeyEvent.ACTION_UP
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.ContextThemeWrapper
import androidx.constraintlayout.widget.Group
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.text.toSpannable
import androidx.core.view.doOnLayout
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.adapters.NoFilterArrayAdapter
import com.example.app.R
import com.example.data.models.user.User
import com.example.ui.base.bottomSheet.BaseBottomSheetFragment
import com.example.util.*
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.squareup.picasso.Picasso
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.YEAR
import kotlin.math.roundToInt


fun decodeBase64ToJson(data: String?): JSONObject? {
    if (data.isNullOrEmpty()) return null
    try {
        val base = Base64.decode(data, Base64.DEFAULT)
        return JSONObject(String(base, StandardCharsets.UTF_8))
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }
}


fun getClickablePrivacyPolitics(context: Context): CharSequence {
    return SpannableString(context.getString(R.string.auth_user_agreement)).apply {
        setSpan(
            ClickableSpan(false) {
                showCustomTabsBrowser(context, context.getString(R.string.auth_agree_address))
            }, 52, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}

fun Spanned?.removeUrlUnderline(): Spannable? {
    if (this.isNullOrEmpty()) return null
    return toSpannable().apply {
        val urls = getSpans<URLSpan>()
        urls.forEach {
            val start = getSpanStart(it)
            val end = getSpanEnd(it)
            removeSpan(it)
            set(start..end, URLSpanNoUnderline(it.url))
        }
    }
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

fun String.parseAsHtmlWithoutUnderline(): Spannable? {
    if (this.isNullOrEmpty()) return null
    val s: Spannable = Html.fromHtml(this) as Spannable
    for (u in s.getSpans(0, s.length, URLSpan::class.java)) {
        s.setSpan(object : UnderlineSpan() {
            override fun updateDrawState(tp: TextPaint) {
                tp.isUnderlineText = false
            }
        }, s.getSpanStart(u), s.getSpanEnd(u), 0)
    }
    return s
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
    val listener = object : AppBarLayout.OnOffsetChangedListener {
        override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) =
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

fun getFragmentLifecycleCallback(
    onFragmentStarted: (f: Fragment) -> Unit?,
    onFragmentStopped: (f: Fragment) -> Unit?,
    onFragmentDestroyed: (f: Fragment) -> Unit?,
    onFragmentViewCreated: (f: Fragment) -> Unit,
    onBottomSheetViewCreated: (f: BaseBottomSheetFragment<*>) -> Unit,
): FragmentManager.FragmentLifecycleCallbacks {
    val callback = object : FragmentManager.FragmentLifecycleCallbacks() {

        override fun onFragmentDestroyed(fm: FragmentManager, f: Fragment) {
            super.onFragmentDestroyed(fm, f)
            onFragmentDestroyed(f)
        }

        override fun onFragmentViewCreated(
            fm: FragmentManager,
            f: Fragment,
            v: View,
            savedInstanceState: Bundle?
        ) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            if (f is BaseBottomSheetFragment<*>) onBottomSheetViewCreated(f)
            else onFragmentViewCreated(f)
        }

        override fun onFragmentStarted(fm: FragmentManager, f: Fragment) {
            super.onFragmentStarted(fm, f)
            onFragmentStarted(f)
        }

        override fun onFragmentStopped(fm: FragmentManager, f: Fragment) {
            super.onFragmentStopped(fm, f)
            onFragmentStopped(f)
        }


    }
    return callback
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

//var TextView.maxLength: Int
//    get() = filters.filterIsInstance<InputFilter.LengthFilter>().firstOrNull()?.max ?: 0
//    set(value) {
//        filters = arrayOf(InputFilter.LengthFilter(value))
//    }

fun TextView.setMaxLength(max : Int){
    filters = arrayOf(InputFilter.LengthFilter(max))
}

fun TextView.setMinMaxLines(min : Int, max : Int){
    minLines = min
    maxLines = max
}


fun ImageView.setCircleImage(url: String?, placeholder: Int? = null) {
    Picasso.get().load(url.let { if (it.isNullOrBlank()) null else it })
        .transform(CropCircleTransformation())
        .apply { placeholder?.let { placeholder(it) } }
        .into(this)
}

fun Context.isConnectedToNetwork(): Boolean {
    val connectivityManager =
        this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return connectivityManager?.activeNetworkInfo?.isConnected ?: false
}

fun Group.setTextDataOrHide(textField: TextView, dataText: CharSequence?) {
    if (dataText.isNullOrBlank()) {
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

fun Bitmap.toBodyPart(
    name: String,
    fileName: String,
    compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
): MultipartBody.Part {
    return let { bitmap ->
        val byteArray = ByteArrayOutputStream().let {
            bitmap.compress(compressFormat, 100, it)
            it.toByteArray()
        }

        val body = byteArray.toRequestBody("application/octet-stream".toMediaTypeOrNull())
        MultipartBody.Part.createFormData(name, fileName, body)
    }
}

fun String?.isValidPhoneNumber(context: Context, defaultRegion: String? = null): Boolean {
    val phoneNumberUtil = PhoneNumberUtil.createInstance(context)
    return isValidPhoneNumber(phoneNumberUtil, defaultRegion)
}

fun String?.isValidPhoneNumber(
    phoneNumberUtil: PhoneNumberUtil,
    defaultRegion: String? = null
): Boolean {
    if (this.isNullOrEmpty()) return false
    val parsedPhone = try {
        phoneNumberUtil.parse(this, defaultRegion)
    } catch (e: Throwable) {
        return false
    }
    return phoneNumberUtil.isValidNumber(parsedPhone)
}

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

fun Uri.fileName(contentResolver: ContentResolver): String? {
    return contentResolver.query(this, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)
        ?.use { cursor ->
            cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME).let { nameIndex ->
                cursor.moveToFirst()
                if (nameIndex >= 0) cursor.getString(nameIndex)
                else null
            }
        }
}

inline fun <reified T> JsonElement?.fromJson(deserializer: JsonDeserializer<T>? = null): T? {
    if (this == null) return null
    return GsonBuilder()
        .apply {
            if (deserializer != null) registerTypeAdapter(T::class.java, deserializer)
        }
        .create()
        .fromJson(this, T::class.java)
}

fun View.setOnClickListener(listener: () -> Unit) {
    setOnClickListener { listener() }
}

fun View.onClickListener(listener: (() -> Unit)?) {
    if (listener != null) setOnClickListener { listener.invoke() }
}

fun JSONObject.getStringOrNull(name: String): String? {
    return if (has(name)) getString(name) else null
}

fun <E> SparseArray<E>.getOrPut(key: Int, put: () -> E): E {
    var value = get(key)
    if (value == null) {
        value = put()
        put(key, value)
    }

    return value
}

fun String?.parseColor(): Int? {
    if (this == null || isEmpty()) return null

    return try {
        Color.parseColor(this)
    } catch (e: IllegalArgumentException) {
        null
    }
}

var Fragment.statusBarColorValue: Int
    get() = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    set(value) {
        if (requireActivity().window.decorView.systemUiVisibility == value) return
        else requireActivity().window.decorView.systemUiVisibility = value
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


