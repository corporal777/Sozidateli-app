package com.example.ui.views

import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.LayoutCustomCheckViewBinding
import com.example.extensions.dp

class CustomCheckView : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private var isCheckedListener: (isChecked: Boolean) -> Unit = {}
    private var isCheckBoxVisible = false
    private val checkView = LayoutCustomCheckViewBinding.inflate(LayoutInflater.from(context), this, true)

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomCheckView)
        val checkViewText = a.getText(R.styleable.CustomCheckView_checkViewText)
        val isCheckable = a.getBoolean(R.styleable.CustomCheckView_isCheckable, false)

        isCheckBoxVisible = isCheckable
        a.recycle()
        setText(checkViewText)

        checkView.scMobilePhone.apply {
            isInvisible = !isCheckBoxVisible
            setOnCheckedChangeListener { buttonView, isChecked ->
                isCheckedListener.invoke(isChecked)
            }
        }
        checkView.ivCheck.apply {
            isInvisible = isCheckBoxVisible
            setOnClickListener {
                this@CustomCheckView.callOnClick()
            }
        }
    }

    init {
        checkView.apply {
            tvCheck.apply {
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                movementMethod = LinkMovementMethod.getInstance()
            }
            progressLoad.apply {
                isVisible = false
                setSize(19.dp)
            }
        }
    }

    fun setChecked(state: Boolean) {
        if (isCheckBoxVisible) checkView.scMobilePhone.isChecked = state
        else {
            if (state) checkView.ivCheck.setImageResource(R.drawable.ic_switch_enabled)
            else checkView.ivCheck.setImageResource(R.drawable.ic_switch)
        }
    }

    fun getTextView(): AppCompatTextView = checkView.tvCheck

    fun setText(text: CharSequence?) { checkView.tvCheck.text = text }

    fun setClickableText(text: CharSequence?, start: Int, onClick: () -> Unit) {
        checkView.tvCheck.text = CustomSpannableString(text).apply {
            setClickSpanWithLength(checkView.tvCheck, start, length) { onClick.invoke() }
        }
    }


    fun showProgressLoading(show: Boolean) {
        checkView.apply {
            progressLoad.isVisible = show

            if (isCheckBoxVisible) scMobilePhone.isInvisible = show
            else ivCheck.isInvisible = show

            this@CustomCheckView.isClickable = !show
        }
    }

    fun showError(show: Boolean){
        checkView.scMobilePhone.isSelected = show
    }

    fun setOnCheckedListener(block: (isChecked: Boolean) -> Unit) {
        isCheckedListener = block
    }
}