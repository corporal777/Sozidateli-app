package com.example.ui.accountChange.items

import com.example.app.R
import com.example.app.databinding.ItemUnloggedAccountHeaderBinding
import com.xwray.groupie.databinding.BindableItem

class UnLoggedAccountsHeader : BindableItem<ItemUnloggedAccountHeaderBinding>() {

    override fun bind(viewBinding: ItemUnloggedAccountHeaderBinding, position: Int) {

    }

    override fun getLayout(): Int = R.layout.item_unlogged_account_header
}