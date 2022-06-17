package com.example.ui.event.my.schedule.items

import com.example.R
import com.example.databinding.ItemEventImageHeaderBinding
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import setOnClickListener

class EventImageHeaderItem(
    val title: String?,
    val image: String?
) : BindableItem<ItemEventImageHeaderBinding>() {

    override fun bind(viewBinding: ItemEventImageHeaderBinding, position: Int) {
        viewBinding.apply {
            cardHeader.setOnClickListener {

            }
            eventTitle.text = title
            eventImage.setImage(image)
        }
    }

    override fun getLayout(): Int = R.layout.item_event_image_header
}