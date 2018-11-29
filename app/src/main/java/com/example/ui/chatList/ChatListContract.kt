package com.example.ui.chatList

import com.example.ui.base.BaseContract

interface ChatListContract {
    interface View : BaseContract.View{
        fun setData()
    }

    interface Presenter : BaseContract.Presenter{

    }
}
