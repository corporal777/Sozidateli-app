package com.example.ui.profile.profileFull

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ProfileFullPresenter
@Inject constructor(private val appData: AppData
) : BasePresenter<ProfileFullContract.View>(), ProfileFullContract.Presenter {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.onUserChange
                .performOnBackgroundOutOnMain()
                .subscribe({
                    it.value?.let { viewState::setUser }
                }, {})
                .call(compositeDisposable)
    }

    override fun attachView(view: ProfileFullContract.View?) {
        super.attachView(view)
        viewState.setUser(appData.getUser())
    }

    override fun onEditClick() {
        viewState.showEditProfile()
    }
}
