package com.example.ui.views.passwordView

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.app.R
import com.example.app.databinding.ViewPasswordFieldsBinding
import com.example.util.ClickableSpanNew
import java.util.regex.Pattern

class PasswordFieldsView : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var onPasswordValid: (password: PasswordModel) -> Unit = {}
    private val pattern = Pattern.compile("[a-zA-z0-9]*")
    private val symbolsPattern = Pattern.compile("[^_\\W]+")

    private val mBinding =
        ViewPasswordFieldsBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        mBinding.apply {
            etPassword.doAfterTextChanged {
                if (it.toString().isNullOrEmpty()) setViewsVisibility(false)
                else {
                    setViewsVisibility(true)
                    validatePassword(it.toString())
                }
            }
            etPasswordConfirm.doAfterTextChanged {
                validatePassword(etPassword.text.toString())
            }
        }
    }

    private fun setViewsVisibility(isVisible: Boolean) {
        mBinding.apply {
            tvResult.isVisible = isVisible
            tvErrors.isVisible = isVisible
            first.isVisible = isVisible
            second.isVisible = isVisible
            third.isVisible = isVisible
        }
    }

    private fun matchPasswords(isMatch: Boolean, isValid: Boolean) {
        mBinding.apply {
            if (isValid) {
                tvNotMatch.isVisible = !isMatch
                first.isVisible = isMatch
                second.isVisible = isMatch
                third.isVisible = isMatch
                tvResult.isVisible = isMatch
                tvErrors.isVisible = isMatch
            } else {
                tvNotMatch.isVisible = false
                first.isVisible = true
                second.isVisible = true
                third.isVisible = true
                tvResult.isVisible = true
                tvErrors.isVisible = true
            }
        }
    }


    private fun validatePassword(password: String?) {
        var isValid = true
        var usedUnacceptableSymbols = false
        var levelCounter = 3
        val errors = mutableListOf<String>()
        if ((password?.length ?: 0) >= PASSWORD_MIN_LENGTH) levelCounter -= 1
        else {
            isValid = false
            errors.add(resources.getString(R.string.password_to_small))
        }

        if (password?.matches(Regex(".*\\d.*")) == true) levelCounter -= 1
        else {
            isValid = false
            errors.add(resources.getString(R.string.password_numbers))
        }

        if (password?.matches(Regex(".*[A-Z].*")) == true && password.matches(Regex(".*[a-z].*"))) levelCounter -= 1
        else {
            isValid = false
            when {
                password?.matches(Regex(".*[A-Z].*")) == true -> {
                    errors.add(resources.getString(R.string.password_letters))
                }
                password?.matches(Regex(".*[a-z].*")) == true -> {
                    errors.add(resources.getString(R.string.password_lowercase_letters))
                }
                else -> errors.add(resources.getString(R.string.password_uppercase_letters))
            }
        }

        usedUnacceptableSymbols =
            if (!symbolsPattern.matcher(password).matches()) true
            else !pattern.matcher(password).matches()


        mBinding.apply {
            if (!usedUnacceptableSymbols) {
                if (!isValid) {
                    first.setBackgroundResource(R.drawable.password_red)
                    second.setBackgroundResource(R.drawable.password_gray)
                    third.setBackgroundResource(R.drawable.password_gray)
                    tvResult.text = resources.getString(R.string.password_invalid)
                    tvErrors.text = errors.joinToString(";\n", postfix = ".")
                } else if (isValid && etPasswordConfirm.text.toString()
                        .isEmpty() && (etPassword.text?.length ?: 0) < 9
                ) {
                    first.setBackgroundResource(R.drawable.password_yellow)
                    second.setBackgroundResource(R.drawable.password_yellow)
                    third.setBackgroundResource(R.drawable.password_gray)
                    tvResult.text = resources.getString(R.string.password_valid_but_low)
                    tvErrors.text = ""
                } else {
                    first.setBackgroundResource(R.drawable.password_green)
                    second.setBackgroundResource(R.drawable.password_green)
                    third.setBackgroundResource(R.drawable.password_green)
                    tvResult.text = resources.getString(R.string.password_valid)
                    tvErrors.text = ""
                }
                if (etPasswordConfirm.text.toString().isEmpty()) matchPasswords(true, isValid)
                else matchPasswords(
                    etPassword.text.toString() == etPasswordConfirm.text.toString(),
                    isValid
                )
                onPasswordValid(
                    PasswordModel(
                        isValid && etPasswordConfirm.text.toString()
                            .isNotEmpty() && etPassword.text.toString() == etPasswordConfirm.text.toString(),
                        password
                    )
                )
            } else {
                first.setBackgroundResource(R.drawable.password_red)
                second.setBackgroundResource(R.drawable.password_gray)
                third.setBackgroundResource(R.drawable.password_gray)
                tvResult.text = resources.getString(R.string.used_unacceptable_symbols)
                tvErrors.text = ""
                matchPasswords(false, false)
                onPasswordValid(PasswordModel(false, password))
            }
        }

    }

    fun setPasswordValidCallback(block: (password: PasswordModel) -> Unit): PasswordFieldsView {
        onPasswordValid = block
        return this
    }

    companion object {
        const val PASSWORD_MIN_LENGTH = 8
    }
}