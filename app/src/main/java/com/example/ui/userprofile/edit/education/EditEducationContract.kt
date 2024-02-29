package com.example.ui.userprofile.edit.education

import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationLevel
import com.example.data.models.EducationModel
import com.example.data.models.ToggleIntModel
import com.example.data.models.UserDetail
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface EditEducationContract {
    interface View : BaseContract.View {
        @OneExecution
        fun setEducationData(user: UserDetail)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSaveEducationClick(
            educationLevel: ToggleIntModel?,
            educationsList: List<EducationModel>?,
            degree: List<AcademicDegreeModel>?
        )
    }
}