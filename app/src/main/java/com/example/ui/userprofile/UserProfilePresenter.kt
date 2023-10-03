package com.example.ui.userprofile

import android.net.Uri
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ImageModel
import com.example.repository.UserRepository
import com.example.ui.gallery.cropImage.cropHelper.CropImageView
import com.example.ui.userprofile.base.BaseUserProfilePresenter
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.rxtakephoto.ResultRotation
import com.example.util.rxtakephoto.RxTakePhoto
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withProgressBarDialogLoading
import javax.inject.Inject

@InjectViewState
class UserProfilePresenter @Inject constructor(
    val appData: AppData,
    private val userRepository: UserRepository,
    private val takePhoto: RxTakePhoto,
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
