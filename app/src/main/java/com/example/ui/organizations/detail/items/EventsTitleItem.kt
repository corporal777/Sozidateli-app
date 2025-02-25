package com.example.ui.organizations.detail.items

import android.view.View
import androidx.core.view.updatePadding
import com.example.app.R
import com.example.app.databinding.ItemEventsTitleBinding
import com.example.extensions.dp
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class EventsTitleItem(val title: String) : BindableItem<ItemEventsTitleBinding>(-1005L) {

    override fun bind(viewBinding: ItemEventsTitleBinding, position: Int) {
        viewBinding.tvTitle.text = title
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is EventsTitleItem) return false
        if (title != other.title) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemEventsTitleBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_events_title
}