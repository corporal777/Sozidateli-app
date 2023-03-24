package com.example.ui.event.about.items

import android.graphics.Color
import com.example.data.models.MemberModel
import com.example.holders.HorizontalListItem
import com.example.holders.redesign.EventSpeakerItem
import com.example.holders.redesign.ShowAllSpeakersItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class SpeakersHorizontalListItem(
    val data: List<MemberModel>,
    val canShowMore: Boolean,
    val onItemClick: (id: Int) -> Unit,
    val showAllSpeakers: () -> Unit
) : HorizontalListItem<GroupieViewHolder>() {

    val items = arrayListOf<Item<*>>().apply {
        addAll(data.map {
            EventSpeakerItem(
                it.id ?: 0,
                it.binds?.user?.name + "\n" + it.binds?.user?.lastName,
                it.binds?.user?.image?.uri ?: "",
                it.status ?: "",
                it.binds?.user?.state?.isRegistered ?: false
            ) { id -> onItemClick(id) }
        })
        if (canShowMore) {
            add(ShowAllSpeakersItem { showAllSpeakers.invoke() })
        }
    }

    init {
        adapter = GroupAdapter<GroupieViewHolder>().apply {
            addAll(items)
            backgroundColor = Color.WHITE
        }
    }

}