import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import com.example.extensions.defaultServerDateFormatter
import com.example.util.CropCircleTransformation
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR
import com.example.util.DATE_FORMAT_FULL_MONTH_NO_YEAR
import com.example.util.Utils
import com.squareup.picasso.Picasso
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

fun ImageView.setCircleImageWithPlaceholder(url: String?, placeholder: Int) {
    Picasso.get().load(url.let { if (it.isNullOrBlank()) null else it })
            .transform(CropCircleTransformation())
            .placeholder(placeholder)
            .into(this)
}

fun SimpleDateFormat.parseTimestamp(source: String): Long {
    return this.parse(source).time
}


fun Context.isConnectedToNetwork(): Boolean {
    val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    return connectivityManager?.activeNetworkInfo?.isConnected ?: false
}