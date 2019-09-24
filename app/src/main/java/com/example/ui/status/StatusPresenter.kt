package com.example.ui.status

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.user.User
import com.example.ui.base.BasePresenter
import javax.inject.Inject

@InjectViewState
class StatusPresenter
@Inject constructor(
        private val appData: AppData
) : BasePresenter<StatusContract.View>(), StatusContract.Presenter {

    lateinit var status: User.Status

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setStatus(status, status == appData.getUser().user_status, null, false)
    }
}
