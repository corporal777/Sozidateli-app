package com.example.holders.registerEvent

import android.text.InputType
import com.example.R
import com.example.data.models.RegisterEventField
import com.example.ui.request.RequestPresenter
import com.example.util.SimpleTextWatcher
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.register_event_input.view.*

open class RegisterEventNumberItem(private val fieldRegister:RegisterEventField, presenter: RequestPresenter) : RegisterEventStringItem(fieldRegister,presenter) {
    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        super.bind(viewHolder, position)

        viewHolder.itemView.etInput.apply {
            inputType = InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_CLASS_NUMBER
        }
    }
}