package com.example.ui.state.max.interests

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.InterestNew
import com.example.data.models.UserDetail
import com.example.data.models.UserInterest
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.UserState
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class BaseStateInterestsPresenter
@Inject constructor(
        private val appData: AppData,
        private val userRepository: UserRepository
): BasePresenter<BaseStateInterestsContract.View>(appData), BaseStateInterestsContract.Presenter {

    var screen: Int = 1
    private var isInterestsLoaded = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userNewChangeSubject
                .performOnBackgroundOutOnMain()
                .subscribe({
                    val user = it.value ?: throw RuntimeException("Edit null user")
                    viewState.apply {
                        getInterests(user)
                    }
                }, {
                    it.printStackTrace()
                    viewState.navigateUp()
                })
    }

    private fun getInterests(user: UserDetail) {
        if (isInterestsLoaded) return
        compositeDisposable += userRepository.getInterestsList(null)
                .map { groupUserInterests(user, it.data) }
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setInterestsData(it)
                    isInterestsLoaded = true
                }, {
                    it.printStackTrace()
                })
    }

    private fun groupUserInterests(user: UserDetail, interests: List<InterestNew>?): Map<InterestNew, List<UserInterest>> {
        val userInterests = user.interests?.map { it } ?: emptyList()
        val groups = mutableMapOf<InterestNew, MutableList<UserInterest>>()
        interests?.forEach { interest ->
            val parent = interests.find { parent -> parent.id == interest.parent }
            parent?.let {
                val isUserInterest = userInterests.find { userInterest -> userInterest == interest.id } != null
                groups.getOrPut(parent) { mutableListOf() }.add(UserInterest(interest, isUserInterest))
            }
        }
        return groups
    }

    override fun onClickClose() {
        viewState.navigateUp()
    }

    override fun onSaveInterestsClick(data: List<InterestNew>) {
        viewState.showLoadingDialog()
        updateUser(userRepository.updateProfile(appData.getId(), mapOf(UserDetail.USER_INTERESTS to data.map { item -> item.id }))) {
            it.interests = data.map { item -> item.id?: 0 }
            viewState.hideAllLoadingDialogs()
            true
        }
    }

    private fun updateUser(request: Single<UserDetail>, onComplete: (UserDetail) -> Boolean) {
        compositeDisposable += request
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    appData.getUserNew().apply {
                        phone = it.phone
                    }
                    if (onComplete(it))
                        compositeDisposable += userRepository.checkUserProfileSingle()
                                .performOnBackgroundOutOnMain()
                                .subscribe({
                                    viewState.goToNext()
                                },{
                                    viewState.goToNext()
                                })
                        //viewState.goToNext()
                }, {
                    it.printStackTrace()
                    viewState.showUpdateError(it.message)
                })
    }
}