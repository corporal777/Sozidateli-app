package com.example.ui.state.max

import android.graphics.Bitmap
import com.arellomobile.mvp.InjectViewState
import com.example.BuildConfig
import com.example.data.AppData
import com.example.data.models.*
import com.example.data.models.user.RecommendationFile
import com.example.data.models.user.User
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.base.MainInfoContract
import com.example.util.AuthValidateUtil
import com.example.util.IMAGE_MAX_SIZE_AVATAR
import com.example.util.rxtakephoto.ResultRotation
import com.isseiaoki.simplecropview.CropImageView
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import performOnBackgroundOutOnMain
import withCustomProgressBarLoadingDialog
import withLoadingDialog
import java.io.File
import javax.inject.Inject
import kotlin.math.abs

@InjectViewState
class MaxStateMainInfoPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository
) : BasePresenter<MaxStateMainInfoContract.View>(appData), MaxStateMainInfoContract.Presenter {

    var screen: Int = 1
    var isUpdatePhoto = false
    private var mDy = 0f

    override fun attachView(view: MaxStateMainInfoContract.View?) {
        super.attachView(view)
        viewState.setAppBarElevation(mDy)
    }

    override fun changeAppBarElevation(value: Int) {
        mDy = abs(value / 10f)
        viewState.setAppBarElevation(mDy)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setAppBarElevation(mDy)
        compositeDisposable += appData.userNewChangeSubject
            .performOnBackgroundOutOnMain()
            .subscribe({
                val user = it.value ?: throw RuntimeException("Edit null user")
                viewState.setPersonalData(user)
            }, {
                it.printStackTrace()
                viewState.navigateUp()
            })
    }

    override fun onClickClose() {
        viewState.setClickClose(screen)
    }


    private fun onEditSaveNew(data: MutableMap<String, Any?>, onComplete: (UserDetail) -> Boolean) {
        if (data.isEmpty()) {
            viewState.navigateUp()
            return
        }
        updateUserNew(userRepository.updateProfile(appData.getId(), data), onComplete)
    }

    override fun onChangeEmailClick() {
        viewState.showChangeEmail()
    }

    override fun onChangeEmailConfirm(email: String, isFirst: Boolean) {
        if (AuthValidateUtil.isValidEmail(email)) {
            updateUserNew(
                userRepository.updateProfile(
                    appData.getId(),
                    mapOf(UserDetail.USER_EMAIL to FieldDetails(value = email))
                )
            ) {
                it.email?.value = email
                viewState.showChangeEmailComplete(email)
                false
            }
        } else {
            viewState.showUpdateError()
        }
    }

    override fun updateFiles(data: MutableMap<String, Any?>) {
        onEditSaveNew(data) {
            appData.updateUserNew {
                name = it.name
                middleName = it.middleName
                lastName = it.lastName
                birthday = it.birthday
                gender = it.gender
                notes = it.notes
                address = it.address
                contactInformation.site = it.contactInformation.site
                contactInformation.socialLinks = it.contactInformation.socialLinks
                phone = it.phone
            }
            true
        }
    }

    private fun updateUserNew(request: Single<UserDetail>, onComplete: (UserDetail) -> Boolean) {
        compositeDisposable += request
            .performOnBackgroundOutOnMain()
            .withCustomProgressBarLoadingDialog(viewState)
            .subscribe({
                appData.getUserNew().apply {
                    phone = it.phone
                }
                if (onComplete(it))
                    viewState.goToNext()
            }, {
                it.printStackTrace()
                viewState.showUpdateError(it.message)
            })
    }
}