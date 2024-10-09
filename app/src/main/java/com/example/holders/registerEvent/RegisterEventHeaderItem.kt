package com.example.holders.registerEvent

import androidx.core.view.isVisible
import com.example.app.R
import com.example.data.models.EventRegistration
import com.example.app.databinding.ItemRegisterEventHeaderBinding
import com.example.extensions.formatToDefaultDate
import com.example.extensions.markWon
import com.xwray.groupie.databinding.BindableItem

class RegisterEventHeaderItem(
    id: Long,
    private val event: EventRegistration,
) : BindableItem<ItemRegisterEventHeaderBinding>(id) {


    private val eventStartDate = "Дата проведения " +
            event.conferenceStart?.formatToDefaultDate() + " - " +
            event.conferenceFinish?.formatToDefaultDate()

    
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

    override fun getLayout(): Int = R.layout.item_register_event_header
}