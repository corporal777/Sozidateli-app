package com.example.holders.registerEvent

import android.text.TextWatcher
import com.example.R
import com.example.databinding.ItemEventRegistrationFileBinding
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder
import com.example.extensions.onTextChanged

open class EventRegistrationFileItem(
    id: Long,
    private val filename: String,
    private val editable: Boolean,
    private val onRemoveFileClick: () -> Unit,
    private val onNameChange: (String) -> Unit
) : BindableItem<ItemEventRegistrationFileBinding>(id) {

    private var textWatcher: TextWatcher? = null

    override fun bind(viewBinding: ItemEventRegistrationFileBinding, position: Int) {
        viewBinding.apply {
            textInputEditText.apply {
                setText(filename)
                textWatcher = onTextChanged {
                    text?.toString()?.let {
                        onNameChange(it)
                    }
                }
                isEnabled = editable
            }

            btnDelete.setOnClickListener { onRemoveFileClick() }
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder<ItemEventRegistrationFileBinding>) {
        viewHolder.binding.apply {
            textInputEditText.apply {
                textWatcher?.let { removeTextChangedListener(it) }
            }
        }
        super.unbind(viewHolder)
    }


    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (this === other) return true
        if (other !is EventRegistrationFileItem) return false
        if (filename != other.filename) return false
        return true
    }

    override fun getLayout() = R.layout.item_event_registration_file
}