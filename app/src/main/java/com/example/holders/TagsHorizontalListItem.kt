package com.example.holders

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import com.example.app.R
import com.example.data.models.Tag
import com.example.app.databinding.ItemTagsHorizontalListBinding
import com.example.ui.views.TagChipNew
import com.example.util.getDrawable
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item

class TagsHorizontalListItem(
    private val tags: List<Tag>,
    private val onSelectedChange: () -> Unit
) : BindableItem<ItemTagsHorizontalListBinding>(-1011L) {


    @SuppressLint("ResourceType")
    override fun bind(viewBinding: ItemTagsHorizontalListBinding, position: Int) {
        viewBinding.apply {
            tagGroup.apply {
                removeAllViews()

                if (tags.size > 6) {
                    val otherSize = tags.size - 5
                    for (i in tags.indices) {
                        addView(createTagChip(tags[i]))
                        if (i == 5) break
                    }
                    addView(TagChipNew(context).apply {
                        id = 1
                        text = "Еще $otherSize "
                        isChecked = false
                        isClickable = true
                        val img = getDrawable(R.drawable.ic_arrow_down_for_tags)
                        setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                        setOnClickListener {
                            for (i in 6 until tags.size) {
                                addView(createTagChip(tags[i]))
                            }
                            visibility = View.GONE
                        }

                    })
                } else {
                    tags.forEach { tag ->
                        addView(createTagChip(tag))
                    }
                }
            }
        }
    }

    private fun View.createTagChip(tag : Tag) : CompoundButton {
        return TagChipNew(context).apply {
            id = tag.id.toInt()
            text = tag.name
            isChecked = tag.isSelected

            setOnCheckedChangeListener { _, isChecked ->
                tag.isSelected = isChecked
                onSelectedChange()
            }
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is TagsHorizontalListItem) return false
        if (tags != other.tags) return false
        return true
    }

    override fun getLayout() = R.layout.item_tags_horizontal_list
}