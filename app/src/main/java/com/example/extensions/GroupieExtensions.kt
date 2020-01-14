package com.example.extensions

import com.xwray.groupie.*

inline fun <VH : GroupieViewHolder, reified I : Item<*>> GroupAdapter<VH>.findItemBy(selector: (I) -> Boolean): I? {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        if (item is I && selector(item)) return item
    }

    return null
}

inline fun <VH : GroupieViewHolder, reified I : NestedGroup> GroupAdapter<VH>.findGroupBy(selector: (I) -> Boolean): I? {
    for (i in 0 until this.groupCount) {
        val group = getGroup(i)
        if (group is I && selector(group)) return group
    }

    return null
}

inline fun <VH : GroupieViewHolder, reified I : Item<*>> GroupAdapter<VH>.findItemAdapterPositionBy(selector: (I) -> Boolean): Int {
    val item = this.findItemBy(selector)
    return if (item == null) -1 else this.getAdapterPosition(item)
}

inline fun <VH : GroupieViewHolder> GroupAdapter<VH>.forEachItems(selector: (Item<*>) -> Unit) {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        selector(item)
    }
}

inline fun <VH : GroupieViewHolder> GroupAdapter<VH>.forEachGroups(selector: (Group) -> Unit) {
    for (i in 0 until groupCount) selector(getGroup(i))
}

inline fun <reified I : Item<*>> Group.findItemBy(selector: (I) -> Boolean): I? {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        if (item is I && selector(item)) return item
    }

    return null
}

inline fun <reified I : Group> NestedGroup.findGroupBy(selector: (I) -> Boolean): I? {
    for (i in 0 until groupCount) {
        val group = getGroup(i)
        if (group is I && selector(group)) return group
    }

    return null
}

inline fun Group.forEachItems(selector: (Item<*>) -> Unit) {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        selector(item)
    }
}

inline fun <reified I : Group> Group.forEachGroups(selector: (I) -> Unit) {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        if (item is I) selector(item)
    }
}