package com.example.ui.state.maxNew.education

import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.ToggleIntModel
import com.example.data.models.UserDetail
import com.example.ui.state.maxNew.base.BaseMaxStateContract
import moxy.viewstate.strategy.alias.OneExecution

interface MaxStatusEducationContract {
    interface View : BaseMaxStateContract.View {
        @OneExecution
        fun setEducationData(user: UserDetail)
    }
    interface Presenter : BaseMaxStateContract.Presenter {
        fun onSaveEducationClick(educationLevel: ToggleIntModel?, educationsList: List<EducationModel>?, degree: List<AcademicDegreeModel>?)
    }
}