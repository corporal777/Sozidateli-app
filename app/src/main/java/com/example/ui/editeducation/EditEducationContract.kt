package com.example.ui.editeducation

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.ui.base.BaseContract

interface EditEducationContract {
    interface View : BaseContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEducationData(user: UserDetail)

        @StateStrategyType(SkipStrategy::class)
        fun buttonSaveEnabled(enable : Boolean)
    }
    interface Presenter : BaseContract.Presenter {
        fun onSaveEducationClick(educationLevel: ToggleIntModel?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?)
    }
}