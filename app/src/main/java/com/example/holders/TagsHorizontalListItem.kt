package com.example.holders

import com.example.R
import com.example.data.models.Tag
import com.example.ui.views.TagChip
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_tags_horizontal_list.*

class TagsHorizontalListItem(
        private val tags: List<Tag>,
        private val onSelectedChange: (selectedTags: List<Tag>) -> Unit
) : Item() {

    private val selectedTags = mutableListOf<Tag>()

    override fun bind(viewHolder:GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tagGroup.apply {
                removeAllViews()
                tags.forEach { tag ->
                    val chip = TagChip(context).apply {
                        text = tag.getTagName()
                        isCheckable = true
                        isChecked = selectedTags.contains(tag)
                        setOnCheckedChangeListener { _, isChecked ->
                            val changed = if (isChecked) selectedTags.add(tag)
                            else selectedTags.remove(tag)
                            if (changed) onSelectedChange(selectedTags)
                        }
                    }

                    addView(chip)
                }
            }
        }
    }

    override fun getLayout() = R.layout.item_tags_horizontal_list
}