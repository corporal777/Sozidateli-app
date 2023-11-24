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

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextInputView)
        titleText = a.getText(R.styleable.CustomTextInputView_titleText)
        a.recycle()
        layoutView.tvTitle.text = titleText
    }

    private var onTextChanged: (text: String?) -> Unit = {}
    private var onFocused: (focused: Boolean) -> Unit = {}
    private val layoutView =
        LayoutCustomPhoneTextInputViewBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        layoutView.apply {
            etInput.apply {
                onInputTextChanged {
                    onTextChanged.invoke(clearPhoneText(it.toString()))
                }
                onInputFocusChanged { hasFocus ->
                    onFocused.invoke(hasFocus)
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

    fun showTextError(text: String) {
        layoutView.apply {
            btnAction.isEnabled = false
            btnAction.isVisible = true
            tvTitle.text = text
            tvTitle.setTextColor(ContextCompat.getColor(context, R.color.red_new))
        }
    }

    fun initFocused(onTextChanged: (focused: Boolean) -> Unit) {
        onFocused = onTextChanged
    }

    fun initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
        layoutView.etInput.setText(text)
        this.onTextChanged = onTextChanged
    }

    fun setText(text: String?) {
        layoutView.etInput.setText(text)
    }

    fun setInputEnabled(enabled: Boolean) {
        layoutView.etInput.isEnabled = enabled
    }

    private fun clearPhoneText(text : String?): String? {
        if (text.isNullOrEmpty()) return null
        var newText = text.replace("_", "")
        newText = newText.replace(" ", "")
        return newText
    }
}