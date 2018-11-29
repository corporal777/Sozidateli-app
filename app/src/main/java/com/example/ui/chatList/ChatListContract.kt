package com.example.ui.chatList

import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface ChatListContract {
    interface View : BaseContract.View{
        fun iniChatAdapter(query: Query)
        fun openChat(userChat: UserChat)
    }

    interface Presenter : BaseContract.Presenter{
        fun onChatClick(userChat: UserChat)
    }
}
