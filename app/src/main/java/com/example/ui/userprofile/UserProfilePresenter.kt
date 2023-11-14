package com.example.ui.userprofile

import com.example.data.AppData
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import moxy.InjectViewState
import javax.inject.Inject

@InjectViewState
class UserProfilePresenter @Inject constructor(
    val appData: AppData,
) : BaseUserProfilePresenter<UserProfileContract.View>(appData),
    UserProfileContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
    }

    override fun attachView(view: UserProfileContract.View?) {
        super.attachView(view)
    }

    override fun onEditAvatarClick() {
        val avatar = user.image?.uri?.takeIf { it.isNotBlank() }
        viewState.showTakePictureChooser(avatar != null, appData.hasBaseState, appData.hasMaxState)
    }

    override fun onMainDataClick() = viewState.showMainData()
    override fun onContactsClick() = viewState.showContacts()
    override fun onInterestsClick() {
        if (!user.isHasInterests()) viewState.showEdit()
        else viewState.showInterests()
    }
    override fun onEducationClick() = viewState.showEducation()
    override fun onExperienceClick() = viewState.showExperience()
}
