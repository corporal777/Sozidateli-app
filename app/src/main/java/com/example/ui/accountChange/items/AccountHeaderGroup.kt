package com.example.ui.accountChange.items

import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class AccountHeaderGroup () : NestedGroup() {


    private val headerItem = UnLoggedAccountsHeader()

    init {
        headerItem.registerGroupDataObserver(this)
        //dataItem.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> headerItem
            // 1 -> dataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            headerItem -> 0
            //dataItem -> 1
            else -> -1
        }
    }

    override fun getGroupCount() = 1
}