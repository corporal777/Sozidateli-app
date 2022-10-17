package com.example.ui.profile.data

import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.arellomobile.mvp.InjectViewState
import com.example.R
import com.example.data.AppData
import com.example.data.socket.SocketIOManager
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.util.saveImageToCache
import com.example.util.saveImageToGallery
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import javax.inject.Inject

@InjectViewState
class ProfileDataPresenter
@Inject constructor(
    private val userRepository: UserRepository,
    private val appData: AppData,
    private val socket: SocketIOManager,
    private val notificationManager: NotificationManager,
    private val authRepository: AuthRepository,
) : BaseBottomSheetPresenter<ProfileDataContract.View>(appData), ProfileDataContract.Presenter {

    var image: Bitmap? = null
    var userName = ""
    var userShortName = ""

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        val userLink = "sozidateli.ru/$userShortName"
        viewState.setData(image!!, userName, userLink)
    }

    override fun shareImageClick(context: Context) {
        compositeDisposable += Single.fromCallable {
            image?.let { saveImageToCache(context, it) }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                if (it != null) {
                    viewState.showShareImage(it)
                }
            }
    }

    override fun shareLinkClick(text: String) {
        viewState.showShareLink(text)
    }

    override fun saveImageToGalleryClick(context: Context) {
        compositeDisposable += Completable.fromAction {
            image?.let { saveImageToGallery(context, it, "sozidateli_images") }
        }
            .performOnBackgroundOutOnMain()
            .subscribeSimple {
                viewState.showSnackBarMessage(
                    context.getString(R.string.qr_code_is_saved),
                    R.drawable.ic_profile_code_save
                )
            }
    }


}