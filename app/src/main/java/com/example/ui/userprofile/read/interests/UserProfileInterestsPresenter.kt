package com.example.ui.userprofile.read.interests

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.InterestNew
import com.example.data.models.UserDetail
import com.example.repository.UserRepository
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserProfileInterestsPresenter @Inject constructor(
    appData: AppData,
    private val userRepository: UserRepository
) : BaseUserProfilePresenter<UserProfileInterestsContract.View>(appData),
    UserProfileInterestsContract.Presenter {

    override fun onUserUpdated(user: UserDetail?) {
        val userInterests = user?.interests

//        if (userInterests.isNullOrEmpty()) { viewState.showNextScreen() } else {
//            compositeDisposable += userRepository.getInterestsList(null)
//                    .map { groupUserInterests(userInterests, it.data) }
//                    .performOnBackgroundOutOnMain()
//                    .withLoadingDialog(viewState)
//                    .subscribe({
//                        viewState.onInterestsUpdated(it)
//                    }, {
//                        it.printStackTrace()
//                    })
//        }
//        if (userInterests.isNullOrEmpty()) {
//            viewState.showEdit()
//        }
        compositeDisposable += userRepository.getInterestsList(null)
            .map { groupUserInterests(userInterests, it.data) }
            .performOnBackgroundOutOnMain()
            .withLoadingDialog(viewState)
            .subscribe({
                viewState.onInterestsUpdated(it)
            }, {
                it.printStackTrace()
            })
    }

    private fun groupUserInterests(
        userInterests: List<Int>?,
        interests: List<InterestNew>?
    ): Map<InterestNew, MutableList<InterestNew>> {
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
