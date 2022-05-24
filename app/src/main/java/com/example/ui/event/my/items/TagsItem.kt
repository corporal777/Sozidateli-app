package com.example.ui.event.my.items

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import com.example.R
import com.example.data.models.MyEventsFilter
import com.example.data.models.NewTags
import com.example.data.models.Tag
import com.example.data.models.Tags
import com.example.ui.views.TagChipNew
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_my_events_tags.*
import kotlinx.android.synthetic.main.item_tags_horizontal_list.*

class TagsItem(
    private val tags: List<Tag>,
    private val onSelectedChange: (filter : MyEventsFilter) -> Unit
) : Item() {

    @SuppressLint("ResourceAsColor", "ResourceType")
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            tagsGroup.apply {
                removeAllViews()

                val chip: (Tag) -> CompoundButton = { tag ->
                    TagChipNew(context).apply {
                        id = tag.id.toInt()
                        text = tag.name
                        isChecked = tag.isSelected

                        setOnCheckedChangeListener { _, isChecked ->
                            tag.isSelected = isChecked
                            Log.e("STATE", tag.id)
                            if (tag.isSelected){
                                onSelectedChange.invoke(getFilter(tag.id.toInt()))
                            }else {
                                onSelectedChange.invoke(getFilter(0))
                            }

                        }
                    }
                }

                tags.forEach { tag ->
                    addView(chip(tag))
                }
            }
        }
    }

    private fun getFilter(id : Int): MyEventsFilter{
        var filter = MyEventsFilter.NONE
        when(id) {
            1 -> {
                filter = MyEventsFilter.ACCEPTED
            }
            2 -> {
                filter = MyEventsFilter.PENDING
            }
            3 -> {
                filter = MyEventsFilter.DECLINED
            }else -> MyEventsFilter.NONE
        }
        return filter
    }

    override fun getLayout() = R.layout.item_my_events_tags
}