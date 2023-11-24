package com.example.ui.views.loading

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.LayoutLoadingButtonBinding
import com.example.extensions.dp
import com.example.extensions.inverseSp
import com.example.extensions.sp
import com.example.ui.views.UserSubscribeButton
import com.example.util.getDrawable
import io.github.inflationx.calligraphy3.CalligraphyUtils

class CustomLoadingButton : ConstraintLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        obtainAttributes(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var isProgressVisible = false
    private var buttonInitText = ""

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
        val buttonTextColor = a.getColor(R.styleable.CustomLoadingButton_buttonTextColor, Color.WHITE)
        val buttonTextSize = a.getDimensionPixelSize(R.styleable.CustomLoadingButton_buttonTextSize, 16.sp)
        val buttonText = a.getText(R.styleable.CustomLoadingButton_buttonText)

        a.recycle()
        initActionButton(buttonBack, buttonTextColor, buttonMinHeight, buttonTextSize, buttonText)

        buttonInitText = buttonText.toString()
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
        buttonText: CharSequence
    ) {
        loadingView.btnLoad.apply {
            CalligraphyUtils.applyFontToTextView(context, this, "fonts/sf_pro_text_semibold.ttf")
            background = back ?: getDrawable(R.drawable.custom_btn_brown_selectable)
            minimumHeight = buttonHeight
            minHeight = buttonHeight
            stateListAnimator = null
            letterSpacing = -0.01f
            isAllCaps = false
            text = buttonText
            setTextColor(color)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, buttonTextSize.inverseSp)
        }

    }

    override fun setEnabled(enabled: Boolean) {
        loadingView.btnLoad.isEnabled = enabled
    }

    override fun setSelected(enabled: Boolean){
        loadingView.btnLoad.isSelected = !enabled
    }

    fun setButtonText(buttonText: CharSequence) {
        loadingView.btnLoad.text = buttonText
        buttonInitText = buttonText.toString()
    }

    fun showProgressLoading(show : Boolean) {
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