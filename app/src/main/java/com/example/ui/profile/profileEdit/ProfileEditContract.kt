package com.example.ui.profile.profileEdit

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.user.User
import com.example.ui.base.BaseContract
import com.example.ui.base.takePhoto.TakePhotoContract
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder

interface ProfileEditContract {
    interface View : TakePhotoContract.View{
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setUser(user: User)
    }

    interface Presenter : BaseContract.Presenter{
        fun onSaveClick(groupAdapter: GroupAdapter<ViewHolder>)
    }
}
