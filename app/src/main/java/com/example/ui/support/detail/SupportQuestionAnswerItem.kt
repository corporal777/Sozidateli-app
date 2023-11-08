package com.example.ui.support.detail

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.core.text.set
import com.example.R
import com.example.databinding.ItemSupportQuestionAnswerBinding
import com.example.util.URLSpanNoUnderline
import com.example.util.markWon
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.databinding.BindableItem
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class SupportQuestionAnswerItem(
    val context: Context,
    val title: String,
    val answer: String
) : BindableItem<ItemSupportQuestionAnswerBinding>() {

    private val formattedAnswer = answer.toMarkdownText(context)

    override fun bind(viewBinding: ItemSupportQuestionAnswerBinding, position: Int) {
        viewBinding.apply {
            tvTitle.text = title
            tvAnswer.apply {
                highlightColor = ContextCompat.getColor(context, R.color.profile_id_text)
                BetterLinkMovementMethod.linkifyHtml(this)
                    .setOnLinkClickListener { _, url ->
                        showCustomTabsBrowser(context, url)
                        true
                    }
                text = formattedAnswer
            }
        }
    }

    private fun String?.toMarkdownText(context: Context): SpannableStringBuilder? {
        if (this.isNullOrBlank()) return null
        else {
            val spanned = markWon(context).toMarkdown(this)
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

    override fun getLayout(): Int = R.layout.item_support_question_answer
}