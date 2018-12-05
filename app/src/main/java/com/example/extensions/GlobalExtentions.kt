import android.widget.TextView
import com.example.util.DATE_FORMAT_FULL_MONTH_FULL_YEAR
import com.example.util.DATE_FORMAT_FULL_MONTH_NO_YEAR
import com.example.util.DATE_FORMAT_SHORT_MONTH_FULL_YEAR
import com.example.util.DATE_FORMAT_SHORT_MONTH_NO_YEAR
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.YEAR

fun TextView.setDatesIntervalText(startDate: Long, finishDate: Long) {
    val start = Calendar.getInstance().apply { timeInMillis = startDate }
    val finish = Calendar.getInstance().apply { timeInMillis = finishDate }

    val startFormat = if (start.get(YEAR) == finish.get(YEAR)) DATE_FORMAT_SHORT_MONTH_NO_YEAR else DATE_FORMAT_SHORT_MONTH_FULL_YEAR
    val formattedStart = SimpleDateFormat(startFormat, Locale.getDefault()).format(start.time)
    val formattedFinish = SimpleDateFormat(DATE_FORMAT_SHORT_MONTH_FULL_YEAR, Locale.getDefault()).format(finish.time)

    val result = "$formattedStart - $formattedFinish"
    this.text = result
}

fun TextView.setDateCheckYearText(date: Long) {
    val dateCalendar = Calendar.getInstance().apply { timeInMillis = date }
    val now = Calendar.getInstance()

    val format = if (dateCalendar.get(YEAR) == now.get(YEAR)) DATE_FORMAT_FULL_MONTH_NO_YEAR else DATE_FORMAT_FULL_MONTH_FULL_YEAR
    val formatted = SimpleDateFormat(format, Locale.getDefault()).format(dateCalendar.time)

    this.text = formatted

}