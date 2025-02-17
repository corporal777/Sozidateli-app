package com.example.ui.userSessions.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemSessionsHeaderBinding
import com.xwray.groupie.viewbinding.BindableItem

class SessionsHeaderItem(val title : String): BindableItem<ItemSessionsHeaderBinding>() {

    override fun bind(viewBinding: ItemSessionsHeaderBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
        }
    }

    override fun initializeViewBinding(view: View) = ItemSessionsHeaderBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_sessions_header
}