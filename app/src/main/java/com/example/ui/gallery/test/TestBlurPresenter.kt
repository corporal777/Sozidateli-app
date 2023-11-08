package com.example.ui.gallery.test

import android.content.Context
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.repository.EventRepository
import com.example.ui.base.BasePresenter
import com.example.data.models.AboutEventData
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
