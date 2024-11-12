package com.example.ui.views.passwordView

import android.content.Context
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.app.R
import com.example.app.databinding.ViewPasswordCustomBinding
import com.example.extensions.getSymbols
import com.example.util.getColor
import com.example.extensions.onFocusChanged
import com.example.extensions.onTextChanged
import java.nio.charset.Charset

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
                    if (hasFocus) matchPasswords(false, secondPassword)
                }
                doAfterTextChanged {
                    secondPassword = it.toString()
                    matchPasswords(false, secondPassword)
                    toggleTwo.isVisible = !secondPassword.isNullOrEmpty()
                }
            }
        }

    }

    private fun validatePassword(isRegister: Boolean, password: CharSequence?) {
        val isLengthValid = (password?.length ?: 0) >= MIN_LENGTH
        val isLettersValid = password?.matches(Regex(".*[A-Z].*")) == true
        val isNumbersValid = password?.matches(Regex(".*\\d.*")) == true

        isUncaughtSymbolsUsed = if (password.isNullOrEmpty()) false
        else if (password.contains(Regex("[$symbols]"))) true
        else if (!Charset.forName("US-ASCII").newEncoder().canEncode(password)) true
        else false


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

        isFirstPasswordValid = !isUncaughtSymbolsUsed && isLengthValid && isLettersValid && isNumbersValid
        isSecondPasswordValid = isFirstPasswordValid && firstPassword == secondPassword
        onPasswordValid(getPasswordModel())
    }

    private fun matchPasswords(isRegister: Boolean, password: CharSequence?) {
        if (!password.isNullOrEmpty() && password != firstPassword) {
            binding.lnErrorDescription.isVisible = false
            binding.tvErrorUncaughtSymbols.apply {
                text = context.getString(R.string.passwords_do_not_match)
                isVisible = true
            }
        } else validatePassword(false, firstPassword)

        isSecondPasswordValid = isFirstPasswordValid && password == firstPassword
        onPasswordValid(getPasswordModel())
    }


    fun showErrors(show: Boolean) {
        showFirstPasswordError(!isFirstPasswordValid)
        showSecondPasswordError(!isSecondPasswordValid)

        if (!isFirstPasswordValid) validatePassword(firstPassword.isNullOrEmpty(), firstPassword)
        else matchPasswords(true, secondPassword)
    }

    private fun View.changeBackground(hasFocus: Boolean) {
        setBackgroundResource(
            if (hasFocus) R.drawable.background_custom_input_view_focused
            else R.drawable.background_custom_input_view_unfocused
        )
    }

    private fun TextView.changeTextColorError(isRegistered: Boolean, isValid: Boolean) {
        if (isValid) {
            setTextColor(getColor(R.color.password_errors_text_color_valid))
            typeface = boldTypeFace
        } else {
            setTextColor(getColor(R.color.password_errors_text_color))
            typeface = if (isRegistered) boldTypeFace else defaultTypeFace
        }
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

    private fun getPasswordModel(): PasswordModel {
        return PasswordModel(
            isFirstPasswordValid && isSecondPasswordValid,
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