package com.example.holders.registerEvent

import android.view.View
import android.widget.TextView
import com.example.R
import com.example.data.models.EventRegisterFieldData
import com.example.databinding.ItemRegisterEventPhoneBinding
import com.example.util.AuthValidateUtil
import com.example.util.getColorStateList

class RegisterEventPhoneItem (
    private val fieldData: EventRegisterFieldData<String>,
    onDataChange: (fieldData: EventRegisterFieldData<*>) -> Unit
) : BaseRegisterInputItem<ItemRegisterEventPhoneBinding>(fieldData, onDataChange) {


    override fun bind(viewBinding: ItemRegisterEventPhoneBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.phoneInputEditText.apply {
            setPhoneHint("+79123456789")
            setPhoneText(fieldData.value)
            onInputTextChanged {
                fieldData.value = it?.toString()
                onDataChange()
                showError(false)
            }
        }
    }

    private fun checkValueIsValid(fieldData: EventRegisterFieldData<String>) : Boolean {
        return if (fieldData.field.required){
            AuthValidateUtil.isValidPhone(fieldData.value ?: "")
        } else true
    }

    override fun getInputView(binding: ItemRegisterEventPhoneBinding): View = binding.phoneInputEditText
    override fun getErrorFrameView(binding: ItemRegisterEventPhoneBinding): View = binding.viewInputError
    override fun getTitleView(binding: ItemRegisterEventPhoneBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_phone

}