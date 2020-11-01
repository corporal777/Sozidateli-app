package com.example.ui.userprofile.read.experience

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import javax.inject.Inject

@InjectViewState
class UserProfileExperiencePresenter @Inject constructor(
        appData: AppData
) : BaseUserProfilePresenter<UserProfileExperienceContract.View>(appData), UserProfileExperienceContract.Presenter {

    override fun onEditClick() = viewState.showEdit()
}
