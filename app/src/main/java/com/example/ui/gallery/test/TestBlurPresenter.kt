package com.example.ui.gallery.test

import android.Manifest
import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.AddToFavoriteEntityModel
import com.example.data.bodies.AddToFavoriteModel
import com.example.data.bodies.EventCalendarBody
import com.example.data.bodies.EventCalendarBodyEntity
import com.example.data.models.EventActivityModel
import com.example.data.models.EventUserFavorite
import com.example.data.models.createMapInfo
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.ui.event.about.AboutEventContractNew
import com.example.ui.event.about.items.AboutEventData
import com.example.util.ImageUtil
import com.example.util.rxtakephoto.PermissionNotGrantedException
import com.google.gson.Gson
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import retrofit2.HttpException
import javax.inject.Inject

@InjectViewState
class TestBlurPresenter
@Inject constructor(
    private val context: Context,
    private val appData: AppData,
    private val eventRepository: EventRepository,
    private val userEventData: UserEventData,
) : BasePresenter<TestBlurContract.View>(appData), TestBlurContract.Presenter {

    lateinit var eventId: String
    private var mDy = 0
    private lateinit var aboutEventData: AboutEventData

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()

    }
}
