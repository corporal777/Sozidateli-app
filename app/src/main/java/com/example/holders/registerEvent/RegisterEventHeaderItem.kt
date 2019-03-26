package com.example.holders.registerEvent

import android.text.InputType
import com.example.R
import com.example.data.models.RegisterEventField
import com.example.ui.request.RequestPresenter
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.register_event_input.view.*
import kotlinx.android.synthetic.main.register_event_header_item.view.*

open class RegisterEventHeaderItem(private val presenter: RequestPresenter) : Item() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.apply {
            btnClose.setOnClickListener {
                presenter.onCloseClick()
            }
        }
    }

    override fun getLayout() = R.layout.register_event_header_item
}