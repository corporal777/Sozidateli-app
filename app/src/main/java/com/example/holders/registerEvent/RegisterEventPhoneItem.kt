package com.example.holders.registerEvent

import android.view.View
import android.widget.TextView
import com.example.app.R
import com.example.app.databinding.ItemRegisterEventPhoneBinding
import com.example.data.models.eventRegister.EventRegisterField
import com.example.util.AuthValidateUtil

class RegisterEventPhoneItem(
    private val fieldData: EventRegisterField<String>,
    onDataChange: (fieldData: EventRegisterField<*>) -> Unit
) : BaseRegisterInputItem<ItemRegisterEventPhoneBinding>(fieldData, onDataChange) {


    override fun bind(viewBinding: ItemRegisterEventPhoneBinding, position: Int) {
        super.bind(viewBinding, position)
        viewBinding.phoneInputEditText.apply {
            setPhoneText(fieldData.value)

            onInputTextChanged {
                fieldData.value = it?.toString()
                onDataChange()
                checkPhoneIsValid(fieldData.value)
            }
        }
    }

    private fun checkPhoneIsValid(field: String?) {
        if (field.isNullOrEmpty() || field == "+7") showError(false)
        else {
            val isValid = AuthValidateUtil.isValidPhone(field)
            showError(!isValid)
        }
    }

    override fun getInputView(binding: ItemRegisterEventPhoneBinding): View =
        binding.phoneInputEditText

    override fun getErrorFrameView(binding: ItemRegisterEventPhoneBinding): View =
        binding.viewInputError

    override fun getTitleView(binding: ItemRegisterEventPhoneBinding): TextView = binding.textView
    override fun getLayout() = R.layout.item_register_event_phone
    override fun initializeViewBinding(view: View) = ItemRegisterEventPhoneBinding.bind(view)

}