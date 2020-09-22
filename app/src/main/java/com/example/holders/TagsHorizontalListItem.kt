package com.example.holders

import androidx.core.view.isVisible
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
            btnMoreTags.apply {
                setOnClickListener(onShowAllClick)
                isVisible = tags.size >= 3
            }
            tagGroup.apply {
                removeAllViews()

//                LayoutInflater.from(context).inflate(R.layout.layout_tag_button, this, true).apply {
//                    findViewById<Button>(R.id.btnTag).apply {
//                        text = context.getText(R.string.schedule_show_all_tags)
//                        setOnClickListener(onShowAllClick)
//                    }
//                }

                tags.forEach { tag ->
                    val chip = TagChip(context).apply {
                        text = tag.name
                        isChecked = tag.isSelected
                        setOnCheckedChangeListener { _, isChecked ->
                            tag.isSelected = isChecked
                            onSelectedChange()
                        }
                    }

                    addView(chip)
                }
            }
        }
    }

    override fun getLayout() = R.layout.item_tags_horizontal_list
}