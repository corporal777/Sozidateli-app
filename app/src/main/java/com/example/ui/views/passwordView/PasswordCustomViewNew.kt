package com.example.ui.views.passwordView

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.example.app.R
import com.example.util.ClickableSpan
import com.google.android.material.textfield.TextInputEditText
import java.util.regex.Pattern

class PasswordCustomViewNew: FrameLayout {

    private var isShowAgree = false
    private var onClickAgreeHyperlink: () -> Unit = {}
    private var onAgreeChangedSelection: (isChecked: Boolean) -> Unit = {}
    private var onPasswordValid: (password: PasswordModel) -> Unit = {}

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    val pattern = Pattern.compile("[a-zA-z0-9]*")
    val symbolsPattern = Pattern.compile("[^_\\W]+")

    //val pattern = Pattern.compile("[a-zA-z0-9]*^[^-_\"\'\\s.:;?/,#$%!@^<>*&+=(){}]*\$")
    var etPassword: TextInputEditText
    var etPasswordConfirm: TextInputEditText
    var tvResult: TextView
    var tvErrors: TextView
    var first: View
    var second: View
    var third: View
    var llAgree: LinearLayout
    var tvAgree: TextView
    var flAgree: FrameLayout
    var cbAgree: AppCompatCheckBox
    var tvNotMatch: TextView

    private var view: View = LayoutInflater.from(context).inflate(R.layout.view_password_new, this, true)

    init {
        etPassword = view.findViewById(R.id.etPassword)
        etPasswordConfirm = view.findViewById(R.id.etPasswordConfirm)
        tvResult = view.findViewById(R.id.tv_result)
        tvErrors = view.findViewById(R.id.tv_errors)
        first = view.findViewById(R.id.first)
        second = view.findViewById(R.id.second)
        third = view.findViewById(R.id.third)
        llAgree = view.findViewById(R.id.llAgree)
        tvAgree = view.findViewById(R.id.tvAgree)
        flAgree = view.findViewById(R.id.flAgree)
        cbAgree = view.findViewById(R.id.cbAgree)
        tvNotMatch = view.findViewById(R.id.tv_not_match)
        initPasswordField()
        setAgreeText()
    }

    private fun initPasswordField() {
        etPassword.doAfterTextChanged {
            if (it.toString().isEmpty()) setViewsVisibility(false)
            else {
                setViewsVisibility(true)
                validatePassword(it.toString())
            }
        }
        etPasswordConfirm.doAfterTextChanged {
            validatePassword(etPassword.text.toString())
        }
    }

    private fun setViewsVisibility(isVisible: Boolean) {
        tvResult.isVisible = isVisible
        tvErrors.isVisible = isVisible
        first.isVisible = isVisible
        second.isVisible = isVisible
        third.isVisible = isVisible
    }

    private fun matchPasswords(isMatch: Boolean, isValid: Boolean) {
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

    private fun setAgreeText() {
        val agreementText = SpannableString(context.resources.getString(R.string.auth_agree_user_agreement)).apply {
            val linkStart = 11
            val linkEnd = length
            setSpan(ClickableSpan(drawUnderline = false) {
                onClickAgreeHyperlink()
            }, linkStart, linkEnd, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
        }

        tvAgree.apply {
            text = agreementText
            movementMethod = LinkMovementMethod.getInstance()
        }

        flAgree.setOnClickListener {
            cbAgree.apply {
                isChecked = !isChecked
            }
        }

        cbAgree.setOnCheckedChangeListener { _, isChecked -> onAgreeChangedSelection(isChecked) }
    }

    private fun validatePassword(password: String?) {
        var isValid = true
        var usedUnacceptableSymbols = false
        var levelCounter = 3
        val errors = mutableListOf<String>()
        if ((password?.length ?: 0) >= PasswordCustomView.PASSWORD_MIN_LENGTH) levelCounter -= 1
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
                else -> {
                    errors.add(resources.getString(R.string.password_uppercase_letters))
                }
            }
        }

        //usedUnacceptableSymbols = pattern.matcher(password).matches() != true
        //usedUnacceptableSymbols = !pattern.matcher(password).matches()
        usedUnacceptableSymbols = if (!symbolsPattern.matcher(password).matches()){
            true
        }else {
            !pattern.matcher(password).matches()
        }


        if (!usedUnacceptableSymbols) {
            if (!isValid) {
                first.setBackgroundResource(R.drawable.password_red)
                second.setBackgroundResource(R.drawable.password_gray)
                third.setBackgroundResource(R.drawable.password_gray)
                tvResult.text = resources.getString(R.string.password_invalid)
                //tvResult.text = resources.getString(R.string.password_invalid)
                tvErrors.text = errors.joinToString(";\n", postfix = ".")
            } else if (isValid && etPasswordConfirm.text.toString().isEmpty() && (etPassword.text?.length?: 0) < 9) {
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
            hideShowAgree(isValid)
            if (etPasswordConfirm.text.toString().isEmpty()) matchPasswords(true, isValid)
            else matchPasswords(etPassword.text.toString() == etPasswordConfirm.text.toString(), isValid)
            onPasswordValid(PasswordModel(isValid && etPasswordConfirm.text.toString().isNotEmpty() && etPassword.text.toString() == etPasswordConfirm.text.toString(), password))
        } else {
            first.setBackgroundResource(R.drawable.password_red)
            second.setBackgroundResource(R.drawable.password_gray)
            third.setBackgroundResource(R.drawable.password_gray)
            tvResult.text = resources.getString(R.string.used_unacceptable_symbols)
            tvErrors.text = ""
            hideShowAgree(false)
            matchPasswords(false, false)
            onPasswordValid(PasswordModel(false, password))
        }
    }

    private fun hideShowAgree(passwordValid: Boolean) {
        if (isShowAgree) llAgree.isVisible = passwordValid
        else llAgree.isVisible = false
    }

    fun setShowAgree(isShow: Boolean) {
        isShowAgree = isShow
    }

    fun setAgreeSelection(isChecked: Boolean) {
        cbAgree.isChecked = isChecked
    }

    fun setHyperlinkClickCallback(block: () -> Unit): PasswordCustomViewNew {
        onClickAgreeHyperlink = block
        return this
    }

    fun setChangedSelectionCallback(block: (isChecked: Boolean) -> Unit): PasswordCustomViewNew {
        onAgreeChangedSelection = block
        return this
    }

    fun setPasswordValidCallback(block: (password: PasswordModel) -> Unit): PasswordCustomViewNew {
        onPasswordValid = block
        return this
    }

    companion object {
        const val PASSWORD_MIN_LENGTH = 8
    }
}
