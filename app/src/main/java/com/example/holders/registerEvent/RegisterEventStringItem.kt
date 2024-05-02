package com.example.holders.registerEvent

import android.text.InputFilter
import android.text.InputType
import android.text.Spanned
import android.text.TextWatcher
import com.example.R
import com.example.data.models.EventRegisterField
import com.example.data.models.EventRegisterFieldData
import com.example.databinding.ItemRegisterEventInputBinding
import com.xwray.groupie.databinding.GroupieViewHolder
import com.example.extensions.onTextChanged

class RegisterEventStringItem(
    private val fieldData: EventRegisterFieldData<String>,
    private val editable: Boolean = true,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterItem<ItemRegisterEventInputBinding>(fieldData, onDataChange) {

    private val textChangeListener: (CharSequence?) -> Unit = {
        fieldData.value = it?.toString()
        onDataChange()
    }

    private var textWatcher: TextWatcher? = null

    override fun bind(viewBinding: ItemRegisterEventInputBinding, position: Int) {
        viewBinding.apply {
            textInputEditText.apply {
                isEnabled = editable
                when (field.type) {
                    EventRegisterField.Type.STRING, EventRegisterField.Type.GROUP -> {
                        inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
                    }
                    EventRegisterField.Type.TEXT_AREA -> {
                        inputType =
                            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES or InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        minLines = 2
                        maxLines = 8
                    }
                    EventRegisterField.Type.NUMBER -> {
                        inputType =
                            InputType.TYPE_CLASS_NUMBER//InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
                        fieldData.field.mask?.takeIf { it.isNotEmpty() }?.let {
                            filters = arrayOf(SpecialCharacterInputFilter(it))
                        }
                    }
                    else -> throw IllegalArgumentException("Wrong field type ${field.type} for RegisterEventStringItem")
                }

                hint = field.description
                setText(fieldData.value)
                textWatcher = onTextChanged(textChangeListener)
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

    private class SpecialCharacterInputFilter(pattern: String) : InputFilter {
        private val regex = pattern.toRegex()

        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
        ): CharSequence? {
            return if (source.toString() == "" || source.matches(regex)) {
                source
            } else {
                ""
            }
        }
    }

    override fun getLayout() = R.layout.item_register_event_input

}