package com.example.holders

import com.example.data.models.MemberModel
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

open class SpeakerGroup(
        private var speaker: MemberModel,
        private val onSpeakerClick: (MemberModel) -> Unit,
        private val onFavoriteChangeClick: (MemberModel) -> Unit
) : NestedGroup() {

    private val userItem = UserItem(
            speaker.binds?.user?.id?: 0,
            speaker.binds?.user?.fullName?: "",
            speaker.binds?.user?.address?.city,
            speaker.binds?.user?.image?.uri,
            { onSpeakerClick(speaker) },
            //speaker.user.getUserSubscribeAction(),
            { onFavoriteChangeClick(speaker) }
    ).apply {
        registerGroupDataObserver(this@SpeakerGroup)
    }

    private val descriptionItem: SpeakerDescriptionItem?

    init {
        val description = "speaker.description"
        descriptionItem = if (!description.isNullOrBlank()) SpeakerDescriptionItem(-(speaker.user?.toLong()?:0), description)
        else null
    }

    override fun getGroup(position: Int): Group {
        return when {
            position == 0 -> userItem
            position == 1 && descriptionItem != null -> descriptionItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            userItem -> 0
            descriptionItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = if (descriptionItem == null) 1 else 2
}