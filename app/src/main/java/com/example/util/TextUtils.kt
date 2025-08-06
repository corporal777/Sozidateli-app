package com.example.util

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.app.R
import java.util.regex.Pattern

object TextUtils {

    val font = FontFamily(
        Font(R.font.sf_pro_text_regular, FontWeight.Thin),
        Font(R.font.sf_pro_text, FontWeight.Normal),
        Font(R.font.sf_pro_text_medium, FontWeight.Medium),
        Font(R.font.sf_pro_text_semibold, FontWeight.SemiBold),
        Font(R.font.sf_pro_text_bold, FontWeight.Bold),
        Font(R.font.sf_pro_display_heavy, FontWeight.ExtraBold),
    )
}