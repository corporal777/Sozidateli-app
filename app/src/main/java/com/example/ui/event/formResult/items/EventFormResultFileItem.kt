package com.example.ui.event.formResult.items

import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.EventFile
import com.example.databinding.ItemEventFormResultStringBinding
import com.example.ui.views.CustomSpannableString
import com.example.util.showCustomTabsBrowser
import com.example.util.showFileBrowser
import com.xwray.groupie.databinding.BindableItem

class EventFormResultFileItem (
    val id: String?,
    val title: String?,
    val value: EventFile?,
) : BindableItem<ItemEventFormResultStringBinding>(id?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemEventFormResultStringBinding, position: Int) {
        viewBinding.apply {
            tvFormTitle.apply {
                isVisible = !title.isNullOrEmpty()
                text = title
            }
            tvFormValue.apply {
                highlightColor = ContextCompat.getColor(context, R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
                text = CustomSpannableString(value?.name).apply {
                    setClickSpan(tvFormValue){
                        if (value?.isFilePDF() == true) showFileBrowser(context, value.path.toString())
                        else showCustomTabsBrowser(context, value?.path.toString())
                    }
                }
            }

        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result_string
}