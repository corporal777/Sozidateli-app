package com.example.holders.registerEvent

import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
import android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
import android.text.InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
import android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
import android.text.TextWatcher
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventInputBinding
import com.example.data.models.EventFormFieldModel
import com.example.data.models.eventRegister.EventRegisterField
import com.example.extensions.onTextChanged
import com.example.extensions.setMaxLength
import com.example.extensions.setMinMaxLines
import com.example.util.AuthValidateUtil
import com.xwray.groupie.viewbinding.GroupieViewHolder

class RegisterEventStringItem(
    private val fieldData: EventRegisterField<String>,
    onDataChange: (fieldData: EventRegisterField<*>) -> Unit
) : BaseRegisterInputItem<ItemRegisterEventInputBinding>(fieldData, onDataChange) {

    private var textWatcher: TextWatcher? = null
    private var maxSymbolsLength = 100

    override fun bind(viewBinding: ItemRegisterEventInputBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.apply {
            textInputEditText.apply {
                when (field.type) {
                    EventFormFieldModel.Type.STRING, EventFormFieldModel.Type.GROUP -> {
                        maxSymbolsLength = 200
                        setMinMaxLines(1, 1)
                        inputType = TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_CAP_SENTENCES or TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    }
                    EventFormFieldModel.Type.TEXT_AREA -> {
                        maxSymbolsLength = 500
                        setMinMaxLines(2, 8)
                        inputType = TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_MULTI_LINE or TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    }
                    EventFormFieldModel.Type.NUMBER -> {
                        maxSymbolsLength = 100
                        setMinMaxLines(1, 1)
                        inputType = TYPE_CLASS_NUMBER or TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    }

                    EventFormFieldModel.Type.EMAIL, EventFormFieldModel.Type.SITE -> {
                        maxSymbolsLength = 100
                        setMinMaxLines(1, 1)
                        inputType = TYPE_TEXT_VARIATION_EMAIL_ADDRESS or TYPE_TEXT_FLAG_NO_SUGGESTIONS
                    }
                    else -> throw IllegalArgumentException("Wrong field type ${field.type} for RegisterEventStringItem")
                }


                setMaxLength(maxSymbolsLength)
                setHint(field.description)
                setText(fieldData.value)

                textWatcher = onTextChanged {
                    fieldData.value = it?.toString()
                    onDataChange()

                    checkFieldIsValid(fieldData.value)
                    setSymbolsLeftVisible(fieldData.value, textViewSymbolsLeft)
                }

            }
        }
    }

    override fun unbind(viewHolder: GroupieViewHolder<ItemRegisterEventInputBinding>) {
        viewHolder.binding.textInputEditText.apply {
            textWatcher?.let { removeTextChangedListener(it) }
        }
        super.unbind(viewHolder)
    }

    private fun checkFieldIsValid(value: String?) {
        if (value.isNullOrEmpty()) showError(false)
        else {
            when (field.type) {
                EventFormFieldModel.Type.EMAIL -> {
                    showErrorText(!AuthValidateUtil.isValidEmail(value), "Неверный формат e-mail")
                }
                EventFormFieldModel.Type.SITE -> showError(!AuthValidateUtil.isValidSite(value))
                else -> showError(false)
            }
        }
    }

    private fun setSymbolsLeftVisible(text : CharSequence?, textView: TextView) {
        textView.apply {
            isVisible = when (field.type) {
                EventFormFieldModel.Type.STRING ->
                    !text.isNullOrEmpty() && text.length > 149 && text.length < maxSymbolsLength
                EventFormFieldModel.Type.TEXT_AREA ->
                    !text.isNullOrEmpty() && text.length > 374 && text.length < maxSymbolsLength
                EventFormFieldModel.Type.NUMBER ->
                    !text.isNullOrEmpty() && text.length > 74 && text.length < maxSymbolsLength
                else -> false
            }
        }
        if (textView.isVisible && !text.isNullOrEmpty())
            textView.text = "Осталось " + (maxSymbolsLength - text.length) + " символов"
    }

    override fun getInputView(binding: ItemRegisterEventInputBinding): View = binding.textInputEditText
    override fun getErrorFrameView(binding: ItemRegisterEventInputBinding): View = binding.viewInputError
    override fun getTitleView(binding: ItemRegisterEventInputBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_input
    override fun initializeViewBinding(view: View) = ItemRegisterEventInputBinding.bind(view)
}