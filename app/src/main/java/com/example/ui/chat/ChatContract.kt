package com.example.ui.chat

import com.example.data.models.ChatMessage
import com.example.data.models.UserChatMessage
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface ChatContract {
    interface View : BaseContract.View {
        fun iniChatAdapter(query: Query, parser: SnapshotParser<UserChatMessage>)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSendTextMessageClick(message: String)
    }
}
