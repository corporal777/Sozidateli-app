package com.example.ui.state.maxNew.work

import com.example.data.models.UserDetail
import com.example.data.models.WorkExperienceServerModel
import com.example.ui.state.maxNew.base.BaseMaxStateContract
import moxy.viewstate.strategy.alias.OneExecution

interface MaxStatusWorkContract {
    interface View : BaseMaxStateContract.View {
        @OneExecution
        fun setWorkData(user: UserDetail)
    }
    interface Presenter : BaseMaxStateContract.Presenter {
        fun onSaveWorkClick(data: WorkExperienceServerModel)
    }
}