package com.example.ui.event.about.items

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.RequestApplyModel
import com.example.data.models.RequestsApplyModel
import com.example.databinding.ItemEventDetailImageBlockBinding
import com.example.extensions.*
import com.example.ui.event.location.map.redesign.MapPresenterNew
import com.example.ui.views.CustomSpannableString
import com.example.util.setImage
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
    private var requestDate = ""

    init {
        if (requestsApply != null) {
            if (!requestsApply.dateFrom.isNullOrEmpty()) {
                val today = System.currentTimeMillis()
                val startReq = defaultServerDateFormatter.parse(requestsApply.dateFrom)?.time ?: 0
                if (startReq > today) {
                    val day = daysBetweenNew(today, startReq)
                    requestDate = when (day) {
                        1 -> "До начала приема заявок $day день"
                        in 2..4 -> "До начала приема заявок $day дня"
                        else -> "До начала приема заявок $day дней"
                    }
                } else {
                    if (!requestsApply.dateLimit.isNullOrEmpty()) {
                        val limitDate = requestsApply.dateLimit.parseAndFormat(
                            defaultServerDateFormatter,
                            dateFormatterShortDayFullMothShortYear
                        )
                        requestDate = "Заявки принимаются по $limitDate"
                    }
                }
            } else {
                if (!requestsApply.dateLimit.isNullOrEmpty()) {
                    val limitDate = requestsApply.dateLimit.parseAndFormat(
                        defaultServerDateFormatter,
                        dateFormatterShortDayFullMothShortYear
                    )
                    requestDate = "Заявки принимаются по $limitDate"
                }
            }
        }
    }

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
                setImage(logo ?: imageColor)
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

    private fun daysBetweenNew(d1: Long, d2: Long): Int {
        var days = 0
        for (i in d1..d2 step 86400000) days++
        return days
    }



    override fun getLayout(): Int = R.layout.item_event_detail_image_block


}