package com.example.holders

class ProfileExpandableSubtitleGroup(
        subtitle: String,
        isInitiallyExpanded: Boolean = false,
        onExpandChange: OnExpandChange<ProfileExpandableSubtitleItem>
) : ExpandableTitleGroup<ProfileExpandableSubtitleItem>(ProfileExpandableSubtitleItem(subtitle), isInitiallyExpanded, onExpandChange)