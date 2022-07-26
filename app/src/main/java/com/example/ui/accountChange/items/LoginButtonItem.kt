package com.example.ui.accountChange.items

import com.example.R
import com.example.databinding.ItemLoginAccountButtonBinding
import com.xwray.groupie.databinding.BindableItem
import setOnClickListener

class LoginButtonItem(val onActionClick: () -> Unit) :
    BindableItem<ItemLoginAccountButtonBinding>() {


    override fun bind(viewBinding: ItemLoginAccountButtonBinding, position: Int) {
        viewBinding.clAdd.setOnClickListener {
            onActionClick.invoke()
        }
    }

    override fun getLayout(): Int = R.layout.item_login_account_button
}