package com.example.holders.registerEvent

import android.text.InputType
import android.text.TextWatcher
import com.example.R
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_register_event_input.*
import onTextChanged

class RegisterEventStringItem(
        private val fieldData: EventRegisterFieldData<String>,
        private val editable: Boolean = true,
        onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem(fieldData, onDataChange) {

    private val textChangeListener: (CharSequence?) -> Unit = {
        fieldData.value = it?.toString()
        onDataChange()
    }

    private var textWatcher: TextWatcher? = null

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            textInputEditText.apply {
                isEnabled = editable
                when (field.type) {
                    EventRegisterField.Type.STRING ->
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    EventRegisterField.Type.TEXT_AREA -> {
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        minLines = 2
                        maxLines = 8
                    }
                    EventRegisterField.Type.NUMBER ->
                        inputType = InputType.TYPE_NUMBER_FLAG_SIGNED or InputType.TYPE_CLASS_NUMBER
                    else -> throw IllegalArgumentException("Wrong field type ${field.type} for RegisterEventStringItem")
                }

                hint = field.description
                setText(fieldData.value)
                textWatcher = onTextChanged(textChangeListener)
            }
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

    override fun getLayout() = R.layout.item_register_event_input
}