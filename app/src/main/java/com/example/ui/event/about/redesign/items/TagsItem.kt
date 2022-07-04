package com.example.ui.event.about.redesign.items

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CompoundButton
import com.example.R
import com.example.data.models.Tag
import com.example.databinding.ItemEventDetailTagsBlockBinding
import com.example.ui.views.TagChipNew
import com.xwray.groupie.databinding.BindableItem

class TagsItem (
    private val listTags : List<Tag>,
    private val onTagClick: (tag : Tag) -> Unit
) : BindableItem<ItemEventDetailTagsBlockBinding>() {

    private var isExpanded = false

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun bind(viewBinding: ItemEventDetailTagsBlockBinding, position: Int) {
        viewBinding.apply {
            tagGroup.apply {
                val createChip: (Tag) -> CompoundButton = { tag ->
                    TagChipNew(context).apply {
                        id = tag.id.toInt()
                        text = tag.name
                        isChecked = tag.isSelected
                        isClickable = true
                        setOnCheckedChangeListener { _, isChecked ->
                            tag.isSelected = isChecked
                            onTagClick.invoke(tag)
                        }
                    }
                }

                removeAllViews()

                if (!listTags.isNullOrEmpty()) {
                    if (listTags.size > 6) {
                        if (!isExpanded){
                            val otherSize = listTags.size - 5
                            for (i in listTags.indices) {
                                listTags[i].apply {
                                    addView(createChip(this))
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
                                    isExpanded = true
                                    for (i in 6 until listTags.size) {
                                        listTags[i].apply {
                                            addView(createChip(this))
                                        }
                                    }
                                    visibility = View.GONE
                                }

                            })
                        }else {
                            listTags.forEach {
                                addView(createChip(it))
                            }
                        }

                    } else {
                        listTags.forEach {
                            addView(createChip(it))
                        }
                    }
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_event_detail_tags_block
}