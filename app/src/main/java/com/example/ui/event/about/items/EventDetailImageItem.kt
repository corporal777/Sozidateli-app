package com.example.ui.event.about.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.view.isVisible
import coil.ImageLoader
import com.example.R
import com.example.data.models.RequestApplyModel
import com.example.databinding.ItemEventDetailImageBlockBinding
import com.example.extensions.*
import com.example.util.setImage
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import parseColor

class EventDetailImageItem(
    val name: String?,
    val address: String?,
    val dateFrom: String?,
    val dateTo: String?,
    val logo: String?,
    val backgroundColor: String?,
    val requestsApply: RequestApplyModel?
) : BindableItem<ItemEventDetailImageBlockBinding>(1000L) {

    private val eventDate = dateFrom.formatToEventDatesIntervalOnMain(dateTo) ?: ""
    private val imageColor = ColorDrawable(backgroundColor.parseColor() ?: Color.DKGRAY)
    private val requestDate = getEventRequestDate()

    override fun bind(viewBinding: ItemEventDetailImageBlockBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = name
            tvDate.text = eventDate
            tvRequestsDate.apply {
                isVisible = !requestDate.isNullOrEmpty()
                text = requestDate
            }
            tvLocation.apply {
                isVisible = !address.isNullOrEmpty()
                text = address
            }
            ivLogo.apply {
                Picasso.get()
                    .load(logo)
                    .placeholder(imageColor)
                    .error(imageColor)
                    .into(this)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventDetailImageItem) return false
        if (name != other.name) return false
        if (address != other.address) return false
        if (dateFrom != other.dateFrom) return false
        if (dateTo != other.dateTo) return false
        if (logo != other.logo) return false
        if (backgroundColor != other.backgroundColor) return false
        if (requestsApply != other.requestsApply) return false
        return true
    }




    private fun getEventRequestDate() : String? {
        if (requestsApply == null) return null
        if (!requestsApply.dateFrom.isNullOrEmpty() && !requestsApply.dateLimit.isNullOrEmpty()) {
            val today = System.currentTimeMillis()
            val startReq = defaultServerDateTimeFormatter.parse(requestsApply.dateFrom)?.time ?: 0
            if (startReq > today) {
                val day = daysBetween(today, startReq)
                return when (day) {
                    1 -> "До начала приема заявок $day день"
                    in 2..4 -> "До начала приема заявок $day дня"
                    else -> "До начала приема заявок $day дней"
                }
            } else {
                val limitDate = requestsApply.dateLimit.parseAndFormat(defaultServerDateTimeFormatter, dateFormatterShortMonthShortYear)
                val limitTime = requestsApply.dateLimit.parseAndFormat(defaultServerDateTimeFormatter, defaultTimeFormatter)

                return "Заявки принимаются по $limitDate, $limitTime"
            }
        } else return null
    }


    override fun getLayout(): Int = R.layout.item_event_detail_image_block


}