import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.PorterDuff
import android.net.ConnectivityManager
import android.text.Editable
import android.text.InputFilter
import android.text.Layout
import android.text.TextWatcher
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.KeyEvent.ACTION_UP
import android.view.MotionEvent.ACTION_POINTER_UP
import android.view.View
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
import com.example.R
import com.example.data.models.user.User
import com.example.extensions.defaultServerDateFormatter
import com.example.util.*
import com.google.android.material.textfield.TextInputLayout
import com.squareup.picasso.Picasso
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    val format = if (dateCalendar.get(YEAR) == now.get(YEAR)) DATE_FORMAT_FULL_MONTH_NO_YEAR else DATE_FORMAT_FULL_MONTH_FULL_YEAR
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

fun TextView.removeUrlUnderline() {
    text.toSpannable().apply {
        val urls = getSpans<URLSpan>()
        urls.forEach {
            val start = getSpanStart(it)
            val end = getSpanEnd(it)
            removeSpan(it)
            set(start..end, URLSpanNoUnderline(it.url))
        }
    }
}

fun TextView.onTextChanged(onTextChanged: (text: CharSequence?) -> Unit) {
    addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = onTextChanged(s)
    })
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

val TextView.maxLength: Int
    get() = filters.filterIsInstance<InputFilter.LengthFilter>().firstOrNull()?.max ?: 0

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
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
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

fun View.setSelectableItemBackgroundBorderless() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackgroundBorderless, this, true)
    setBackgroundResource(resourceId)
}

fun Bitmap.toBodyPart(name: String, fileName: String, compressFormat: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG): MultipartBody.Part {
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
    if (this.isNullOrEmpty()) return false
    val phoneNumberUtil = PhoneNumberUtil.createInstance(context)
    val parsedPhone = try {
        phoneNumberUtil.parse(this, defaultRegion)
    } catch (e: Throwable) {
        return false
    }
    return phoneNumberUtil.isValidNumber(parsedPhone)
}

fun TextInputLayout.initAsDatePicker(startDate: Date?, transformDate: (year: Int, month: Int, day: Int) -> String?) {
    val showDatePicker = {
        val calendar = Calendar.getInstance().apply { time = startDate ?: Date() }
        DatePickerDialog(context, R.style.AlertDialogTheme, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            editText?.setText(transformDate(year, month, dayOfMonth))
        }, calendar.get(YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                .show()
    }
    setEndIconDrawable(R.drawable.ic_calendar)
    setEndIconTintMode(PorterDuff.Mode.MULTIPLY)
    setEndIconOnClickListener { showDatePicker() }
    editText?.apply {
        isCursorVisible = false
        isFocusableInTouchMode = false
        setOnTouchListener { _, event ->
            if (event.action == ACTION_UP) showDatePicker()
            true
        }
    }
}