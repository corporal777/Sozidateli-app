package com.example.extensions

import com.xwray.groupie.Group
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

inline fun <VH : ViewHolder, reified I : Item<*>> GroupAdapter<VH>.findItemBy(selector: (I) -> Boolean): I? {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        if (item is I && selector(item)) return item
    }

    return null
}

inline fun <VH : ViewHolder, reified I : Item<*>> GroupAdapter<VH>.findItemAdapterPositionBy(selector: (I) -> Boolean): Int {
    val item = this.findItemBy(selector)
    return if (item == null) -1 else this.getAdapterPosition(item)
}

inline fun <VH : ViewHolder> GroupAdapter<VH>.forEachItems(selector: (Item<*>) -> Unit) {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        selector(item)
    }
}

inline fun <reified I : Item<*>> Group.findItemBy(selector: (I) -> Boolean): I? {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        if (item is I && selector(item)) return item
    }

    return null
}

inline fun Group.forEachItems(selector: (Item<*>) -> Unit) {
    for (i in 0 until this.itemCount) {
        val item = this.getItem(i)
        selector(item)
    }
}