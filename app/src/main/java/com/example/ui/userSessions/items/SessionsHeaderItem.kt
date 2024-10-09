package com.example.ui.userSessions.items

import com.example.app.R
import com.example.app.databinding.ItemSessionsHeaderBinding
import com.xwray.groupie.databinding.BindableItem

class SessionsHeaderItem(val title : String): BindableItem<ItemSessionsHeaderBinding>() {

    override fun bind(viewBinding: ItemSessionsHeaderBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
        }
    }

    override fun getLayout(): Int = R.layout.item_sessions_header
}