package com.example.ui.event.about.items

import android.view.View
import androidx.core.view.updatePadding
import com.example.app.R
import com.example.app.databinding.ItemEventDetailLabelBlockBinding
import com.xwray.groupie.viewbinding.BindableItem

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

    override fun initializeViewBinding(view: View) = ItemEventDetailLabelBlockBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_detail_label_block
}