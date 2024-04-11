package com.example.ui.views

import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.widget.LinearLayout
import com.example.R
import com.google.android.material.textfield.TextInputEditText
import com.hbb20.CountryCodePicker
import kotlinx.android.synthetic.main.phone_view.view.*
import com.example.extensions.onTextChanged

class PhoneView(context: Context, attrs: AttributeSet): LinearLayout(context, attrs) {

    private var ccp: CountryCodePicker
    private var isPhoneValidd = false
    private var isPhoneValid: (education: Boolean) -> Unit = {}
    private var textChanged: (phone: String) -> Unit = {}
    private var textChangedWithoutPlus: (phone: String) -> Unit = {}

    init {
        inflate(context, R.layout.phone_view, this)
        ccp = findViewById(R.id.ccp)
        ccp.registerCarrierNumberEditText(etCountryCodePhone)
        ccp.changeDefaultLanguage(CountryCodePicker.Language.RUSSIAN)
        ccp.setPhoneNumberValidityChangeListener {
            isPhoneValid(it)
            isPhoneValidd = it
        }
        etCountryCodePhone.apply {
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
        tilCountryCodePhone.apply {
            error = if (show) context.resources.getString(R.string.invalid_phone_number_second_error) else null
        }
    }

    fun showEmptyError(show: Boolean){
        tilCountryCodePhone.apply {
            error = if (show) context.resources.getString(R.string.invalid_phone_number_error) else null
            requestFocus()
        }
    }

    fun showErrorWithFocus(invalidNumberError: String) {
        tilCountryCodePhone.apply {
            error = invalidNumberError
            requestFocus()
        }
    }

    fun getError(): CharSequence? = tilCountryCodePhone.error

    fun getFullNumber(): String = ccp.fullNumber

    fun getFullNumberWithPlus(): String = ccp.fullNumberWithPlus

    fun getNumberWithoutCode(): String = etCountryCodePhone.text.toString()

    fun getIsValid(): Boolean = isPhoneValidd

    fun getEditTextLayout() : TextInputEditText = etCountryCodePhone

    fun setCursorPosition(){
        etCountryCodePhone.apply {
            val text = etCountryCodePhone.text
            if (!text.toString().isNullOrEmpty()) setSelection(text.toString().length)
            else setSelection(0)
        }
    }
}