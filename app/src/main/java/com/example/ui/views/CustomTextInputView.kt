package com.example.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.telephony.PhoneNumberFormattingTextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.LayoutCustomTextInputViewBinding
import com.example.extensions.dp
import com.example.util.SearchInput
import com.example.util.getColor
import com.example.util.getDrawable
import com.example.util.setTint
import onFocusChanged
import onTextChanged
import setOnClickListener

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

    private val layoutView =
        LayoutCustomTextInputViewBinding.inflate(LayoutInflater.from(context), this, true)


    private var titleText: CharSequence? = ""
    private var hintText: CharSequence = ""

    private var inputTextColor: Int = Color.BLACK
    private var inputTextType: Int = 0x00004001

    private var inputMaxLines: Int = 1
    private var inputMinLines: Int = 1
    private var inputEnabled: Boolean = true
    private var inputCheckBoxVisible: Boolean = false


    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextInputView)
        titleText = a.getText(R.styleable.CustomTextInputView_titleText)
        hintText = a.getText(R.styleable.CustomTextInputView_hintText)
        inputTextType = a.getInt(R.styleable.CustomTextInputView_inputType, 0x00004001)
        inputMaxLines = a.getInt(R.styleable.CustomTextInputView_inputMaxLines, 1)
        inputMinLines = a.getInt(R.styleable.CustomTextInputView_inputMinLines, 1)
        inputEnabled = a.getBoolean(R.styleable.CustomTextInputView_inputEnabled, true)
        inputTextColor = a.getColor(R.styleable.CustomTextInputView_inputTextColor, Color.BLACK)
        inputCheckBoxVisible = a.getBoolean(R.styleable.CustomTextInputView_inputCheckBoxVisible, false)
        a.recycle()

        layoutView.tvTitle.apply {
            isVisible = !titleText.isNullOrEmpty()
            text = titleText
        }
        layoutView.etInput.apply {
            hint = hintText
            setTextColor(inputTextColor)
            inputType = inputTextType
            if (inputMinLines > 1 || inputMaxLines > 1) {
                gravity = Gravity.START and Gravity.TOP
                setPadding(0, 5.dp, 0, 5.dp)
            }
            maxLines = inputMaxLines
            minLines = inputMinLines
            isEnabled = inputEnabled
        }
        layoutView.scCheck.isVisible = inputCheckBoxVisible
    }

    private var onTextChanged: (text: String?) -> Unit = {}
    private var onFocused: (focused: Boolean) -> Unit = {}

    init {
        layoutView.apply {
            etInput.apply {
                onTextChanged {
                    onTextChanged.invoke(it.toString())
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
            if (show) tvTitle.setTextColor(getColor(R.color.title_text_error_red))
            else {
                tvTitle.text = titleText
                tvTitle.setTextColor(getColor(R.color.chat_list_date))
            }
        }
    }

    fun showTextError(text: String) {
        layoutView.apply {
            btnAction.isEnabled = false
            btnAction.isVisible = true
            tvTitle.text = text
            tvTitle.setTextColor(getColor(R.color.title_text_error_red))
        }
    }

    fun initFocused(onTextChanged: (focused: Boolean) -> Unit) {
        onFocused = onTextChanged
    }

    fun initInput(text: String? = null, onTextChanged: (text: CharSequence?) -> Unit) {
        layoutView.etInput.setText(text)
        this.onTextChanged = onTextChanged
    }

    fun initSwitch(checked: Boolean, onCheckedChanged: (isChecked: Boolean) -> Unit) {
        layoutView.scCheck.isChecked = checked
        layoutView.scCheck.setOnCheckedChangeListener { _, isChecked -> onCheckedChanged(isChecked) }
    }

    fun setText(text: String?) = layoutView.etInput.setText(text)

    override fun setEnabled(enabled: Boolean) {
        layoutView.etInput.isEnabled = enabled
        layoutView.btnAction.isEnabled = enabled
    }
}