package com.example.ui.userprofile

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserProfilePresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
) : BasePresenter<UserProfileContract.View>(), UserProfileContract.Presenter {

    lateinit var userId: String

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

        compositeDisposable += userRepository.getUserFull()
                .flatMapObservable { appData.userChangeSubject }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeBy(
                        onError = {
                            it.printStackTrace()
                        },
                        onNext = {
                            viewState.setUser(it.value)
                        }
                )
    }

    override fun onEditAvatarClick() {
        TODO("Not yet implemented")
    }

    override fun onMainDataClick() {
        TODO("Not yet implemented")
    }

    override fun onContactsClick() {
        TODO("Not yet implemented")
    }

    override fun onInterestsClick() {
        TODO("Not yet implemented")
    }

    override fun onEducationClick() {
        TODO("Not yet implemented")
    }

    override fun onExperienceClick() {
        TODO("Not yet implemented")
    }
}
