package com.example.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.LayoutCustomTextInputBinding
import com.example.common.extensions.onFocusChanged
import com.example.common.extensions.onTextChanged

class CustomInputTextLayout : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var inputText: String? = null
    private var hintText: CharSequence = ""
    private var inputTextType: Int = InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
    private var inputEnabled: Boolean = true
    private var icon: Drawable? = null

    private var isErrorShown = false
    private var inputId: Int = generateViewId()

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomTextInputLayout)

        hintText = a.getText(R.styleable.CustomTextInputLayout_textHint)
        inputTextType = a.getInt(R.styleable.CustomTextInputLayout_textType, 16384)
        icon = a.getDrawable(R.styleable.CustomTextInputLayout_iconDrawable)

        a.recycle()

        layoutView.etCustomInput.apply {
            hint = hintText
            setTextColor(Color.BLACK)
            inputType = inputTextType
            this@CustomInputTextLayout.setEnabled(inputEnabled)
        }
        layoutView.btnAction.apply {
            isVisible = icon != null
            setImageDrawable(icon)
        }
    }

    private var onIconClick: () -> Unit = {}
    private var onTextChanged: (text: String?) -> Unit = {}

    private val layoutView =
        LayoutCustomTextInputBinding.inflate(LayoutInflater.from(context), this, true)


    init {
        layoutView.apply {
            btnAction.setOnClickListener { onIconClick.invoke() }
            etCustomInput.apply {
                id = inputId
                onTextChanged {
                    inputText = it.toString()
                    onTextChanged.invoke(it.toString())
                }
                onFocusChanged { hasFocus ->
                    clInputLayout.setBackgroundResource(
                        if (hasFocus) R.drawable.background_custom_input_view_focused
                        else R.drawable.background_custom_input_view_unfocused
                    )
                }
            }
        }
    }


    fun setText(text: String?) = layoutView.etCustomInput.setText(text)
    fun getEditText(): EditText = layoutView.etCustomInput

    override fun setEnabled(enabled: Boolean) {
        layoutView.etCustomInput.isEnabled = enabled
        layoutView.btnAction.isEnabled = enabled
    }


    fun setIconClickCallback(onIconClick: () -> Unit) {
        this.onIconClick = onIconClick
    }
}