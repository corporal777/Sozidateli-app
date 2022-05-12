package com.example.holders.redesign

import android.graphics.drawable.Drawable
import com.example.R
import com.example.databinding.ItemEventTagBinding
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem

class EventTagItem(
    val mId: Int,
    val mText: String?,
    val onTagClick: (id: Int) -> Unit,
    val onShowMoreClick: () -> Unit
) : BindableItem<ItemEventTagBinding>() {


    override fun bind(viewBinding: ItemEventTagBinding, p1: Int) {
        viewBinding.apply {
            if (mId == 1) {
                btnTag.apply {
                    this.text = mText
                    val img: Drawable? = context.getDrawable(R.drawable.ic_arrow_down_for_tags)
                    this.setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                    setOnClickListener {
                        onShowMoreClick()
                    }
                }
            } else {
                btnTag.text = mText
                btnTag.setOnClickListener {
                    onTagClick(mId)
                }
            }

        }
    }

    override fun getLayout(): Int = R.layout.item_event_tag
}