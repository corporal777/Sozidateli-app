package com.example.ui.userprofile.read.education

import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationLevel
import com.example.data.models.EducationLevelModel
import com.example.data.models.EducationModel
import com.example.data.models.UserDetail
import com.example.ui.userprofile.base.BaseUserProfileContract
import com.example.ui.views.educationlist.EducationChangeModel
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserProfileEducationContract {
    interface View : BaseUserProfileContract.View {

        @Skip
        override fun setUserData(user: UserDetail, state: String) = Unit

        @AddToEndSingle
        fun setUserEducation(
            userEducationLevel: String,
            userAcademicDegrees: List<AcademicDegreeModel>,
            userEducation : List<EducationModel>
        )

        @OneExecution
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onEditClick()
    }
}
