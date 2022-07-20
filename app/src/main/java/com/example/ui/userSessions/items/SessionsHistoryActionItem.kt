package com.example.ui.userSessions.items

import android.widget.TextView
import com.example.R
import com.example.databinding.ItemSessionsHistoryActionBinding
import com.xwray.groupie.databinding.BindableItem
import setOnClickListener

class SessionsHistoryActionItem(
    val action: SessionsAction,
    val onActionClick: (action: SessionsAction) -> Unit
) :
    BindableItem<ItemSessionsHistoryActionBinding>() {

    private var actionType = action

    override fun bind(viewBinding: ItemSessionsHistoryActionBinding, position: Int) {
        viewBinding.apply {
            decorActionButton(tvAction, actionType)
            tvAction.setOnClickListener {
                actionType = if (actionType == SessionsAction.HIDDEN) {
                    SessionsAction.SHOWN
                } else {
                    SessionsAction.HIDDEN
                }
                onActionClick(actionType)
                decorActionButton(tvAction, actionType)
            }
        }
    }

    private fun decorActionButton(
        textView: TextView,
        action: SessionsAction
    ) {
        textView.apply {
            text = when (action) {
                SessionsAction.HIDDEN -> {
                    context.getString(R.string.show_all_sessions_history)
                }
                SessionsAction.SHOWN -> {
                    context.getString(R.string.hide_all_sessions_history)
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_sessions_history_action
}

enum class SessionsAction {
    SHOWN, HIDDEN
}