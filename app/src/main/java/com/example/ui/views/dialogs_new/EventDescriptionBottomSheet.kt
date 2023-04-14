package com.example.ui.views.dialogs_new

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.view.LayoutInflater
import androidx.core.text.getSpans
import androidx.core.text.set
import androidx.core.view.isVisible
import com.example.R
import com.example.databinding.BottomSheetEventDescriptionBinding
import com.example.databinding.BottomSheetUpdateAppBinding
import com.example.util.URLSpanNoUnderline
import com.example.util.markWon
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

class EventDescriptionBottomSheet(
    context: Context,
    private val title: String?,
    private val description: String?
) : BottomSheetDialog(context) {

    private val mBinding = BottomSheetEventDescriptionBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(mBinding.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED

        mBinding.apply {
            tvBottomSheetLabel.text = title
            tvDescription.text = getMarkdownFormattedText(description)

            btnClose.setOnClickListener {
                dismiss()
            }
        }
    }

    private fun getMarkdownFormattedText(description: String?): SpannableStringBuilder {
        val spanned = markWon(context).toMarkdown(description ?: "")
        return SpannableStringBuilder(spanned).apply {
            val urls = getSpans<URLSpan>()
            urls.forEach {
                val start = getSpanStart(it)
                val end = getSpanEnd(it)
                removeSpan(it)
                set(start..end, URLSpanNoUnderline(it.url))
            }
        }
    }
}