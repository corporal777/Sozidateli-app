package com.example.ui.event.registration.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.ItemRegisterEventHeaderNewBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToEventDatesIntervalOnMain
import com.example.util.markWon
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import parseColor
import java.util.*

class RegisterEventImageHeaderItem(
    id: Long,
    private val logo: String?,
    private val backgroundColor: String?,
    private val formTitle: String?,
    private val formDescription: String?,
    private val dateFrom: String?,
    private val dateTo: String?,
) : BindableItem<ItemRegisterEventHeaderNewBinding>(id) {

    private var eventStartDate = ""
    private var imageColor = ColorDrawable(Color.DKGRAY)

    init {
        val cal = defaultServerDateFormatter.parse(dateFrom).time.calendar()
//        eventStartDate = "Дата проведения " + cal.get(Calendar.DAY_OF_MONTH) + " " + cal.getDisplayName(
//            Calendar.MONTH,
//            Calendar.LONG,
//            Locale.getDefault()
//        ) + " " + cal.get(Calendar.YEAR) + " г."
        eventStartDate =
            "Дата проведения " + dateFrom.formatToEventDatesIntervalOnMain(dateTo) ?: ""

        if (!backgroundColor.isNullOrEmpty()) {
            val color = backgroundColor.parseColor() ?: Color.DKGRAY
            imageColor = ColorDrawable(color)
        }

    }

    override fun bind(viewBinding: ItemRegisterEventHeaderNewBinding, position: Int) {
        viewBinding.apply {
            ivLogo.setImage(logo ?: imageColor)
            tvFormLabel.apply {
                isVisible = !formTitle.isNullOrBlank()
                text = formTitle
            }
            tvEventDate.text = eventStartDate
            tvFormDescription.apply {
                isVisible = !formDescription.isNullOrBlank()
                markWon(context).setMarkdown(this, formDescription ?: "")
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_register_event_header_new
}