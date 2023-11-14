package com.example.ui.userprofile.read.education

import com.example.data.AppData
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class UserProfileEducationPresenter @Inject constructor(
        appData: AppData
) : BaseUserProfilePresenter<UserProfileEducationContract.View>(appData), UserProfileEducationContract.Presenter {

    override fun onEditClick() = viewState.showEdit()

    override fun attachView(view: UserProfileEducationContract.View?) {
        super.attachView(view)

    }
}
