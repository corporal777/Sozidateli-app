package com.example.ui.userprofile.edit.interests

import com.example.data.AppData
import com.example.data.models.InterestNew
import com.example.data.models.UserDetail
import com.example.data.models.UserInterest
import com.example.repository.CommonRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.userprofile.edit.contacts.EditContactsContract
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCustomLoading
import javax.inject.Inject

@InjectViewState
class EditInterestsPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val commonRepository: CommonRepository
) : BasePresenter<EditInterestsContract.View>(appData), EditInterestsContract.Presenter {

    private var isUpdating = false
    private var isInterestsLoaded = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setPlaceholder()
        compositeDisposable += appData.userChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.navigateUp()
                },
                onNext = {
                    val user = it.value
                    if (user == null) viewState.navigateUp()
                    else if (isUpdating) isUpdating = false
                    else getInterests(user)
                })
    }

    private fun getInterests(user: UserDetail) {
        if (isInterestsLoaded) return
        compositeDisposable += commonRepository.getInterests()
            .map { groupUserInterests(user, it) }
            .performOnBackgroundOutOnMain()
            .subscribe({
                viewState.setInterestsData(it)
                isInterestsLoaded = true
            }, {
                it.printStackTrace()
            })
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
                val isUserInterest =
                    userInterests.find { userInterest -> userInterest == interest.id } != null

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
            .flatMap { userRepository.checkUserProfileSingle() }
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { it.printStackTrace() },
                onSuccess = { viewState.navigateUp() }
            )
    }

    fun getBaseUserState() = appData.hasBaseState
    fun getMaxUserState() = appData.hasMaxState
}