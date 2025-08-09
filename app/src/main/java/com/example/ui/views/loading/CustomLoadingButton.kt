package com.example.ui.views.loading

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.app.R
import com.example.app.databinding.LayoutLoadingButtonBinding
import com.example.common.dp
import com.example.util.getDrawable

class CustomLoadingButton : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var isProgressVisible = false
    private var buttonInitText: CharSequence = ""

    private val loadingView =
        LayoutLoadingButtonBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        loadingView.progressLoad.isVisible = false
        loadingView.btnLoad.setOnClickListener {
            this.callOnClick()
        }
    }

    private fun obtainAttributes(attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CustomLoadingButton)
        val buttonBack = a.getDrawable(R.styleable.CustomLoadingButton_buttonBackground)
        val buttonMinHeight = a.getDimensionPixelSize(
            R.styleable.CustomLoadingButton_buttonMinHeight,
            R.dimen.auth_button_min_height
        )
        val buttonTextColor =
            a.getColor(R.styleable.CustomLoadingButton_buttonTextColor, Color.WHITE)
        val buttonTextSize = a.getDimensionPixelSize(
            R.styleable.CustomLoadingButton_buttonTextSize,
            resources.getDimensionPixelSize(R.dimen.common_button_text_size)
        )
        val buttonText = a.getText(R.styleable.CustomLoadingButton_buttonText) ?: ""
        val buttonDrawable = a.getDrawable(R.styleable.CustomLoadingButton_buttonDrawable)

        val buttonPadding = a.getDimensionPixelSize(R.styleable.CustomLoadingButton_buttonPaddingHorizontal, 0.dp)

        a.recycle()
        initActionButton(
            buttonBack,
            buttonTextColor,
            buttonMinHeight,
            buttonTextSize,
            buttonText,
            buttonDrawable,
            buttonPadding
        )

        buttonInitText = buttonText
        loadingView.progressLoad.apply {
            setProgressColor(buttonTextColor)
            setSize(25.dp)
        }
    }


    private fun initActionButton(
        back: Drawable?,
        color: Int,
        buttonHeight: Int,
        buttonTextSize: Int,
        buttonText: CharSequence?,
        buttonDrawable: Drawable?,
        buttonPaddingHorizontal : Int
    ) {
        loadingView.ivButton.apply {
            isVisible = buttonDrawable != null
            setImageDrawable(buttonDrawable)
        }
        loadingView.btnLoad.apply {
            if (back != null) background = back
            minimumHeight = buttonHeight
            minHeight = buttonHeight
            text = buttonText
            setTextColor(color)
            setTextSize(TypedValue.COMPLEX_UNIT_PX, buttonTextSize.toFloat())
            setPadding(buttonPaddingHorizontal, 0, buttonPaddingHorizontal,0)
        }

    }

    override fun setEnabled(enabled: Boolean) {
        loadingView.btnLoad.isEnabled = enabled
    }

    override fun setSelected(enabled: Boolean) {
        loadingView.btnLoad.isSelected = !enabled
    }

    fun setButtonText(buttonText: CharSequence) {
        loadingView.btnLoad.text = buttonText
        buttonInitText = buttonText
    }

    fun setButtonTextColor(buttonTextColor: Int) {
        loadingView.btnLoad.setTextColor(ContextCompat.getColorStateList(context, buttonTextColor))
    }

    fun setButtonBackground(back: Int) {
        loadingView.btnLoad.background = getDrawable(back)
    }

    fun setProgressColor(buttonTextColor: Int) {
        loadingView.progressLoad.setProgressColor(
            ContextCompat.getColor(context, buttonTextColor)

        )
    }

    fun showProgressLoading(show: Boolean) {
        if (isProgressVisible == show) return
        isProgressVisible = show
        loadingView.apply {
            progressLoad.isVisible = show
            btnLoad.apply {
                text = if (show) "" else buttonInitText
                this.isClickable = !show
            }
            this@CustomLoadingButton.isClickable = !show
        }
    }
}