package com.example.holders

class ProfileExpandableTitleGroup(subtitle: String, onExpandChange: OnExpandChange<ProfileExpandableTitleItem>)
    : ExpandableTitleGroup<ProfileExpandableTitleItem>(ProfileExpandableTitleItem(subtitle), onExpandChange)