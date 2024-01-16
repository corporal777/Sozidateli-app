package com.example.ui.views.codeView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Size
import android.util.TypedValue
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.example.R
import com.example.extensions.dp
import com.example.util.getDrawable
import com.example.util.setRightDrawableWithIntrinsicBounds
import io.github.inflationx.calligraphy3.CalligraphyUtils
import onFocusChanged


private const val textPaintAlphaAnimDuration = 25L
private const val borderPaintAlphaAnimDuration = 150L

private const val cursorAlphaAnimDuration = 500L
private const val cursorAlphaAnimStartDelay = 200L

private const val cursorSymbol = "|"

class EditView : AppCompatEditText {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var backgroundColorFocused = Color.parseColor("#4DC3996C")
    private var backgroundColorUnfocused = Color.parseColor("#33C3996C")
    private var textColor = Color.BLACK
    private var hintTextColor = ContextCompat.getColor(context, R.color.input_hint_text_color)
    private var textSize = resources.getDimensionPixelSize(R.dimen.common_edit_text_size)

    private val cornerRadius: Float = resources.getDimension(R.dimen.chat_message_padding_vertical)

    private val backgroundRect = RectF()
    private val backgroundPaint: Paint = Paint().apply {
        color = backgroundColorUnfocused
        style = Paint.Style.FILL
    }

    private val titleTextPaint: Paint = Paint().apply {
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.chat_list_date)
        textSize = resources.getDimensionPixelSize(R.dimen.common_edit_text_title_size).toFloat()
        typeface = ResourcesCompat.getFont(context, R.font.sf_pro_display_regular)
        textAlign = Paint.Align.LEFT
    }

    private val editTextHeight = resources.getDimensionPixelSize(R.dimen.common_edit_text_min_height)

    var textTitle = "Тема вопроса *"
    var text: String = ""


    init {
        minHeight = editTextHeight + calculateTitleSize().height + 5.dp
        minimumHeight = editTextHeight + calculateTitleSize().height + 5.dp
        maxLines = 1
        isSingleLine = true
        background = null

        CalligraphyUtils.applyFontToTextView(context, this, "fonts/sf_pro_display.OTF")
        setTextColor(textColor)
        setHintTextColor(hintTextColor)
        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.toFloat())

        onFocusChanged { hasFocus ->
            if (hasFocus) backgroundPaint.color = backgroundColorFocused
            else backgroundPaint.color = backgroundColorUnfocused
        }
        maxEms = 4
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        backgroundRect.left = 0f
        backgroundRect.top = (calculateTitleSize().height + 5.dp).toFloat()
        backgroundRect.right = width.toFloat()
        backgroundRect.bottom = height.toFloat()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawText(
            textTitle,
            0f,
            backgroundRect.top - 5.dp,
            titleTextPaint
        )

        canvas.drawRoundRect(
            backgroundRect,
            cornerRadius,
            cornerRadius,
            backgroundPaint
        )
        val topPadding = calculateTitleSize().height / 2
        setPadding(15.dp, topPadding.dp, 15.dp,0 )


        val d = getDrawable(R.drawable.ic_input_error_icon)

        setRightDrawableWithIntrinsicBounds(R.drawable.ic_input_error_icon)
    }


    private fun calculateTitleSize(): Size {
        val textBounds = Rect()
        titleTextPaint.getTextBounds(textTitle, 0, 1, textBounds)
        return Size(textBounds.width(), textBounds.height())
    }
}