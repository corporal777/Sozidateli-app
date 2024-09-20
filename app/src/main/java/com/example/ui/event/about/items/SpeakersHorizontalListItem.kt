package com.example.ui.event.about.items

import android.graphics.Color
import android.util.Log
import com.example.R
import com.example.data.models.MemberModel
import com.example.databinding.ItemSpeakersHorizontalListBinding
import com.example.holders.HorizontalListItem
import com.example.holders.redesign.EventActivityItem
import com.example.holders.redesign.EventSpeakerItem
import com.example.holders.redesign.ShowAllSpeakersItem
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder

class SpeakersHorizontalListItem(
    private val data: List<MemberModel>,
    private val canShowMore: Boolean,
    private val onItemClick: (id: Int) -> Unit,
    private val showAllSpeakers: () -> Unit
) : BindableItem<ItemSpeakersHorizontalListBinding>(-1006L) {

    private val groupAdapter by lazy {
        GroupAdapter<GroupieViewHolder>().apply {
            update(
                data.map {
                    EventSpeakerItem(
                        it.id,
                        it.memberNameLastName,
                        it.memberImage,
                        it.getSpeakerStatus()
                    ) { id -> onItemClick(id) }
                }
            )
            if (canShowMore) add(ShowAllSpeakersItem { showAllSpeakers.invoke() })
        }
    }

    override fun bind(viewBinding: ItemSpeakersHorizontalListBinding, position: Int) {
        viewBinding.apply {
            recyclerView.adapter = groupAdapter
        }
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (other !is SpeakersHorizontalListItem) return false
        if (data != other.data) return false
        return true
    }

    override fun getLayout(): Int = R.layout.item_speakers_horizontal_list

}