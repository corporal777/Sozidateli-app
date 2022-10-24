package com.example.ui.event.my.schedule.items

import com.example.R
import com.example.databinding.ItemEventImageHeaderBinding
import com.example.holders.redesign.EventActivityItem
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import setOnClickListener

class EventImageHeaderItem(
    val title: String?,
    val image: String?,
    val onHeaderClick: () -> Unit
) : BindableItem<ItemEventImageHeaderBinding>() {

    override fun bind(viewBinding: ItemEventImageHeaderBinding, position: Int) {
        viewBinding.apply {
            cardHeader.setOnClickListener {
                onHeaderClick.invoke()
            }
            eventTitle.text = title
            eventImage.setImage(image)
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventImageHeaderItem) return false
        if (title != other.title) return false
        if (image!= other.image) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_event_image_header
}