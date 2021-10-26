package com.example.ui.userprofile.editfile

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class UserEditFilePresenter
    @Inject constructor(
            private val appData: AppData
    )
    : BasePresenter<UserEditFileContract.View>(appData), UserEditFileContract.Presenter {


}