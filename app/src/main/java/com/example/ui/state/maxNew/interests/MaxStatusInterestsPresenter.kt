package com.example.ui.state.maxNew.interests

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.InterestNew
import com.example.data.models.UserDetail
import com.example.data.models.UserInterest
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.state.maxNew.base.BaseMaxStatePresenter
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withInfinityCustomLoading
import withProgressBarDialogLoading
import withProgressBarLoading
import javax.inject.Inject

@InjectViewState
class MaxStatusInterestsPresenter
@Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : BaseMaxStatePresenter<MaxStatusInterestsContract.View>(appData, userRepository, authRepository),
    MaxStatusInterestsContract.Presenter {

    private var isFirstLaunch = true
    private val interestsList = arrayListOf<InterestNew>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += appData.userNewChangeSubject
            .flatMapMaybe { loadInterests(it.value) }
            .performOnBackgroundOutOnMain()
            .let {
                if (isFirstLaunch) {
                    isFirstLaunch = false
                    it.withProgressBarLoading(viewState)
                } else it
            }
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.navigateUp()
                },
                onNext = {
                    if (isUpdating) isUpdating = false
                    else viewState.setInterestsData(it)
                })

    }


    private fun loadInterests(user: UserDetail?): Maybe<Map<InterestNew, List<UserInterest>>> {
        if (user == null) return Maybe.error(java.lang.NullPointerException())
        else {
            return if (interestsList.isNullOrEmpty())
                userRepository.getInterestsList(null).map { groupUserInterests(user, it.data) }
            else Maybe.just(interestsList).map { groupUserInterests(user, it) }
        }

    }

    private fun groupUserInterests(
        user: UserDetail,
        interests: List<InterestNew>?
    ): Map<InterestNew, List<UserInterest>> {
        val userInterests = user.getUserInterests()
        val groups = mutableMapOf<InterestNew, MutableList<UserInterest>>()
        interests?.forEach { interest ->
            val parent = interests.find { parent -> parent.id == interest.parent }
            parent?.let {
                val isUserInterest = userInterests.find { userInterest -> userInterest == interest.id } != null
                groups.getOrPut(parent) { mutableListOf() }
                    .add(UserInterest(interest, isUserInterest))
            }
        }
        return groups
    }

    override fun onSaveInterestsClick(data: List<InterestNew>) {
        isUpdating = true
        compositeDisposable += userRepository.updateUserProfile(
            appData.getId(),
            mapOf(UserDetail.USER_INTERESTS to data.map { item -> item.id })
        )
            .performOnBackgroundOutOnMain()
            .withInfinityCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { checkNextScreen() }
            )
    }

}