package com.example.holders

class ProfileExpandableSubtitleGroup(subtitle: String, onExpandChange: OnExpandChange<ProfileExpandableSubtitleItem>)
    : ExpandableTitleGroup<ProfileExpandableSubtitleItem>(ProfileExpandableSubtitleItem(subtitle), onExpandChange)