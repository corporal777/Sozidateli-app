package com.example.ui.event.about.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventNew
import com.example.databinding.ItemEventDetailImageBlockBinding
import com.example.extensions.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.parseColor
import com.example.util.setImage
import com.example.util.setImagePicasso

class EventDetailImageItem(val event: EventNew) :
    BindableItem<ItemEventDetailImageBlockBinding>(1000L) {

    private val eventDate =
        event.holdingDate?.from?.formatToDefaultDate() + " - " + event.holdingDate?.to?.formatToDefaultDate()
    private val imageColor =
        ColorDrawable(event.backgroundColor?.value.parseColor() ?: Color.DKGRAY)
    private val requestDate = getEventRequestDate()

    override fun bind(viewBinding: ItemEventDetailImageBlockBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = event.name
            tvDate.text = eventDate
            tvRequestsDate.apply {
                isVisible = !requestDate.isNullOrEmpty()
                text = requestDate
            }
            tvLocation.apply {
                isVisible = !event.address?.getShortAddress().isNullOrEmpty()
                text = event.address?.getShortAddress()
            }
            ivLogo.apply {
                setImagePicasso(url = event.image?.uri, placeholder = imageColor, error = imageColor)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventDetailImageItem) return false
        if (event != other.event) return false
        return true
    }

    private fun getEventRequestDate(): String? {
        val requestsApply = event.requestsApply ?: return null
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
                val limitDate = requestsApply.dateLimit.parseAndFormat(
                    defaultServerDateTimeFormatter,
                    dateFormatterShortMonthShortYear
                )
                val limitTime = requestsApply.dateLimit.parseAndFormat(
                    defaultServerDateTimeFormatter,
                    defaultTimeFormatter
                )

                return "Заявки принимаются по $limitDate, $limitTime"
            }
        } else return null
    }


    override fun getLayout(): Int = R.layout.item_event_detail_image_block


}