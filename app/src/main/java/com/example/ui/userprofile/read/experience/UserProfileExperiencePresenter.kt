package com.example.ui.userprofile.read.experience

import com.example.data.AppData
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class UserProfileExperiencePresenter @Inject constructor(
        appData: AppData
) : BaseUserProfilePresenter<UserProfileExperienceContract.View>(appData), UserProfileExperienceContract.Presenter {


    override fun onEditClick() = viewState.showEdit()

    override fun attachView(view: UserProfileExperienceContract.View?) {
        super.attachView(view)
    }
}
