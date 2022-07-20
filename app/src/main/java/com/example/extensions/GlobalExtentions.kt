import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.net.Uri
import android.provider.OpenableColumns
import android.text.Editable
import android.text.InputFilter
import android.text.Layout
import android.text.TextWatcher
import android.text.style.URLSpan
import android.util.SparseArray
import android.util.TypedValue
import android.view.KeyEvent.ACTION_UP
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
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
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.user.User
import com.example.extensions.defaultServerDateFormatter
import com.example.util.*
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
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.YEAR
import kotlin.math.roundToInt

fun TextView.setDatesIntervalText(startDate: Long, finishDate: Long) {
    this.text = Utils.getDatesInterval(startDate, finishDate)
}

fun TextView.setDatesIntervalText(startDate: String?, finishDate: String?) {
    this.text = Utils.getDatesInterval(startDate, finishDate)
}

fun TextView.setDateCheckYearText(date: Long) {
    val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
    val now = Calendar.getInstance()

    val format =
        if (dateCalendar.get(YEAR) == now.get(YEAR)) DATE_FORMAT_FULL_MONTH_NO_YEAR else DATE_FORMAT_FULL_MONTH_FULL_YEAR
    val formatted = SimpleDateFormat(format, Locale.getDefault()).format(dateCalendar.time)

    this.text = formatted
}

fun TextView.setDateCheckYearText(date: String) {
    try {
        val dateLong = defaultServerDateFormatter.parse(date).time
        setDateCheckYearText(dateLong)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun TextView.removeUrlUnderline(textColor: Int? = null) {
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

fun TextView.onTextChanged(onTextChanged: (text: CharSequence?) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) =
            onTextChanged(s)
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

fun RecyclerView.onScrolled(onScrolled : (dx: Int, dy: Int) -> Unit) : RecyclerView.OnScrollListener{
    val listener = object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
        }
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) = onScrolled(dx, dy)
    }
    addOnScrollListener(listener)
    return listener
}

fun NestedScrollView.onScrolled(onScrolled : (scrollY : Int, oldScrollY : Int, scrollX : Int, oldScrollX : Int) -> Unit) : NestedScrollView.OnScrollChangeListener {
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

var TextView.maxLength: Int
    get() = filters.filterIsInstance<InputFilter.LengthFilter>().firstOrNull()?.max ?: 0
    set(value) {
        filters = arrayOf(InputFilter.LengthFilter(value))
    }

fun TextView.setUserStatus(status: User.Status, toFormat: String? = null) {
    val statusTextRes: Int
    val statusTextColorRes: Int
    val statusBackgroundStyleRes: Int
    when (status) {
        User.Status.LOW_PROTECTION -> {
            statusTextRes = R.string.profile_status_low
            statusTextColorRes = R.color.profile_status_low_text
            statusBackgroundStyleRes = R.style.ViewBackgroundStatusLow
        }
        User.Status.MID_PROTECTION -> {
            statusTextRes = R.string.profile_status_mid
            statusTextColorRes = R.color.profile_status_mid_text
            statusBackgroundStyleRes = R.style.ViewBackgroundStatusMid
        }
        User.Status.MAX_PROTECTION -> {
            statusTextRes = R.string.profile_status_max
            statusTextColorRes = R.color.profile_status_max_text
            statusBackgroundStyleRes = R.style.ViewBackgroundStatusMax
        }
    }

    val statusText = resources.getString(statusTextRes).toUpperCase(Locale.getDefault())
    text = toFormat?.format(statusText) ?: statusText
    setTextColor(ContextCompat.getColor(context, statusTextColorRes))
    background = ResourcesCompat.getDrawable(
        resources,
        R.drawable.background_corners,
        ContextThemeWrapper(context, statusBackgroundStyleRes).theme
    )
}

fun User.Status.getUserStatusText(context: Context): String {
    return "${context.getString(R.string.status_your_status)} ${
        context.getString(
            when (this) {
                User.Status.LOW_PROTECTION -> R.string.profile_status_low
                User.Status.MID_PROTECTION -> R.string.profile_status_mid
                User.Status.MAX_PROTECTION -> R.string.profile_status_max
            }
        )
    }"
}

fun ImageView.setCircleImage(url: String?, placeholder: Int? = null) {
    Picasso.get().load(url.let { if (it.isNullOrBlank()) null else it })
        .transform(CropCircleTransformation())
        .apply { placeholder?.let { placeholder(it) } }
        .into(this)
}

fun ImageView.setCircleImage(bitmap: Bitmap?, placeholder: Int? = null) {
    if (bitmap == null) setImageResource(placeholder ?: return)
    else setImageBitmap(CropCircleTransformation().transform(bitmap))
}

fun SimpleDateFormat.parseTimestamp(source: String): Long {
    return this.parse(source).time
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

    setEndIconDrawable(R.drawable.ic_calendar)
    setEndIconTintMode(PorterDuff.Mode.MULTIPLY)
    setEndIconOnClickListener { showDatePicker() }
    errorIconDrawable = null
    editText?.apply {
        isCursorVisible = false
        isFocusableInTouchMode = false
        setOnTouchListener { _, event ->
            if (event.action == ACTION_UP) showDatePicker()
            true
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

fun View.onClickListener(listener: () -> Unit) {
    setOnClickListener { listener() }
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