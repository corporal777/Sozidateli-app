package com.example.ui.userprofile.read.experience

import com.example.data.models.AcademicDegreeModel
import com.example.data.models.EducationModel
import com.example.data.models.UserDetail
import com.example.data.models.WorkExperience
import com.example.ui.userprofile.base.BaseUserProfileContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserProfileExperienceContract {
    interface View : BaseUserProfileContract.View {

        @Skip
        override fun setUserData(user: UserDetail, state: String) = Unit

        @AddToEndSingle
        fun setUserExperience(workExperience: List<WorkExperience>)

        @OneExecution
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter {
        fun onEditClick()
    }
}
