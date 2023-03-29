package com.example.ui.state.maxNew.education

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.ToggleIntModel
import com.example.data.models.UserDetail
import com.example.ui.state.maxNew.base.BaseMaxStateContract

interface MaxStatusEducationContract {
    interface View : BaseMaxStateContract.View {
        @StateStrategyType(OneExecutionStateStrategy::class)
        fun setEducationData(user: UserDetail)
    }
    interface Presenter : BaseMaxStateContract.Presenter {
        fun onSaveEducationClick(educationLevel: ToggleIntModel?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?)
    }
}