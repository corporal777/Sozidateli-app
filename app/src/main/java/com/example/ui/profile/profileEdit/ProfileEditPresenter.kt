package com.example.ui.profile.profileEdit

import android.net.Uri
import call
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.models.ProfileField
import com.example.data.models.ProfileFieldExpand
import com.example.repository.UserRepository
import com.example.ui.base.takePhoto.TakePhotoPresenter
import com.example.util.FIELD_ATTACH_RECOMMENDATION_FILE
import performOnBackgroundOutOnMain
import withLoadingDialog
import javax.inject.Inject

@InjectViewState
class ProfileEditPresenter
@Inject constructor(private val userRepository: UserRepository) : TakePhotoPresenter<ProfileEditContract.View>(), ProfileEditContract.Presenter {


    var photo: String? = null

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.setUser(appData.getUser())
    }

    override fun attachView(view: ProfileEditContract.View?) {
        super.attachView(view)
    }

    override fun onSaveClick(fields: List<ProfileField>, expandFields: List<ProfileFieldExpand>) {

        val mapUser = HashMap<String, Any?>()

        fields.forEach {
            if (isRequiredValid(it)) {
                mapUser.put(it.nameField, it.data)
            } else {
                it.label?.let { name ->
                    viewState.showRequiredError(name)
                }
                return
            }
        }

        expandFields.forEach {
            val arr = mutableListOf<HashMap<String, Any?>>()
            it.listOfField.forEach { arrayField ->
                val map = HashMap<String, Any?>()
                arrayField.forEach {
                    if (isRequiredValid(it)) {
                        map.put(it.nameField, it.data)
                    } else {
                        it.label?.let { name ->
                            viewState.showRequiredError(name)
                        }
                        return
                    }
                }

                arr.add(map)
            }
            mapUser.put(it.nameField, arr)
        }

        var isEmailChange = false

        if (mapUser["user_email"] != appData.getUser().user_email) {
            isEmailChange = true
        }


        userRepository.uploadAvatar(photo)
                .andThen(userRepository.updateUser(mapUser))
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({

                    it.isEmailChanged = isEmailChange
                    if(isEmailChange){
                        it.new_email = mapUser["user_email"].toString()
                    }

                    appData.setUser(it)
                    viewState.navigateUp()
                }, {})
                .call(compositeDisposable)
    }

    private fun isRequiredValid(field: ProfileField): Boolean {
        return (field.required && !field.data?.toString().isNullOrEmpty() && field.isValid) || !field.required
    }

    override fun onChangePasswordShowDialogClick() {
        viewState.showChangePasswordDialog()
    }

    override fun onChangePasswordClick(oldPassword: String, newPassword: String) {
        val mapUser = HashMap<String, Any?>()
        mapUser.put("user_old_password", oldPassword)
        mapUser.put("user_new_pwd", newPassword)

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

    override fun onPdfSelected(path: String) {
        userRepository.uploadRecommendationFile(path)
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribe({
                    viewState.updateExpandFieldByName(FIELD_ATTACH_RECOMMENDATION_FILE, it.attached_recomendation_files)
                }, {
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
