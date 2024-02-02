package com.example.util

import android.content.Context
import android.graphics.Typeface
import com.example.ui.views.expandableTextView.CustomTypefaceSpan
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonConfiguration
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.RenderProps
import io.noties.markwon.SoftBreakAddsNewLinePlugin
import io.noties.markwon.SpanFactory
import org.commonmark.node.StrongEmphasis

class MarkdownEmphasisPlugin(val context: Context) : AbstractMarkwonPlugin() {


    override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
        builder.setFactory(StrongEmphasis::class.java, object : SpanFactory {
            override fun getSpans(configuration: MarkwonConfiguration, props: RenderProps): Any {
                val font = Typeface.createFromAsset(context.assets, "fonts/sf_pro_display_bold.ttf")
                return CustomTypefaceSpan("", font)
            }

        })
    }

    companion object {
        fun create(context: Context): MarkdownEmphasisPlugin {
            return MarkdownEmphasisPlugin(context)
        }
    }
}