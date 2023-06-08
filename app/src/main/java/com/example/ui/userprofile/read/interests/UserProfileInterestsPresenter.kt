package com.example.ui.userprofile.read.interests

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.InterestNew
import com.example.data.models.UserDetail
import com.example.repository.CommonRepository
import com.example.repository.UserRepository
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import withProgressBarLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserProfileInterestsPresenter @Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val commonRepository: CommonRepository,
) : BaseUserProfilePresenter<UserProfileInterestsContract.View>(appData),
    UserProfileInterestsContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.showInterestsPlaceholder()
    }


    override fun onUserUpdated(user: UserDetail?) {
        if (user?.isHasInterests() == false) viewState.onInterestsUpdated(emptyMap())
        else {
            val userInterests = user?.interests
            compositeDisposable += commonRepository.getInterests()
                .map { groupUserInterests(userInterests, it) }
                .performOnBackgroundOutOnMain()
                .subscribeSimple(
                    onError = { onReceiveError(it) },
                    onSuccess = { viewState.onInterestsUpdated(it) }
                )
        }
    }

    private fun groupUserInterests(userInterests: List<Int>?, interests: List<InterestNew>?): Map<InterestNew, MutableList<InterestNew>> {
        val groups = mutableMapOf<InterestNew, MutableList<InterestNew>>()
        val headers = interests?.filter { it.parent == 0 }
        headers?.forEach {
            val parent = interests.filter { parent -> parent.parent == it.id }
            parent.let { it1 ->
                userInterests?.forEach { usIn ->
                    val isUserInterest = it1.find { it2 -> it2.id == usIn }
                    if (isUserInterest != null)
                        groups.getOrPut(it) { mutableListOf() }.add(isUserInterest)
                }
            }
        }
        return groups
    }


    override fun onEditClick() = viewState.showEdit()
}
