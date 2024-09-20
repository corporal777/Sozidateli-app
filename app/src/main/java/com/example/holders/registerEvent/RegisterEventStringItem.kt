package com.example.holders.registerEvent

import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.example.databinding.ItemRegisterEventInputBinding
import com.xwray.groupie.databinding.GroupieViewHolder
import com.example.extensions.onTextChanged
import com.example.util.getColorStateList

class RegisterEventStringItem(
    private val fieldData: EventRegisterFieldData<String>,
     onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterInputItem<ItemRegisterEventInputBinding>(fieldData, onDataChange) {

    private var textWatcher: TextWatcher? = null


    override fun bind(viewBinding: ItemRegisterEventInputBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            textInputEditText.apply {
                when (field.type) {
                    EventRegisterField.Type.STRING, EventRegisterField.Type.GROUP -> {
                        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                        minLines = 1
                        maxLines = 1
                    }
                    EventRegisterField.Type.TEXT_AREA -> {
                        inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        minLines = 2
                        maxLines = 8
                    }
                    EventRegisterField.Type.NUMBER -> {
                        inputType = InputType.TYPE_CLASS_NUMBER
                        minLines = 1
                        maxLines = 1
                    }
                    EventRegisterField.Type.EMAIL -> {
                        inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                        minLines = 1
                        maxLines = 1
                    }
                    EventRegisterField.Type.SITE -> {
                        inputType = InputType.TYPE_TEXT_VARIATION_WEB_EMAIL_ADDRESS
                        minLines = 1
                        maxLines = 1
                    }
                    else -> throw IllegalArgumentException("Wrong field type ${field.type} for RegisterEventStringItem")
                }

                setHint(field.description)
                setText(fieldData.value)
                textWatcher = onTextChanged {
                    fieldData.value = it?.toString()
                    onDataChange()
                    showError(false)
                }

            }
        }
    }


    override fun unbind(viewHolder: GroupieViewHolder<ItemRegisterEventInputBinding>) {
        viewHolder.binding.apply {
            textInputEditText.apply {
                textWatcher?.let { removeTextChangedListener(it) }
            }
        }
        super.unbind(viewHolder)
    }


    override fun getInputView(binding: ItemRegisterEventInputBinding): View = binding.textInputEditText
    override fun getErrorFrameView(binding: ItemRegisterEventInputBinding): View = binding.viewInputError
    override fun getTitleView(binding: ItemRegisterEventInputBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_input
}