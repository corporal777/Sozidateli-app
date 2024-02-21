package com.example.ui.event.formResult.items

import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import androidx.core.content.ContextCompat
import com.example.R
import com.example.data.models.EventFile
import com.example.databinding.ItemEventFormResultBinding
import com.example.ui.views.CustomSpannableString
import com.example.util.ClickableSpan
import com.example.util.showCustomTabsBrowser
import com.xwray.groupie.databinding.BindableItem

class EventFormResultFileItem (
    val id: String?,
    val title: String?,
    val value: EventFile?,
) : BindableItem<ItemEventFormResultBinding>(id?.toLong() ?: 0) {


    override fun bind(viewBinding: ItemEventFormResultBinding, position: Int) {
        viewBinding.apply {
            tvFormTitle.text = "$title"
            tvFormValue.apply {
                highlightColor = ContextCompat.getColor(context, R.color.event_tabs_text_unchecked)
                movementMethod = LinkMovementMethod.getInstance()
                text = CustomSpannableString(value?.name).apply {
                    setClickSpan(tvFormValue){
                        showCustomTabsBrowser(context, value?.path.toString())
                    }
                }
            }

        }
    }


    override fun getLayout(): Int = R.layout.item_event_form_result
}