package com.example.holders.registerEvent

import android.text.TextWatcher
import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_event_registration_file.*
import onTextChanged

open class EventRegistrationFileItem(
        id: Long,
        private val filename: String,
        private val editable: Boolean,
        private val onRemoveFileClick: () -> Unit,
        private val onNameChange: (String) -> Unit
) : Item(id) {

    private var textWatcher: TextWatcher? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            textInputEditText.apply {
                setText(filename)
                textWatcher = onTextChanged { text?.toString()?.let { onNameChange(it) } }
                isEnabled = editable
            }

            btnDelete.setOnClickListener { onRemoveFileClick() }
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder) {
        viewHolder.apply {
            textInputEditText.apply {
                textWatcher?.let { removeTextChangedListener(it) }
            }
        }
        super.unbind(viewHolder)
    }

    override fun getLayout() = R.layout.item_event_registration_file

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationFileItem) return false

        if (filename != other.filename) return false

        return true
    }
}