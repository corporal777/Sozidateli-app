package com.example.ui.views

import android.content.Context
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.LayoutCustomTextInputViewBinding
import com.example.util.SearchInput
import onFocusChanged

class CustomTextInputView : LinearLayout {

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
    private var hintText: CharSequence = ""
    private var inputType: Int = 0x00004001

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextInputView)
        titleText = a.getText(R.styleable.CustomTextInputView_titleText)
        hintText = a.getText(R.styleable.CustomTextInputView_hintText)
        inputType = a.getInt(R.styleable.CustomTextInputView_inputType, 0x00004001)
        a.recycle()

        layoutView.tvTitle.text = titleText
        layoutView.etInput.hint = hintText
        layoutView.etInput.inputType = inputType
    }

    private var onTextChanged: (text: String?) -> Unit = {}
    private var onFocused: (focused : Boolean) -> Unit = {}
    private var isCheckBoxVisible = false
    private val layoutView =
        LayoutCustomTextInputViewBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        layoutView.apply {
            etInput.apply {
                SearchInput(this).apply {
                    setOnTextChange {
                        onTextChanged.invoke(it)
                    }
                }
                onFocusChanged { hasFocus ->
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

    fun initFocused(onTextChanged: (focused : Boolean) -> Unit) {
        onFocused = onTextChanged
    }

    fun initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
        layoutView.etInput.setText(text)
        this.onTextChanged = onTextChanged
    }

    fun setText(text : String?){
        layoutView.etInput.setText(text)
    }

    fun setInputEnabled(enabled : Boolean){
        layoutView.etInput.isEnabled = enabled
    }

    fun addPhoneNumberFormatting(){
        layoutView.etInput.addTextChangedListener(PhoneNumberFormattingTextWatcher())
    }

}