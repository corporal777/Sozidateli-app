package com.example.ui.views.galleryView

import android.Manifest
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.example.data.AppData
import com.example.data.models.EventScheduleDay
import com.example.extensions.calendar
import com.example.extensions.defaultServerDateFormatter
import com.example.ui.base.bottomSheet.BaseBottomSheetPresenter
import com.example.ui.event.my.schedule.calendar.CalendarBottomSheetContract
import com.example.ui.views.calendarView.CalendarDay
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.example.util.saveImageToGallery
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import java.util.*
import javax.inject.Inject

@InjectViewState
class GalleryBottomPresenter
@Inject constructor(
    private val appData: AppData,
    private val context: Context,
    private val rxPermissions: RxPermissions
) : BaseBottomSheetPresenter<GalleryBottomContract.View>(appData), GalleryBottomContract.Presenter {

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += rxPermissions.request(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        )
            .flatMapMaybe {
                if (it) Maybe.just(getFiles(context))
                else Maybe.error(PermissionNotGrantedException())
            }
            .subscribeBy(
                onError = {
                    viewState.hideGalleryFragment()
                    it.printStackTrace()
                },
                onNext = {
                    viewState.setGalleryImages(it)
                })

    }


    private fun getFiles(context: Context): List<Uri> {
        val fileList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Files.FileColumns._ID)
        val sortOrder = MediaStore.Images.Media._ID + " DESC"

        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )

                fileList.add(contentUri)
            }
        }
        return fileList
    }

}