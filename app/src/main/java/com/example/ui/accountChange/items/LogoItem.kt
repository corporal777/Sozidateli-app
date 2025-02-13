package com.example.ui.accountChange.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemAccountChangeLogoBinding
import com.xwray.groupie.viewbinding.BindableItem

class LogoItem : BindableItem<ItemAccountChangeLogoBinding>() {

    override fun bind(viewBinding: ItemAccountChangeLogoBinding, position: Int) {

    }

    override fun initializeViewBinding(view: View) = ItemAccountChangeLogoBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_account_change_logo
}