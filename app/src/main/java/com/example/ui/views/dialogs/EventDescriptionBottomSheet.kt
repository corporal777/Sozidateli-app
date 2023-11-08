package com.example.ui.views.dialogs

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.core.text.set
import com.example.R
import com.example.databinding.BottomSheetEventDescriptionBinding
import com.example.util.URLSpanNoUnderline
import com.example.util.markWon
import com.example.util.showCustomTabsBrowser
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class EventDescriptionBottomSheet(
    context: Context,
    private val title: String?,
    private val description: String?
) : BottomSheetDialog(context) {

    private val mBinding = BottomSheetEventDescriptionBinding.inflate(LayoutInflater.from(context))

    init {
        setContentView(mBinding.root)
        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

        mBinding.apply {
            tvBottomSheetLabel.text = title
            tvDescription.apply {
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                BetterLinkMovementMethod.linkifyHtml(this)
                    .setOnLinkClickListener { textView, url ->
                        showCustomTabsBrowser(context, url)
                        true
                    }
                text = getMarkdownFormattedText(description)
            }

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