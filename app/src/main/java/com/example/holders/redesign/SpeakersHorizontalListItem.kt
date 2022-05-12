package com.example.holders.redesign

import android.graphics.Color
import com.example.data.models.MemberModel
import com.example.holders.HorizontalListItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class SpeakersHorizontalListItem(
    data: List<MemberModel>?,
    val onItemClick: (id: Int) -> Unit
) : HorizontalListItem<GroupieViewHolder>() {


    val items = data?.filter { it.role == "speaker" }?.map {
        EventSpeakerItem(
            it.binds?.user?.id ?: 0,
            it.binds?.user?.name + "\n" + it.binds?.user?.lastName,
            it.binds?.user?.image?.uri ?: ""
        ) { id -> onItemClick(id) }
    }

    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply {
            if (items != null){
                addAll(items)
            }
            backgroundColor = Color.WHITE
        }
    }

}