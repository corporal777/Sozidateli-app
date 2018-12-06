package com.example.ui.about

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface AboutContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showText(text:String)
    }

    interface Presenter : BaseContract.Presenter{
    }
}
