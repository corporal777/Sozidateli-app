package com.example.ui.profile.profileEdit

import android.net.Uri
import call
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.models.ProfileField
import com.example.data.models.ProfileFieldExpand
import com.example.data.models.user.SocialRoles
import com.example.holders.profile.ProfileBaseFieldItem
import com.example.holders.profile.ProfileExpandFieldItem
import com.example.repository.UserRepository
import com.example.ui.base.takePhoto.TakePhotoPresenter
import com.example.util.photohelper.RealPathUtil
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ProfileEditPresenter
@Inject constructor(private val appData: AppData, private val userRepository: UserRepository) : TakePhotoPresenter<ProfileEditContract.View>(), ProfileEditContract.Presenter {


    var photo: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.onUserChange
                .performOnBackgroundOutOnMain()
                .subscribe({
                    it.value?.let { viewState::setUser }
                }, {})
                .call(compositeDisposable)
    }

    override fun attachView(view: ProfileEditContract.View?) {
        super.attachView(view)
        viewState.setUser(appData.getUser())
    }

    override fun onSaveClick(fields:List<ProfileField>,expandFields:List<ProfileFieldExpand>) {

        val mapUser = HashMap<String, Any?>()

        fields.forEach {
            mapUser.put(it.nameField, it.data)
        }

        expandFields.forEach {
            val arr = mutableListOf<HashMap<String, Any?>>()
            it.listOfField.forEach { arrayField ->
                val map = HashMap<String, Any?>()
                arrayField.forEach {
                    map.put(it.nameField, it.data)
                }

                arr.add(map)
            }
            mapUser.put(it.nameField, arr)
        }

        userRepository.updateUser(mapUser)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.navigateUp()

                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)

        if (photo == null) {
            // viewState.navigateUp()
        } else {
            userRepository.uploadAvatar(photo!!)
                    .performOnBackgroundOutOnMain()
                    .withLoadingDialog(viewState)
                    .subscribe({
                        appData.setUser(it)
                        viewState.navigateUp()
                    }, {})
                    .call(compositeDisposable)
        }
    }

    override fun onChangePasswordShowDialogClick() {
        viewState.showChangePasswordDialog()
    }

    override fun onChangePasswordClick(oldPassword: String, newPassword: String) {
        val mapUser = HashMap<String, Any?>()
        mapUser.put("user_old_password",oldPassword)
        mapUser.put("user_new_pwd",newPassword)

        userRepository.updateUser(mapUser)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.showToast(R.string.profile_password_success_change)
                }, {
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    override fun onUploadDocumentClick() {
        viewState.showPdfSelector()
    }

    override fun onPdfSelected(path:String) {
        userRepository.uploadRecommendationFile(path)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.setUser(it)
                },{
                    it.printStackTrace()
                }).call(compositeDisposable)
    }

    override fun onImageTaken(path: String, uri: Uri) {
        photo = path
        val user = appData.getUser()
        user.user_avatar_uri = uri
        viewState.setUser(user)
    }
}
