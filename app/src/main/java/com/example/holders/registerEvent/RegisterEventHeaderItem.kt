package com.example.holders.registerEvent

import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.EventRegistration
import com.example.app.databinding.ItemRegisterEventHeaderBinding
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.extensions.formatToDefaultDate
import com.example.extensions.isSameDay
import com.example.extensions.markWon
import com.example.extensions.parseToDate
import com.xwray.groupie.databinding.BindableItem

class RegisterEventHeaderItem(
    id: Long,
    private val event: EventRegistration,
) : BindableItem<ItemRegisterEventHeaderBinding>(id) {


    private val eventStartDate = getEventDate()


    override fun bind(viewBinding: ItemRegisterEventHeaderBinding, position: Int) {
        viewBinding.apply {
            tvFormLabel.apply {
                isVisible = !event.registrationHeadline.isNullOrBlank()
                text = event.registrationHeadline
            }
            tvEventDate.text = eventStartDate
            tvFormDescription.apply {
                isVisible = !event.registrationSubtitle.isNullOrBlank()
                markWon(context).setMarkdown(this, event.registrationSubtitle ?: "")
            }
        }
    }

    private fun getEventDate(): String? {
        val dateStart = event.conferenceStart ?: return null
        val dateEnd = event.conferenceFinish ?: return null

        val startDate =
            dateStart.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null
        val finishDate =
            dateEnd.parseToDate(defaultServerDateFormatter)?.calendar() ?: return null

        return "Дата проведения " +
                if (startDate.isSameDay(finishDate)) dateStart.formatToDefaultDate()
                else dateStart.formatToDefaultDate() + " - " + dateEnd.formatToDefaultDate()
    }

    override fun getLayout(): Int = R.layout.item_register_event_header
}