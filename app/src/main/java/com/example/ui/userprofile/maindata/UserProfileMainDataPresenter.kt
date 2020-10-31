package com.example.ui.userprofile.maindata

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import javax.inject.Inject

@InjectViewState
class UserProfileMainDataPresenter @Inject constructor(
        appData: AppData
) : BaseUserProfilePresenter<UserProfileMainDataContract.View>(appData), UserProfileMainDataContract.Presenter {
    override fun onEditClick() = viewState.showEdit()
}
