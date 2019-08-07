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
@Inject constructor(
        private val userRepository: UserRepository,
        private val appData: AppData
) : BasePresenter<ProfileFullContract.View>(), ProfileFullContract.Presenter {

    private var isFirstAttach = true

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

//        userRepository.getUserFull()
//                .performOnBackgroundOutOnMain()
//                .withLoadingDialog(viewState)
//                .subscribe({
//                    viewState.setUser(it)
//                }, {})
//                .call(compositeDisposable)
    }

    override fun attachView(view: ProfileFullContract.View?) {
        super.attachView(view)
//        if (!isFirstAttach) {
//            val user = appData.fullUser
//
//            user?.let {
//                viewState.setUser(it)
//                if (it.isEmailChanged) {
//                    it.new_email?.let {
//                        viewState.showChangeEmailDialog(it)
//                    }
//                }
//            }
//
//
//        }
//        isFirstAttach = false
    }

    override fun onEditClick() {
        viewState.showEditProfile()
    }
}
