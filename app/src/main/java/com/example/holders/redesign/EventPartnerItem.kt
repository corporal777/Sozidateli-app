package com.example.holders.redesign

import android.content.Context
import android.text.SpannableStringBuilder
import android.text.style.URLSpan
import androidx.core.text.getSpans
import androidx.core.text.set
import com.example.app.R
import com.example.app.databinding.ItemPartnerBinding
import com.example.extensions.markWon
import com.example.util.URLSpanNoUnderline
import com.example.util.setImage
import com.example.util.setImagePicasso
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class EventPartnerItem(
    val context: Context,
    val id: Int?,
    val name: String?,
    val title: String?,
    val image: String?,
    val onPartnerClick: (id: Int) -> Unit
) : BindableItem<ItemPartnerBinding>(id?.toLong() ?: 0) {

    private val description = getMarkdownFormattedText(context, title)

    override fun bind(viewBinding: ItemPartnerBinding, position: Int) {
        viewBinding.apply {

            tvPartnerName.text = name ?: ""
            tvPartnerTitle.text = description

            ivPartnerImage.setImagePicasso(
                url = image,
                placeholder = R.drawable.background_image_placeholder,
                error = R.drawable.background_image_placeholder
            )

            root.setOnClickListener {
                if (id != null) onPartnerClick(id)
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is EventPartnerItem) return false
        if (id != other.id) return false
        if (name != other.name) return false
        if (title != other.title) return false
        if (image != other.image) return false
        return true
    }

    private fun getMarkdownFormattedText(
        context: Context,
        description: String?
    ): SpannableStringBuilder? {
        if (description.isNullOrEmpty()) return null
        else {
            val spanned = markWon(context).toMarkdown(description.replace("\n", " ") ?: "")
            return SpannableStringBuilder(spanned).apply {
                val urls = getSpans<URLSpan>()
                urls.forEach {
                    val start = getSpanStart(it)
                    val end = getSpanEnd(it)
                    removeSpan(it)
                    set(start..end, URLSpanNoUnderline(it.url))
                }
                replace(Regex("[\\t\\n\\r]+"), " ")
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_partner
}