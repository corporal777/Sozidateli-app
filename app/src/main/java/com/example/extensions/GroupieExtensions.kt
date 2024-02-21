package com.example.extensions

import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import com.xwray.groupie.NestedGroup
import com.xwray.groupie.Section

inline fun <reified I : Item<*>> GroupAdapter<GroupieViewHolder>.findItem(position : Int): I? {
    if (this.getItem(position) !is I) return null
    else return this.getItem(position) as I
}

inline fun <VH : GroupieViewHolder, reified I : Item<*>> GroupAdapter<VH>.findItemBy(selector: (I) -> Boolean): I? {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        if (item is I && selector(item)) return item
    }
    return null
}


inline fun <reified I : Item<*>> GroupAdapter<GroupieViewHolder>.findItemByShort(selector: (I) -> Boolean): I? {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        if (item is I && selector(item)) return item
    }
    return null
}

inline fun Group.forEachItems(selector: (Item<*>, position: Int) -> Unit) {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        selector(item, i)
    }
}

inline fun <VH : GroupieViewHolder, reified I : NestedGroup> GroupAdapter<VH>.findGroupBy(selector: (I) -> Boolean): I? {
    for (i in 0 until this.groupCount) {
        val group = getGroup(i)
        if (group is I && selector(group)) return group
    }

    return null
}

inline fun <reified I : NestedGroup> GroupAdapter<com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder>.findGroup(selector: (I) -> Boolean): I? {
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

inline fun <reified I : Group> Group.forEachGroups(selector: (I) -> Unit) {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        if (item is I) selector(item)
    }
}

inline fun <reified I : Group> Group.forEachGroupsPos(selector: (I, position: Int) -> Unit) {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        if (item is I) selector(item, i)
    }
}


fun Section.updateItem(item: Item<*>?){
    if (item == null) update(emptyList())
    else update(listOf(item))
}

fun Section.updateItems(item: Item<*>?, list: List<Group>?){
    update(listOfNotNull(item).plus(list ?: emptyList()))
}

fun Section.updateItems(vararg item: Item<*>?){
    update(item.toList())
}

fun Section.updateGroup(item: Group?){
    if (item != null) update(listOf(item))
}

fun <VH : GroupieViewHolder> GroupAdapter<VH>.updateItem(item: Item<*>?) {
    if (item == null) update(emptyList())
    else update(listOf(item))
}

fun <VH : GroupieViewHolder> GroupAdapter<VH>.updateItems(item: Item<*>?, list: List<Item<*>>) {
    if (item == null) update(emptyList())
    else update(listOf(item).plus(list))
}

