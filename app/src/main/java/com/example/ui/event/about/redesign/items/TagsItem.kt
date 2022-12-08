package com.example.ui.event.about.redesign.items

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.R
import com.example.data.models.Tag
import com.example.databinding.ItemEventDetailTagsBlockBinding
import com.example.ui.views.TagChipNew
import com.xwray.groupie.databinding.BindableItem
import setOnClickListener

class TagsItem (
    private val listTags : List<Tag>,
    private val onTagClick: (tag : Tag) -> Unit
) : BindableItem<ItemEventDetailTagsBlockBinding>() {

    private var isExpanded = false
    private lateinit var mBinding : ItemEventDetailTagsBlockBinding

    @SuppressLint("SetTextI18n", "ResourceType")
    override fun bind(viewBinding: ItemEventDetailTagsBlockBinding, position: Int) {
        viewBinding.apply {
            mBinding = viewBinding
            tagGroup.apply {
                removeAllViews()
                if (listTags.size > 6){
                    if (!isExpanded){
                        val shortListTags = listTags.subList(0, 5)
                        val remainSize = listTags.size - shortListTags.size
                        shortListTags.forEach {
                            addView(createChip(context, it))
                        }
                        addView(getRemainTagsChip(context, remainSize){
                            isExpanded = true
                            removeAllViews()
                            listTags.forEach {
                                addView(createChip(context, it))
                            }
                        })
                    }else {
                        listTags.forEach {
                            addView(createChip(context, it))
                        }
                    }
                }else {
                    listTags.forEach {
                        addView(createChip(context, it))
                    }
                }
            }
        }
    }

    private fun getRemainTagsChip(context : Context, size : Int, block : () -> Unit): TagChipNew{
        return TagChipNew(context).apply {
            id = size
            text = "Еще $size "
            isChecked = false
            isClickable = true
            val img: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_arrow_down_for_tags)
            setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
            setOnClickListener {
                block.invoke()
                visibility = View.GONE
            }
        }
    }

    private fun createChip(context : Context, tag: Tag): TagChipNew {
        return TagChipNew(context).apply {
            id = tag.id.toInt()
            text = tag.name
            isChecked = tag.isSelected
            isClickable = true
            setOnClickListener {
                tag.isSelected = true
                onTagClick.invoke(tag)
            }
        }
    }

    fun updateTag(tag: Tag){
        if (this::mBinding.isInitialized){
            val item = mBinding.tagGroup.findViewById<TagChipNew>(tag.id.toInt())
            item.isChecked = tag.isSelected
        }
    }


    override fun getLayout(): Int = R.layout.item_event_detail_tags_block
}