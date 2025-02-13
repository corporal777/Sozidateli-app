package com.example.ui.organizations.detail.items

import android.view.View
import androidx.core.view.updatePadding
import com.example.app.R
import com.example.app.databinding.ItemEventsTitleBinding
import com.example.extensions.dp
import com.xwray.groupie.Item
import com.xwray.groupie.viewbinding.BindableItem

class EventsTitleItem(
    val title: String,
    val pTop: Int = 0,
    val pBottom: Int = 0,
    val pLeft: Int = 0,
    val pRight: Int = 0,
) : BindableItem<ItemEventsTitleBinding>(-1005L) {

    override fun bind(viewBinding: ItemEventsTitleBinding, position: Int) {
        viewBinding.tvTitle.apply {
            text = title
            if (pTop != 0) {
                updatePadding(top = pTop.dp)
            }
            if (pBottom != 0) {
                updatePadding(bottom = pBottom.dp)
            }
            if (pLeft != 0) {
                updatePadding(left = pLeft.dp)
            }
            if (pRight != 0) {
                updatePadding(right = pRight.dp)
            }
        }
    }

    override fun hasSameContentAs(other: Item<*>): Boolean {
        if (other !is EventsTitleItem) return false
        if (title != other.title) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemEventsTitleBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_events_title
}