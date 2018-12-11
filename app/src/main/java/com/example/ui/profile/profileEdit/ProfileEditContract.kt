package com.example.ui.profile.profileEdit

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.ProfileField
import com.example.data.models.User
import com.example.data.models.UserChat
import com.example.ui.base.BaseContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

interface ProfileEditContract {
    interface View : BaseContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: User)
    }

    interface Presenter : BaseContract.Presenter{
        fun onSaveClick(groupAdapter: GroupAdapter<ViewHolder>)
    }
}
