package com.example.ui.chat

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserChatMessage
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface ChatContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun iniChatAdapter(query: Query, parser: SnapshotParser<UserChatMessage>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun clearMessageInput()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun scrollToLastPosition()

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setChatLabel(label: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSendTextMessageClick(message: String)
        fun onNewMessage()
    }
}
