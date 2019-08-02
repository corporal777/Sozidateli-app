package com.example.holders

import com.xwray.groupie.ExpandableGroup

class ProfileExpandableTitleGroup(title: String) : ExpandableGroup(ProfileExpandableTitleItem(title)) {

    private val profileExpandableTitleItem: ProfileExpandableTitleItem = getGroup(0) as ProfileExpandableTitleItem

    override fun onToggleExpanded() {
        super.onToggleExpanded()
        profileExpandableTitleItem.notifyChanged(isExpanded)
    }
}