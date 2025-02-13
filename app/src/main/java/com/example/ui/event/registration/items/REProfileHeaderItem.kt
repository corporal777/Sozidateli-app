package com.example.ui.event.registration.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventProfileFooterBinding
import com.xwray.groupie.viewbinding.BindableItem

class REProfileHeaderItem(
    val itemId : Long,
    val onProfileClick: () -> Unit
) : BindableItem<ItemRegisterEventProfileFooterBinding>(itemId) {


    override fun bind(viewBinding: ItemRegisterEventProfileFooterBinding, position: Int) {
        viewBinding.apply {
            btnGoToProfile.setOnClickListener {
                onProfileClick.invoke()
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is REProfileHeaderItem) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemRegisterEventProfileFooterBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_register_event_profile_footer
}