package com.example.ui.profile.profileFull

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.User
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface ProfileFullContract {
    interface View : BaseContract.View{
        fun setUser(user: User)
    }

    interface Presenter : BaseContract.Presenter{
    }
}
