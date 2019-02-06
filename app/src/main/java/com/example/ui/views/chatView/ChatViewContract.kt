package com.example.ui.views.chatView

import com.example.ui.base.BaseContract

class ChatViewContract {

    interface View : BaseContract.View {
        fun setChatCount(count:Int)
    }

    interface Presenter : BaseContract.Presenter {

    }
}