package com.example.holders

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CompoundButton
import com.example.R
import com.example.data.models.Tag
import com.example.ui.views.TagChipNew
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_tags_horizontal_list.*

class TagsHorizontalListItem(
    private val tags: List<Tag>,
    private val onSelectedChange: () -> Unit
) : Item() {

    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            tagGroup.apply {
                removeAllViews()

                val chip: (Tag) -> CompoundButton = { tag ->
                    TagChipNew(context).apply {
                        id = tag.id.toInt()
                        text = tag.name
                        isChecked = tag.isSelected

                        setOnCheckedChangeListener { _, isChecked ->
                            tag.isSelected = isChecked
                            onSelectedChange()
                        }
                    }
                }
                if (tags.size > 6) {
                    val otherSize = tags.size - 5
                    for (i in tags.indices) {
                        tags[i].apply {
                            addView(chip(this))
                        }
                        if (i == 5) break
                    }
                    addView(TagChipNew(context).apply {
                        id = 1
                        text = "Еще $otherSize "
                        isChecked = false
                        isClickable = true
                        val img: Drawable =
                            context.resources.getDrawable(R.drawable.ic_arrow_down_for_tags)
                        setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                        setOnClickListener {
                            for (i in 6 until tags.size) {
                                tags[i].apply {
                                    addView(chip(this))
                                }
                            }
                            visibility = View.GONE
                        }

                    })
                } else {
                    tags.forEach { tag ->
                        addView(chip(tag))
                    }
                }
            }
        }
    }

    override fun getLayout() = R.layout.item_tags_horizontal_list
}