package com.example.ui.views

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.LayoutCustomPhoneTextInputViewBinding
import com.example.util.getDrawable

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
    private var iconDrawable: Drawable? = getDrawable(R.drawable.ic_input_error_icon)
    private var inputEnabled: Boolean = true
    private var inputId : Int = generateViewId()
    private var isErrorShown = false
    private var isIconShown = false

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomPhoneTextInputView)
        titleText = a.getText(R.styleable.CustomPhoneTextInputView_phoneTitleText)
        hintText = a.getText(R.styleable.CustomPhoneTextInputView_phoneHintText)
        inputEnabled = a.getBoolean(R.styleable.CustomPhoneTextInputView_phoneInputEnabled, true)
        iconDrawable = a.getDrawable(R.styleable.CustomPhoneTextInputView_phoneTextIcon)
        a.recycle()

        layoutView.tvTitle.text = titleText
        layoutView.etPhoneInput.apply {
            isEnabled = inputEnabled
            if (!hintText.isNullOrEmpty()) setPhoneHint(hintText)
        }
        layoutView.btnAction.setImageDrawable(iconDrawable)
    }

    private var onTextChanged: (text: String?) -> Unit = {}
    private val layoutView =
        LayoutCustomPhoneTextInputViewBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        layoutView.apply {
            etPhoneInput.apply {
                id = inputId
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
        if (isErrorShown == show) return
        isErrorShown = show
        layoutView.apply {
            btnAction.isEnabled = !show
            btnAction.isVisible = show
            if (show) tvTitle.setTextColor(ContextCompat.getColor(context, R.color.title_text_error_red))
            else {
                tvTitle.text = titleText
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.chat_list_date))
            }
        }
    }

    fun showIcon(show: Boolean){
        if (isIconShown == show) return
        isIconShown = show
        layoutView.btnAction.isVisible = isIconShown
    }

    fun setText(text : String?) = layoutView.etPhoneInput.setPhoneText(text)
    fun setHint(text : String?) = layoutView.etPhoneInput.setHint(text)


    fun initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
        if (text != null) layoutView.etPhoneInput.setText(text)
        this.onTextChanged = onTextChanged
    }
}