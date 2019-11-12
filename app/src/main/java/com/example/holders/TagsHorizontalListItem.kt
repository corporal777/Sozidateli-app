package com.example.holders

import com.example.R
import com.example.data.models.Tag
import com.example.ui.views.TagChip
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_tags_horizontal_list.*
import setOnClickListener

class TagsHorizontalListItem(
        private val tags: List<Tag>,
        private val onSelectedChange: () -> Unit,
        private val onShowAllClick: () -> Unit
) : Item() {

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {
            tagGroup.apply {
                removeAllViews()
                tags.forEach { tag ->
                    val chip = TagChip(context).apply {
                        text = tag.name
                        isCheckable = true
                        isChecked = tag.isSelected
                        setOnCheckedChangeListener { _, isChecked ->
                            tag.isSelected = isChecked
                            onSelectedChange()
                        }
                    }

                    addView(chip)
                }
            }

            btnShowAllTags.setOnClickListener(onShowAllClick)
        }
    }

    override fun getLayout() = R.layout.item_tags_horizontal_list
}