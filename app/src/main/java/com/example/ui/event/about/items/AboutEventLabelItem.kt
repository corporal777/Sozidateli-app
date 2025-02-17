package com.example.ui.event.about.items

import android.view.View
import androidx.core.view.updatePadding
import com.example.app.R
import com.example.app.databinding.ItemEventDetailLabelBlockBinding
import com.xwray.groupie.viewbinding.BindableItem

class AboutEventLabelItem(
    val titleRes: Int
) : BindableItem<ItemEventDetailLabelBlockBinding>() {

    override fun bind(viewBinding: ItemEventDetailLabelBlockBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = root.context.getString(titleRes)
        }
    }

    override fun initializeViewBinding(view: View) = ItemEventDetailLabelBlockBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_event_detail_label_block
}