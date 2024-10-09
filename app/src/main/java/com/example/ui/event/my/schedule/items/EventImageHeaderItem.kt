package com.example.ui.event.my.schedule.items

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import com.example.app.R
import com.example.app.databinding.ItemEventImageHeaderBinding
import com.example.util.setImage
import com.xwray.groupie.databinding.BindableItem
import com.example.extensions.parseColor

class EventImageHeaderItem(
    val id: String?,
    val title: String?,
    val image: String?,
    val backgroundColor: String?,
    val onHeaderClick: () -> Unit
) : BindableItem<ItemEventImageHeaderBinding>(id?.toLong() ?: 0) {

    private var imageColor = ColorDrawable(Color.DKGRAY)

    init {
        if (!backgroundColor.isNullOrEmpty()) {
            val color = backgroundColor.parseColor() ?: Color.DKGRAY
            imageColor = ColorDrawable(color)
        }
    }

    override fun bind(viewBinding: ItemEventImageHeaderBinding, position: Int) {
        viewBinding.apply {
            cardHeader.setOnClickListener {
                onHeaderClick.invoke()
            }
            eventTitle.text = title
            eventImage.setImage(image ?: imageColor)
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventImageHeaderItem) return false
        if (title != other.title) return false
        if (image != other.image) return false
        if (backgroundColor != other.backgroundColor) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_event_image_header
}