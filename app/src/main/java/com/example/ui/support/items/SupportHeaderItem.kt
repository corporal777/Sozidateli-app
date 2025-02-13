package com.example.ui.support.items

import android.content.Context
import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemSupportCenterHeaderBinding
import com.xwray.groupie.viewbinding.BindableItem

class SupportHeaderItem(
    val context: Context,
    val onSearchClick: () -> Unit,
    val onMicrophoneClick: () -> Unit
) : BindableItem<ItemSupportCenterHeaderBinding>(-1001L) {

    override fun bind(viewBinding: ItemSupportCenterHeaderBinding, position: Int) {
        viewBinding.apply {
            etSearch.apply {
                setOnClickListener {
                    onSearchClick.invoke()
                }
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is SupportHeaderItem) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemSupportCenterHeaderBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_support_center_header
}