package com.example.ui.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.LayoutCustomPhoneTextInputViewBinding

class CustomPhoneTextInputView : LinearLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var titleText: CharSequence = ""
    private var hintText: CharSequence? = ""

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomPhoneTextInputView)
        titleText = a.getText(R.styleable.CustomPhoneTextInputView_phoneTitleText)
        hintText = a.getText(R.styleable.CustomPhoneTextInputView_phoneHintText)
        a.recycle()
        layoutView.tvTitle.text = titleText
        layoutView.etInput.hint = hintText
    }

    private var onTextChanged: (text: String?) -> Unit = {}
    private val layoutView =
        LayoutCustomPhoneTextInputViewBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        layoutView.apply {
            etInput.apply {
                onInputTextChanged {
                    onTextChanged.invoke(it.toString())
                }
                onInputFocusChanged { hasFocus ->
                    clSearch.setBackgroundResource(
                        if (hasFocus) R.drawable.background_custom_input_view_focused
                        else R.drawable.background_custom_input_view_unfocused
                    )
                }

            }
        }
    }

    fun showError(show: Boolean) {
        layoutView.apply {
            btnAction.isEnabled = !show
            btnAction.isVisible = show
            if (show) tvTitle.setTextColor(ContextCompat.getColor(context, R.color.red_new))
            else {
                tvTitle.text = titleText
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.chat_list_date))
            }
        }
    }

    fun initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
        if (text != null) layoutView.etInput.setText(text)
        this.onTextChanged = onTextChanged
    }
}