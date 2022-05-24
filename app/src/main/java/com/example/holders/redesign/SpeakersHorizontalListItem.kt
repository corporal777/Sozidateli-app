package com.example.holders.redesign

import android.graphics.Color
import com.example.data.models.MemberModel
import com.example.holders.HorizontalListItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class SpeakersHorizontalListItem(
    val data: List<MemberModel>,
    val onItemClick: (id: Int) -> Unit
) : HorizontalListItem<GroupieViewHolder>() {

    var mCount = 5
    val items = arrayListOf<EventSpeakerItem>().apply {
        if (data.size > 5){
            for (i in 0 until 5) {
                data[i].let {
                    add(EventSpeakerItem(
                        it.binds?.user?.id ?: 0,
                        it.binds?.user?.name + "\n" + it.binds?.user?.lastName,
                        it.binds?.user?.image?.uri ?: ""
                    ) { id -> onItemClick(id) })
                }
            }
        }

    }

    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply {

            if (data.size > 5) {
                val showAll: () -> Unit = {
                    for (i in 5 until data.size) {
                        data[i].let {
                            items.add(EventSpeakerItem(
                                it.binds?.user?.id ?: 0,
                                it.binds?.user?.name + "\n" + it.binds?.user?.lastName,
                                it.binds?.user?.image?.uri ?: ""
                            ) { id -> onItemClick(id) })
                        }
                    }
                    this.clear()
                    addAll(items)
                }
                addAll(items)
                add(ShowAllSpeakersItem{showAll.invoke()})
            }else {
                addAll(items)
            }
            backgroundColor = Color.WHITE
        }


    }

}