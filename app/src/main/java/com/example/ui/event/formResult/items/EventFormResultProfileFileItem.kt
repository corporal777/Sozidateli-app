package com.example.ui.event.formResult.items

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.FileModel
import com.example.data.models.PrefilledFieldFiles
import com.example.databinding.ItemEventFormResultProfileBinding
import com.example.extensions.parseAsHtmlWithoutUnderline
import com.example.util.showCustomTabsBrowser
import com.example.util.showFileBrowser
import com.xwray.groupie.databinding.BindableItem
import me.saket.bettermovementmethod.BetterLinkMovementMethod

class EventFormResultProfileFileItem(
    val id: Int,
    val title : String,
    val field: List<FileModel>?
) : BindableItem<ItemEventFormResultProfileBinding>(id.toLong()) {

    private val fieldAlpha = if (field == null || field.isNullOrEmpty()) 0.4f else 1.0f

    private val fieldText = if (field == null || field.isNullOrEmpty()) "Не заполнено"
    else {
        var filesText = ""
        field.forEachIndexed { index, file ->
            val divider = if (index == 0) "" else "<br>"
            filesText += "$divider<a href='${file.uri}'>${file.name}</a>"
        }
        filesText.parseAsHtmlWithoutUnderline()
    }

    override fun bind(viewBinding: ItemEventFormResultProfileBinding, position: Int) {
        viewBinding.apply {
            tvFormTitle.apply {
                isVisible = false
            }

            tvProfileFormTitle.apply {
                isVisible = !title.isNullOrEmpty()
                text = title
            }
            tvProfileFormField.apply {
                highlightColor = ContextCompat.getColor(context, R.color.event_tabs_text_unchecked)

                alpha = fieldAlpha
                text = fieldText

                BetterLinkMovementMethod.linkifyHtml(this)
                    .setOnLinkClickListener { _, url ->
                        val type = field?.find { x -> x.uri == url }
                        if (type?.isFilePDF() == true) showFileBrowser(context, url)
                        else showCustomTabsBrowser(context, url)

                        true
                    }
            }
        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result_profile
}