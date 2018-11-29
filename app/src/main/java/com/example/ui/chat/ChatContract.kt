package com.example.ui.chat

import com.example.ui.base.BaseContract
import com.google.firebase.firestore.Query

interface ChatContract {
    interface View : BaseContract.View {
        fun iniChatAdapter(query: Query)
    }

    interface Presenter : BaseContract.Presenter
}
