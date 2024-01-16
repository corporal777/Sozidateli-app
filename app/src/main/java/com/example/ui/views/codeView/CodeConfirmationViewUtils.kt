package com.example.ui.views.codeView

import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import com.example.R

internal object CodeConfirmationViewUtils {

    private var defaultStyle: CodeConfirmationView.Style? = null

//    fun getDefault(context: Context): CodeConfirmationView.Style {
//        if (defaultStyle == null) {
//            val resources = context.resources
//            val symbolViewStyle = SymbolView.Style(
//                showCursor = true,
//                width = resources.getDimensionPixelSize(R.dimen.phone_code_view_width),
//                height = resources.getDimensionPixelSize(R.dimen.phone_code_view_height),
//                backgroundColorActive = getColor(context, R.color.white),
//                backgroundColor = getColor(context, R.color.code_view_empty_background_color),
//                borderColor = getColor(context, R.color.code_view_empty_border_color),
//                borderWidth = resources.getDimensionPixelSize(R.dimen.symbol_view_stroke_width),
//                borderColorActive = getColor(context, R.color.code_view_input_border_color),
//                borderColorEntered = getColor(context, R.color.code_view_entered_border_color),
//                borderColorError = getColor(context, R.color.title_text_error_red),
//                borderWidthActive = resources.getDimensionPixelSize(R.dimen.symbol_view_active_stroke_width),
//                borderCornerRadius = resources.getDimension(R.dimen.event_registration_form_title_margin_top),
//                textColor = getColor(context, R.color.black),
//                textSize = resources.getDimensionPixelSize(R.dimen.phone_code_view_text_size)
//            )
//            defaultStyle = CodeConfirmationView.Style(
//                codeLength = CodeConfirmationView.DEFAULT_CODE_LENGTH,
//                symbolsSpacing = resources.getDimensionPixelSize(R.dimen.phone_code_view_spacing),
//                symbolViewStyle = symbolViewStyle,
//                isPasteEnabled = true
//            )
//        }
//        return defaultStyle!!
//    }

    fun getFromAttributes(context: Context, a: TypedArray): CodeConfirmationView.Style {
        val resources = context.resources
        val symbolViewStyle = SymbolView.Style(
            showCursor = true,
            backgroundColorActive = getColor(context, R.color.white),
            backgroundColor = getColor(context, R.color.code_view_empty_background_color),
            borderColor = getColor(context, R.color.code_view_empty_border_color),
            borderWidth = resources.getDimensionPixelSize(R.dimen.symbol_view_stroke_width),
            borderColorActive = getColor(context, R.color.code_view_input_border_color),
            borderColorEntered = getColor(context, R.color.code_view_entered_border_color),
            borderColorError = getColor(context, R.color.title_text_error_red),
            borderWidthActive = resources.getDimensionPixelSize(R.dimen.symbol_view_active_stroke_width),
            borderCornerRadius = resources.getDimension(R.dimen.event_registration_form_title_margin_top),
            textColor = getColor(context, R.color.black),
            width = a.getDimensionPixelSize(
                R.styleable.CodeConfirmationView_codeViewWidth,
                resources.getDimensionPixelSize(R.dimen.phone_code_view_height)
            ),
            height = a.getDimensionPixelSize(
                R.styleable.CodeConfirmationView_codeViewHeight,
                resources.getDimensionPixelSize(R.dimen.phone_code_view_height)
            ),
            textSize = a.getDimensionPixelSize(
                R.styleable.CodeConfirmationView_codeViewTextSize,
                resources.getDimensionPixelSize(R.dimen.phone_code_view_text_size)
            )
        )
        defaultStyle = CodeConfirmationView.Style(
            codeLength = a.getInt(
                R.styleable.CodeConfirmationView_codeViewLength,
                CodeConfirmationView.DEFAULT_CODE_LENGTH
            ),
            symbolsSpacing = a.getDimensionPixelSize(
                R.styleable.CodeConfirmationView_codeViewSpace,
                resources.getDimensionPixelSize(R.dimen.phone_code_view_spacing)
            ),
            symbolViewStyle = symbolViewStyle,
            isPasteEnabled = true
        )
        return defaultStyle!!
    }
}