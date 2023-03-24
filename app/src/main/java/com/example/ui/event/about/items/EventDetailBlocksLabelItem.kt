package com.example.ui.event.about.items

import androidx.core.view.updatePadding
import com.example.R
import com.example.databinding.ItemEventDetailLabelBlockBinding
import com.xwray.groupie.databinding.BindableItem

class EventDetailBlocksLabelItem(
    val title: String,
    val padding: Int = 0,
    val id: Long? = null
) : BindableItem<ItemEventDetailLabelBlockBinding>(id ?: 0) {

    override fun bind(viewBinding: ItemEventDetailLabelBlockBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
            if (padding != 0) {
                titleContainer.updatePadding(top = padding)
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_event_detail_label_block
}