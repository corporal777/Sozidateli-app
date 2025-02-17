package com.example.ui.event.about.items

import android.view.View
import com.example.app.R
import com.example.app.databinding.ItemSpeakersHorizontalListBinding
import com.example.data.models.MemberModel
import com.example.holders.redesign.EventSpeakerItem
import com.example.holders.redesign.ShowAllSpeakersItem
import com.xwray.groupie.GroupieAdapter
import com.xwray.groupie.viewbinding.BindableItem


class SpeakersHorizontalListItem(
    private val data: List<MemberModel>,
    private val canShowMore: Boolean,
    private val onItemClick: (id: Int) -> Unit,
    private val showAllSpeakers: () -> Unit
) : BindableItem<ItemSpeakersHorizontalListBinding>(-1006L) {

    private val groupAdapter by lazy {
        GroupieAdapter().apply {
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

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>): Boolean {
        if (other !is SpeakersHorizontalListItem) return false
        if (data != other.data) return false
        return true
    }

    override fun initializeViewBinding(view: View) = ItemSpeakersHorizontalListBinding.bind(view)
    override fun getLayout(): Int = R.layout.item_speakers_horizontal_list

}