package com.example.ui.views.passwordView

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.R
import com.example.databinding.ViewPasswordCustomBinding
import com.example.ui.views.CustomSpannableString
import com.example.util.getDrawable
import onFocusChanged
import onTextChanged

class CustomPasswordView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val symbols =
        "\\@\\#\\\$\\_\\&\\-\\+\\(\\)\\/\\*\\\"\\'\\:\\;\\!\\?\\,\\.\\~\\`\\|\\÷\\×\\^\\=\\{\\}\\%\\<\\>"
    private var firstPassword: CharSequence? = null
    private var secondPassword: CharSequence? = null

    private var binding =
        ViewPasswordCustomBinding.inflate(LayoutInflater.from(context), this, true)

    private var onPasswordValid: (password: PasswordModel) -> Unit = {}

    private var isPasswordLengthValid = false
    private var isPasswordSymbolsValid = false
    private var isPasswordLettersValid = false
    private var isUncaughtSymbolsUsed = false

    private var isFirstPasswordValid = false
    private var isSecondPasswordValid = false

    init {
        binding.apply {
            toggleOne.apply {
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!isChecked) etPasswordOne.transformationMethod = PasswordTransformationMethod()
                    else etPasswordOne.transformationMethod = null
                    etPasswordOne.setSelection(etPasswordOne.length());
                }
            }
            toggleTwo.apply {
                setOnCheckedChangeListener { buttonView, isChecked ->
                    if (!isChecked) etPasswordTwo.transformationMethod = PasswordTransformationMethod()
                    else etPasswordTwo.transformationMethod = null
                    etPasswordTwo.setSelection(etPasswordTwo.length());
                }
            }
        }
        initPasswordField()
        validatePassword(firstPassword.toString())
    }


    private fun initPasswordField() {
        binding.apply {
            etPasswordOne.apply {
                onTextChanged { showFirstPasswordError(false) }
                onFocusChanged { hasFocus ->
                    tilPasswordOne.changeBackground(hasFocus)
                    if (!hasFocus && !firstPassword.isNullOrEmpty() && !etPasswordTwo.hasFocus())
                        showSecondPasswordError(secondPassword.isNullOrEmpty())
                    if (hasFocus) validatePassword(firstPassword)
                }
                doAfterTextChanged {
                    firstPassword = it.toString()
                    validatePassword(firstPassword)
                }
            }
            etPasswordTwo.apply {
                onTextChanged { showSecondPasswordError(false) }
                onFocusChanged { hasFocus ->
                    tilPasswordTwo.changeBackground(hasFocus)
                }
                doAfterTextChanged {
                    secondPassword = it.toString()
                    matchPasswords(secondPassword)
                }
            }
        }

    }


    private fun validatePassword(password: CharSequence?) {
        isPasswordLengthValid = (password?.length ?: 0) >= MIN_LENGTH
        isPasswordLettersValid = password?.matches(Regex(".*[A-Z].*")) == true
        isPasswordSymbolsValid = password?.matches(Regex(".*\\d.*")) == true
        isUncaughtSymbolsUsed = password?.contains(Regex("[$symbols]")) == true

        if (isUncaughtSymbolsUsed) {
            binding.tvErrorDescription.text = addUncaughtSymbolsError()
            binding.tvErrors.text = ""
            showFirstPasswordError(true)
        } else {
            binding.tvErrorDescription.text = "Используйте сложный пароль. Он должен содержать:"
            binding.tvErrors.text = SpannableStringBuilder().apply {
                append(addPasswordLengthError())
                append("\n")
                append(addPasswordSymbolsError())
                append("\n")
                append(addPasswordLettersError())
            }
        }
        isFirstPasswordValid = !isUncaughtSymbolsUsed && isPasswordLettersValid && isPasswordSymbolsValid && isPasswordLengthValid
        onPasswordValid(PasswordModel(isFirstPasswordValid && isSecondPasswordValid, firstPassword.toString()))
    }

    private fun matchPasswords(password : CharSequence?) {
        if (isFirstPasswordValid){
            if (password != firstPassword){
                binding.tvErrorDescription.text = addNotMatchPasswordsError()
                binding.tvErrors.text = ""
            } else validatePassword(firstPassword)
        }
        isSecondPasswordValid = isFirstPasswordValid && password == firstPassword
        onPasswordValid(PasswordModel(isFirstPasswordValid && isSecondPasswordValid, firstPassword.toString()))
    }

    private fun addPasswordLengthError(): CustomSpannableString {
        return CustomSpannableString(context.getString(R.string.password_to_small_error)).apply {
            if (!isPasswordLengthValid) {
                setColorSpan(R.color.password_errors_text_color, context)
                setFontSpan("fonts/sf_pro_display.OTF", context)
            } else {
                setColorSpan(R.color.password_errors_text_color_valid, context)
                setFontSpan("fonts/sf_pro_display_semibold.ttf", context)
            }
        }
    }

    private fun addPasswordSymbolsError(): CustomSpannableString {
        return CustomSpannableString(context.getString(R.string.password_numbers_error)).apply {
            if (!isPasswordSymbolsValid) {
                setColorSpan(R.color.password_errors_text_color, context)
                setFontSpan("fonts/sf_pro_display.OTF", context)
            } else {
                setColorSpan(R.color.password_errors_text_color_valid, context)
                setFontSpan("fonts/sf_pro_display_semibold.ttf", context)
            }
        }
    }

    private fun addPasswordLettersError(): CustomSpannableString {
        return CustomSpannableString(context.getString(R.string.password_letters_error)).apply {
            if (!isPasswordLettersValid) {
                setColorSpan(R.color.password_errors_text_color, context)
                setFontSpan("fonts/sf_pro_display.OTF", context)
            } else {
                setColorSpan(R.color.password_errors_text_color_valid, context)
                setFontSpan("fonts/sf_pro_display_semibold.ttf", context)
            }
        }
    }

    private fun addUncaughtSymbolsError(): CharSequence {
        return CustomSpannableString(context.getString(R.string.used_unacceptable_symbols)).apply {
            setColorSpan(R.color.user_profile_delete_profile_text, context)
        }
    }

    private fun addNotMatchPasswordsError(): CharSequence {
        return CustomSpannableString(context.getString(R.string.passwords_do_not_match)).apply {
            setColorSpan(R.color.user_profile_delete_profile_text, context)
        }
    }

    private fun showFirstPasswordError(show: Boolean) {
        binding.apply {
            if (show) {
                tvNew.setTextColor(ContextCompat.getColor(context, R.color.red_new))
                toggleOne.buttonDrawable = getDrawable(R.drawable.ic_input_error_icon)
                toggleOne.isEnabled = false
            } else {
                tvNew.setTextColor(ContextCompat.getColor(context, R.color.chat_list_date))
                toggleOne.isEnabled = true
                toggleOne.buttonDrawable = getDrawable(R.drawable.drawable_password_toggle)
            }
        }
    }

    private fun showSecondPasswordError(show: Boolean) {
        binding.apply {
            if (show) {
                tvConfirm.setTextColor(ContextCompat.getColor(context, R.color.red_new))
                toggleTwo.buttonDrawable = getDrawable(R.drawable.ic_input_error_icon)
                toggleTwo.isEnabled = false
            } else {
                tvConfirm.setTextColor(ContextCompat.getColor(context, R.color.chat_list_date))
                toggleTwo.isEnabled = true
                toggleTwo.buttonDrawable = getDrawable(R.drawable.drawable_password_toggle)
            }
        }
    }

    private fun View.changeBackground(hasFocus : Boolean){
        setBackgroundResource(
            if (hasFocus) R.drawable.background_custom_input_view_focused
            else R.drawable.background_custom_input_view_unfocused
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