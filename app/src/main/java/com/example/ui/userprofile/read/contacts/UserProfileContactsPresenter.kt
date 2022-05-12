package com.example.ui.userprofile.read.contacts

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import javax.inject.Inject

@InjectViewState
class UserProfileContactsPresenter @Inject constructor(
        appData: AppData
) : BaseUserProfilePresenter<UserProfileContactsContract.View>(appData), UserProfileContactsContract.Presenter {
    override fun onEditClick() = viewState.showEdit()
}
