package com.example.ui.userprofile.read.interests

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.Interest
import com.example.data.models.user.User
import com.example.repository.CommonRepository
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class UserProfileInterestsPresenter @Inject constructor(
        appData: AppData,
        private val commonRepository: CommonRepository
) : BaseUserProfilePresenter<UserProfileInterestsContract.View>(appData), UserProfileInterestsContract.Presenter {

    override fun onUserUpdated(user: User?) {
        val userInterests = user?.interests
        if (userInterests.isNullOrEmpty()) {
            viewState.onInterestsUpdated(emptyMap())
        } else {
            compositeDisposable += commonRepository.getInterests()
                    .map { groupUserInterests(userInterests, it) }
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribeSimple {
                        viewState.onInterestsUpdated(it)
                    }
        }
    }

    private fun groupUserInterests(
            userInterests: List<Interest>,
            interests: List<Interest>
    ): Map<Interest, MutableList<Interest>> {
        val groups = mutableMapOf<Interest, MutableList<Interest>>()
        userInterests.forEach {
            val key = interests.find { interest -> interest.id == it.parent }
            if (key != null) {
                val list = groups.getOrPut(key) { mutableListOf() }
                list.add(it)
            }
        }
        return groups

    }

    override fun onEditClick() = viewState.showEdit()
}
