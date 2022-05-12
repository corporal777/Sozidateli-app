package com.example.holders.redesign

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.R
import com.example.data.models.EventTagModel
import com.example.databinding.ItemEventDetailTagsBlockBinding
import com.example.ui.views.TagChip
import com.example.ui.views.TagChipNew
import com.google.android.material.chip.Chip
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.item_event_detail_tags_block.*
import kotlinx.android.synthetic.main.item_sub_event.*


class EventTagsListItem(
    private val list: List<EventTagModel>,
    private val onTagCLick: (id: Int) -> Unit
) : Item() {


    @SuppressLint("ResourceType")
    @RequiresApi(Build.VERSION_CODES.M)
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.apply {

            chip_group.apply {
                val createChip: (EventTagModel) -> CompoundButton = {
                    TagChipNew(context).apply {
                        id = it.id
                        text = it.name
                        isChecked = true
                        isClickable = true
                        setOnClickListener {
                            onTagCLick(id)
                        }
                    }
                }


                removeAllViews()

                if (list.size > 6) {
                    val otherSize = list.size - 5
                    for (i in list.indices) {
                        list[i].apply {
                            addView(createChip(this))
                        }
                        if (i == 5) break
                    }
                    addView(TagChipNew(context).apply {
                        id = 1
                        text = "Еще $otherSize "
                        isChecked = true
                        isClickable = true
                        val img: Drawable =
                            context.resources.getDrawable(R.drawable.ic_arrow_down_for_tags)
                        setCompoundDrawablesWithIntrinsicBounds(null, null, img, null)
                        setOnClickListener {
                            for (i in 6 until list?.size) {
                                list[i].apply {
                                    addView(createChip(this))
                                }
                            }
                            visibility = View.GONE
                        }

                    })
                } else {
                    list.forEach {
                        addView(createChip(it))
                    }
                }
            }


//            val mLayoutManager = FlexboxLayoutManager(viewBinding.root.context)
//            mLayoutManager.flexDirection = FlexDirection.ROW;
//            mLayoutManager.justifyContent = JustifyContent.FLEX_START;
//
//            chipsList.apply {
//                layoutManager = mLayoutManager
//                adapter = groupAdapter
//            }

        }

    }


    @RequiresApi(Build.VERSION_CODES.M)
    private fun createTagChip(context: Context, data: EventTagModel): Chip {
        return Chip(context).apply {

            id = data.id ?: 0
            text = data.name
            chipBackgroundColor = context.getColorStateList(R.color.chip_background_color)
            //setBackgroundColor(16000000)
            setTextAppearance(R.style.CustomTagsTextAppearance_TextAppearance)
            setOnClickListener {
                //onTagCLick(id)
                Toast.makeText(context, id.toString(), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_event_detail_tags_block

}