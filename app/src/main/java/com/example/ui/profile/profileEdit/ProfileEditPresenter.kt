package com.example.ui.profile.profileEdit

import android.net.Uri
import call
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ProfileField
import com.example.holders.ProfileExpandFieldItem
import com.example.holders.ProfileFieldItem
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.base.takePhoto.TakePhotoPresenter
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

    override fun onSaveClick(groupAdapter: GroupAdapter<ViewHolder>) {
        /* val arrayField = mutableListOf<ProfileField>()
         for (i in 0 until groupAdapter.itemCount) {
             val item = groupAdapter.getItem(i)

             item.let {
                 when (it) {
                     is ProfileFieldItem -> {
                         arrayField.add(it.getField())
                     }
                     is ProfileExpandFieldItem -> {
                         it.getFieldItems().forEach { profileItem ->
                             arrayField.add(profileItem.getField())
                         }
                     }
                     else -> {
                     }
                 }
             }
         }

         var user = appData.getUser()



         arrayField.forEach {
             try {
                 val field = user::class.java.getDeclaredField(it.nameField)
                 field.isAccessible = true
                 field.set(user, it.data)

                 it.nameFieldIsShowOnlyProfile?.let { nameField ->
                     val fieldShow = user::class.java.getDeclaredField(nameField)
                     fieldShow.isAccessible = true
                     fieldShow.set(user, it.isOnlyProfile)
                 }
             } catch (e: NoSuchFieldException) {
                 e.printStackTrace()
             }
         }

         userRepository.updateUser(user)
                 .performOnBackgroundOutOnMain()
                 .subscribe({
                     viewState.navigateUp()

                 }, {
                     it.printStackTrace()
                 }).call(compositeDisposable)*/

        if (photo == null) {
            viewState.navigateUp()
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

    override fun onImageTaken(path: String, uri: Uri) {
        photo = path
        val user = appData.getUser()
        user.user_avatar_uri = uri
        viewState.setUser(user)
    }
}
