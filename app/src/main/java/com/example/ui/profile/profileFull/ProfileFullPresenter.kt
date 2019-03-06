package com.example.ui.profile.profileFull

import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ProfileFullPresenter
@Inject constructor(private val appData: AppData,
                    private val userRepository: UserRepository
) : BasePresenter<ProfileFullContract.View>(), ProfileFullContract.Presenter {
    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        appData.onUserChange
                .performOnBackgroundOutOnMain()
                .subscribe({
                    it.value?.let { viewState::setUser }
                }, {})
                .call(compositeDisposable)

        userRepository.getUserFull()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setUser(it)
                },{})
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
