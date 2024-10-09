package com.example.ui.views

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.example.app.R
import com.example.app.databinding.PhoneViewBinding
import com.example.extensions.onTextChanged
import com.google.android.material.textfield.TextInputEditText
import com.hbb20.CountryCodePicker

class PhoneView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    private var ccp: CountryCodePicker
    private var isPhoneValidd = false
    private var isPhoneValid: (education: Boolean) -> Unit = {}
    private var textChanged: (phone: String) -> Unit = {}
    private var textChangedWithoutPlus: (phone: String) -> Unit = {}

    val view = PhoneViewBinding.inflate(LayoutInflater.from(context), this, false)

    init {

        ccp = findViewById(R.id.ccp)
        ccp.registerCarrierNumberEditText(view.etCountryCodePhone)
        ccp.changeDefaultLanguage(CountryCodePicker.Language.RUSSIAN)
        ccp.setPhoneNumberValidityChangeListener {
            isPhoneValid(it)
            isPhoneValidd = it
        }
        view.etCountryCodePhone.apply {
            filters = arrayOf(
                InputFilter { source, _, _, _, _, _ ->
                    source.toString().filterIndexed { index, it -> it.isDigit() }
                })
            onTextChanged {
                textChanged(getFullNumberWithPlus())
                textChangedWithoutPlus(it.toString())
            }
            /*setOnFocusChangeListener { view, b ->
                if (b)
                    ccp.setBackgroundResource(R.drawable.background_phone_code_selected)
                else
                    ccp.setBackgroundResource(R.drawable.background_phone_code)
            }*/
        }
    }

    fun getPhoneValidCallback(block: (education: Boolean) -> Unit): PhoneView {
        isPhoneValid = block
        return this
    }

    fun getPhoneCallback(block: (phone: String) -> Unit): PhoneView {
        textChanged = block
        return this
    }

    fun getPhoneCallbackWithoutPlus(block: (phone: String) -> Unit): PhoneView {
        textChangedWithoutPlus = block
        return this
    }

    fun setPhone(phone: String) {
        ccp.fullNumber = phone.replace("+", "")
        if (phone.contains("+7"))
            ccp.setCountryForNameCode("ru")
    }

    fun showError(show: Boolean) {
        view.tilCountryCodePhone.apply {
            error = if (show) context.resources.getString(R.string.invalid_phone_number_second_error) else null
        }
    }

    fun showEmptyError(show: Boolean){
        view.tilCountryCodePhone.apply {
            error = if (show) context.resources.getString(R.string.invalid_phone_number_error) else null
            requestFocus()
        }
    }

    fun showErrorWithFocus(invalidNumberError: String) {
        view.tilCountryCodePhone.apply {
            error = invalidNumberError
            requestFocus()
        }
    }

    fun getError(): CharSequence? = view.tilCountryCodePhone.error

    fun getFullNumber(): String = ccp.fullNumber

    fun getFullNumberWithPlus(): String = ccp.fullNumberWithPlus

    fun getNumberWithoutCode(): String = view.etCountryCodePhone.text.toString()

    fun getIsValid(): Boolean = isPhoneValidd

    fun getEditTextLayout() : TextInputEditText = view.etCountryCodePhone

    fun setCursorPosition(){
        view.etCountryCodePhone.apply {
            val text = view.etCountryCodePhone.text
            if (!text.toString().isNullOrEmpty()) setSelection(text.toString().length)
            else setSelection(0)
        }
    }
}