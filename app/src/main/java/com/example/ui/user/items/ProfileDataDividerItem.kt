package com.example.ui.user.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemProfileDataDividerBinding
import com.xwray.groupie.viewbinding.BindableItem

class ProfileDataDividerItem () : BindableItem<ItemProfileDataDividerBinding>() {


    override fun bind(viewBinding: ItemProfileDataDividerBinding, position: Int) {
    }

    override fun initializeViewBinding(view: View) = ItemProfileDataDividerBinding.bind(view)
    override fun getLayout() = R.layout.item_profile_data_divider
}