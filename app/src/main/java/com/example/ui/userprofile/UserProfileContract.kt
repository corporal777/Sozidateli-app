package com.example.ui.userprofile

import com.example.ui.userprofile.base.BaseUserProfileContract
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface UserProfileContract {

    interface View : BaseUserProfileContract.View {

        @Skip
        fun showTakePictureChooser(canRemove: Boolean, isBase: Boolean, isMax: Boolean)

        @OneExecution
        fun showMainData()

        @OneExecution
        fun showContacts()

        @OneExecution
        fun showInterests()

        @OneExecution
        fun showEducation()

        @OneExecution
        fun showExperience()

        @OneExecution
        fun showEdit()
    }

    interface Presenter : BaseUserProfileContract.Presenter {

        fun onEditAvatarClick()
        fun onMainDataClick()
        fun onContactsClick()
        fun onInterestsClick()
        fun onEducationClick()
        fun onExperienceClick()
    }
}
