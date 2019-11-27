package com.example.holders

import com.example.R
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_action_button_small.*

class ActionButtonItem(
        id: Long,
        private val action: Int,
        private val addClickListener: () -> Unit
) : Item(id) {

    var isEnabled = true

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.btnAction.apply {
            setOnClickListener { addClickListener() }
            val actionText: Int
            val actionIcon: Int
            when (action) {
                ACTION_ADD_RECORD -> {
                    actionText = R.string.add_record
                    actionIcon = R.drawable.ic_add_sn
                }
                ACTION_ADD_FILE -> {
                    actionText = R.string.add_file
                    actionIcon = R.drawable.ic_add_sn
                }
                ACTION_SHOW_ON_MAP -> {
                    actionText = R.string.event_contacts_watch_on_map
                    actionIcon = R.drawable.ic_location
                }
                ACTION_EVENT_REQUEST -> {
                    actionText = R.string.event_register_request
                    actionIcon = 0
                }
                ACTION_SEND -> {
                    actionText = R.string.send
                    actionIcon = 0
                }
                else -> {
                    actionText = R.string.add_record
                    actionIcon = R.drawable.ic_add_sn
                }
            }

            text = resources.getString(actionText)
            setCompoundDrawablesWithIntrinsicBounds(actionIcon, 0, 0, 0)
            isEnabled = this@ActionButtonItem.isEnabled
        }
    }

    override fun getLayout() = when (action) {
        ACTION_ADD_RECORD -> R.layout.item_action_button_small
        ACTION_ADD_FILE -> R.layout.item_action_button_small
        ACTION_SHOW_ON_MAP -> R.layout.item_action_button_middle
        else -> R.layout.item_action_button
    }

    override fun hasSameContentAs(other: com.xwray.groupie.Item<*>?): Boolean {
        if (this === other) return true
        if (other !is ActionButtonItem) return false

        if (action != other.action) return false
        if (isEnabled != other.isEnabled) return false

        return true
    }

    companion object {
        const val ACTION_ADD_RECORD = 0
        const val ACTION_ADD_FILE = 1
        const val ACTION_SHOW_ON_MAP = 2
        const val ACTION_EVENT_REQUEST = 3
        const val ACTION_SEND = 4
    }
}