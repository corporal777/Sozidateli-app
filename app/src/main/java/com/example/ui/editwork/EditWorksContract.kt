package com.example.ui.editwork


import com.example.data.models.UserDetail
import com.example.data.models.WorkExperienceServerModel
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EditWorksContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setWorkData(user: UserDetail)

        @Skip
        fun showUpdateError(message: String? = null)

        @Skip
        fun buttonSaveEnabled(enable : Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSaveWorkClick(data: WorkExperienceServerModel)
    }
}