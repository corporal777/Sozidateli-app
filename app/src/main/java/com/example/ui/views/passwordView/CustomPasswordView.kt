package com.example.ui.views.passwordView

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.R
import com.example.databinding.ViewPasswordCustomBinding
import com.example.extensions.getSymbols
import com.example.ui.views.CustomSpannableString
import com.example.util.getColor
import com.example.util.getDrawable
import onFocusChanged
import onTextChanged

class CustomPasswordView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var binding =
        ViewPasswordCustomBinding.inflate(LayoutInflater.from(context), this, true)


    private val defaultTypeFace = ResourcesCompat.getFont(context, R.font.sf_pro_display)
    private val boldTypeFace = ResourcesCompat.getFont(context, R.font.sf_pro_display_semibold)
    private val symbols = getSymbols()
    private var firstPassword: CharSequence? = null
    private var secondPassword: CharSequence? = null

    private var isPasswordLengthValid = false
    private var isPasswordSymbolsValid = false
    private var isPasswordLettersValid = false
    private var isUncaughtSymbolsUsed = false

    private var isFirstPasswordValid = false
    private var isSecondPasswordValid = false

    private var onPasswordValid: (password: PasswordModel) -> Unit = {}

    init {
        binding.apply {
            toggleOne.apply {
                isVisible = !firstPassword.isNullOrEmpty()
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!isChecked) etPasswordOne.transformationMethod =
                        PasswordTransformationMethod()
                    else etPasswordOne.transformationMethod = null
                    etPasswordOne.setSelection(etPasswordOne.length());
                }
            }
            toggleTwo.apply {
                isVisible = !secondPassword.isNullOrEmpty()
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!isChecked) etPasswordTwo.transformationMethod =
                        PasswordTransformationMethod()
                    else etPasswordTwo.transformationMethod = null
                    etPasswordTwo.setSelection(etPasswordTwo.length());
                }
            }
        }
        initPasswordField()
        validatePassword(false, firstPassword.toString())
    }


    private fun initPasswordField() {
        binding.apply {
            etPasswordOne.apply {
                onTextChanged { showFirstPasswordError(false) }
                onFocusChanged { hasFocus ->
                    tilPasswordOne.changeBackground(hasFocus)
                    if (hasFocus) validatePassword(false, firstPassword)
                    if (!hasFocus) {
                        showFirstPasswordError(isUncaughtSymbolsUsed)
                        showSecondPasswordError(!isFirstPasswordValid && !etPasswordTwo.hasFocus() && secondPassword.isNullOrEmpty())
                    }
                }
                doAfterTextChanged {
                    firstPassword = it.toString()
                    validatePassword(false, firstPassword)
                    toggleOne.isVisible = !firstPassword.isNullOrEmpty()
                }
            }
            etPasswordTwo.apply {
                onTextChanged { showSecondPasswordError(false) }
                onFocusChanged { hasFocus ->
                    tilPasswordTwo.changeBackground(hasFocus)
                    if (hasFocus) matchPasswords(secondPassword)
                }
                doAfterTextChanged {
                    secondPassword = it.toString()
                    matchPasswords(secondPassword)
                    toggleTwo.isVisible = !secondPassword.isNullOrEmpty()
                }
            }
        }

    }


    private fun validatePassword(isRegister: Boolean, password: CharSequence?) {
        val isLengthValid = (password?.length ?: 0) >= MIN_LENGTH
        val isLettersValid = password?.matches(Regex(".*[A-Z].*")) == true
        val isNumbersValid = password?.matches(Regex(".*\\d.*")) == true
        val isUncaughtSymbolsUsed = password?.contains(Regex("[$symbols]")) == true

        binding.apply {
            tvErrorLength.changeTextColorError(isRegister, isLengthValid)
            tvErrorLetters.changeTextColorError(isRegister, isLettersValid)
            tvErrorNumbers.changeTextColorError(isRegister, isNumbersValid)
            lnErrorDescription.isVisible = !isUncaughtSymbolsUsed
        }
        binding.tvErrorUncaughtSymbols.apply {
            text = context.getString(R.string.used_unacceptable_symbols)
            isVisible = isUncaughtSymbolsUsed
        }

        isFirstPasswordValid = !isUncaughtSymbolsUsed && isLengthValid && isLettersValid
                && isNumbersValid && firstPassword == secondPassword
        isSecondPasswordValid = isFirstPasswordValid && firstPassword == secondPassword
        onPasswordValid(getPasswordModel())
    }

    private fun matchPasswords(password: CharSequence?) {
        if (password != firstPassword) {
            binding.lnErrorDescription.isVisible = false
            binding.tvErrorUncaughtSymbols.apply {
                text = context.getString(R.string.passwords_do_not_match)
                isVisible = true
            }
        } else validatePassword(false, firstPassword)

        isSecondPasswordValid = isFirstPasswordValid && password == firstPassword
        onPasswordValid(getPasswordModel())
    }


    private fun showFirstPasswordError(show: Boolean) {
        binding.apply {
            if (show) tvNew.setTextColor(getColor(R.color.title_text_error_red))
            else tvNew.setTextColor(getColor(R.color.chat_list_date))
        }
    }

    private fun showSecondPasswordError(show: Boolean) {
        binding.apply {
            if (show) tvConfirm.setTextColor(getColor(R.color.title_text_error_red))
            else tvConfirm.setTextColor(getColor(R.color.chat_list_date))
        }
    }

    fun showErrors(show: Boolean) {
        showFirstPasswordError(!isFirstPasswordValid)
        showSecondPasswordError(!isSecondPasswordValid)
        if (!isFirstPasswordValid) validatePassword(!firstPassword.isNullOrEmpty(), firstPassword)
        else matchPasswords(secondPassword)
    }

    private fun View.changeBackground(hasFocus: Boolean) {
        setBackgroundResource(
            if (hasFocus) R.drawable.background_custom_input_view_focused
            else R.drawable.background_custom_input_view_unfocused
        )
    }

    private fun TextView.changeTextColorError(isRegistered: Boolean, isValid: Boolean) {
        if (isValid) {
            setTextColor(context.getColor(R.color.password_errors_text_color_valid))
            typeface = boldTypeFace
        } else {
            setTextColor(context.getColor(R.color.password_errors_text_color))
            if (isRegistered) typeface = boldTypeFace
            else typeface = defaultTypeFace
        }
    }

    private fun getPasswordModel(): PasswordModel {
        return PasswordModel(
            isFirstPasswordValid &&
                    isSecondPasswordValid,
            firstPassword.toString()
        )
    }

    fun setPasswordValidCallback(block: (password: PasswordModel) -> Unit): CustomPasswordView {
        onPasswordValid = block
        return this
    }

    companion object {
        const val MIN_LENGTH = 8
    }
}