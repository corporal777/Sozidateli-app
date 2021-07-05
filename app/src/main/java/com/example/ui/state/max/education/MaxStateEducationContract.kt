package com.example.ui.state.max.education

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract

interface MaxStateEducationContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEducationData(user: UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun goToNext()

        @StateStrategyType(SkipStrategy::class)
        fun showUpdateError(message: String? = null)
    }
    interface Presenter : BaseContract.Presenter {
        fun onClickClose()
        fun onSaveEducationClick(educationLevel: Int?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?)
    }
}