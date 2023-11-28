package com.example.ui.views.codeView

import android.content.Context
import androidx.core.content.ContextCompat
import com.example.R

internal object CodeConfirmationViewUtils {

    private var defaultStyle: CodeConfirmationView.Style? = null

    fun getDefault(context: Context): CodeConfirmationView.Style {
        if (defaultStyle == null) {
            val resources = context.resources
            val symbolViewStyle = SymbolView.Style(
                showCursor = true,
                width = resources.getDimensionPixelSize(R.dimen.symbol_view_width),
                height = resources.getDimensionPixelSize(R.dimen.symbol_view_height),
                backgroundColorActive = ContextCompat.getColor(context, R.color.white),
                backgroundColor = ContextCompat.getColor(context, R.color.code_view_empty_background_color),
                borderColor = ContextCompat.getColor(context, R.color.code_view_empty_border_color),
                borderWidth = resources.getDimensionPixelSize(R.dimen.symbol_view_stroke_width),
                borderColorActive = ContextCompat.getColor(context, R.color.code_view_input_border_color),
                borderColorEntered = ContextCompat.getColor(context, R.color.code_view_entered_border_color),
                borderWidthActive = resources.getDimensionPixelSize(R.dimen.symbol_view_active_stroke_width),
                borderCornerRadius = resources.getDimension(R.dimen.event_registration_form_title_margin_top),
                textColor = ContextCompat.getColor(context, R.color.black),
                textSize = resources.getDimensionPixelSize(R.dimen.symbol_view_text_size)
            )
            defaultStyle = CodeConfirmationView.Style(
                codeLength = CodeConfirmationView.DEFAULT_CODE_LENGTH,
                symbolsSpacing = resources.getDimensionPixelSize(R.dimen.symbols_spacing),
                symbolViewStyle = symbolViewStyle,
                isPasteEnabled = true
            )
        }
        return defaultStyle!!
    }
}