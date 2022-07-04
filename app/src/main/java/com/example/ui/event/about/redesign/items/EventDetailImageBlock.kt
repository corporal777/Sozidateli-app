package com.example.ui.event.about.redesign.items

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventNew
import com.example.databinding.ItemEventDetailImageBlockBinding
import com.example.extensions.formatToEventDatesIntervalOnMain
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem

class EventDetailImageBlock(
    private val eventData: EventNew?
) : BindableItem<ItemEventDetailImageBlockBinding>() {

    val backgroundColor: String? = eventData?.binds?.organization?.backgroundColor?.value
    val logo: String? = eventData?.image?.uri

    val date: String =
        eventData?.holdingDate?.from.formatToEventDatesIntervalOnMain(eventData?.holdingDate?.to)
            ?: ""

    @SuppressLint("CheckResult")
    override fun bind(viewBinding: ItemEventDetailImageBlockBinding, position: Int) {
        viewBinding.apply {

            tvDate.text = date
            if (!eventData?.address?.getShortAddress().isNullOrEmpty()) {
                tvLocation.text = eventData?.address?.getShortAddress()
            } else {
                tvLocation.isVisible = false
            }

            tvTitle.text = eventData?.name
            ivLogo.setImage(logo)
        }
    }

    override fun getLayout(): Int = R.layout.item_event_detail_image_block


}