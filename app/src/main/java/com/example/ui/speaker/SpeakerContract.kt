package com.example.ui.speaker

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Speaker
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface SpeakerContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setSpeaker(speaker: Speaker)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun openChat(userName:String,userId:String,chatId:String)
    }

    interface Presenter : BaseContract.Presenter{
        fun onWriteMsgClick()
        fun onAddFavoriteClick()
    }
}
