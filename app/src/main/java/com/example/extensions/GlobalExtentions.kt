import android.content.Context
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.text.Editable
import android.text.TextWatcher
import android.text.style.URLSpan
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.text.toSpannable
import com.example.extensions.defaultServerDateFormatter
import com.example.util.*
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.YEAR

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