import android.content.res.Resources
import android.widget.TextView
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR
import com.example.util.DATE_FORMAT_FULL_MONTH_NO_YEAR
import com.example.util.Utils
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.YEAR

fun TextView.setDatesIntervalText(startDate: Long, finishDate: Long) {
    this.text = Utils.getDatesInterval(startDate, finishDate)
}

fun TextView.setDatesIntervalText(startDate: String, finishDate: String) {
    this.text = Utils.getDatesInterval(startDate, finishDate)
}

fun TextView.setDateCheckYearText(date: Long) {
    val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
    val now = Calendar.getInstance()

    val format = if (dateCalendar.get(YEAR) == now.get(YEAR)) DATE_FORMAT_FULL_MONTH_NO_YEAR else DATE_FORMAT_FULL_MONTH_FULL_YEAR
    val formatted = SimpleDateFormat(format, Locale.getDefault()).format(dateCalendar.time)

    this.text = formatted
}

val Float.dp: Float
    get() = (this * Resources.getSystem().displayMetrics.density)
val Float.px: Float
    get() = (this / Resources.getSystem().displayMetrics.density)

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()
val Int.px: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()