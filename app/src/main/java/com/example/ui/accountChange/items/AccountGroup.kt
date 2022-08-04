package com.example.ui.accountChange.items

import com.example.data.models.EventNew
import com.example.data.models.UserDetail
import com.example.data.models.UserSessionModel
import com.example.holders.redesign.EventItemNew
import com.xwray.groupie.Group
import com.xwray.groupie.NestedGroup

class AccountGroup (
    val canShow : Boolean,
    val session : UserSessionModel,
    val currentAccountId: String,
    val user: UserDetail,
    val onMenuClick: (user: UserDetail) -> Unit,
    val onAccountClick: (user: UserDetail) -> Unit
) : NestedGroup() {


    private val accountItem = AccountItem(
        canShow,
        session,
        currentAccountId,
        user,
        onMenuClick,
        onAccountClick
    )

    init {
        accountItem.registerGroupDataObserver(this)
        //dataItem.registerGroupDataObserver(this)
    }

    override fun getGroup(position: Int): Group {
        return when (position) {
            0 -> accountItem
            // 1 -> dataItem
            else -> throw IndexOutOfBoundsException("Invalid item position: $position")
        }
    }

    override fun getPosition(group: Group): Int {
        return when (group) {
            accountItem -> 0
            //dataItem -> 1
            else -> -1
        }
    }

    fun updateState(id : Long?) : Boolean{
        return accountItem.id == id
    }

    fun updateMenuAction(state : Boolean) {
        accountItem.notifyChanged(state)
    }

    override fun getGroupCount() = 1
}