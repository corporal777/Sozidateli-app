package com.example.ui.eventTabs

import android.util.Log
import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.UserEventData
import com.example.data.bodies.EventCalendarBody
import com.example.data.models.Place
import com.example.data.models.createMapInfo
import com.example.di.Connectivity
import com.example.repository.EventRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.rxkotlin.plusAssign
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withLoadingDialog
import java.util.*
import javax.inject.Inject

@InjectViewState
class EventTabsPresenter
@Inject constructor(
        private val eventData: UserEventData,
        private val eventRepository: EventRepository,
        private val userRepository: UserRepository,
        appData: AppData,
        @Connectivity private val connectivity: Observable<Boolean>
) : BasePresenter<EventTabsContract.View>(appData), EventTabsContract.Presenter {

    /*private*/ val userEvent = eventData.userEvent!!
    private var isInternetConnected = false
    private var isFirstAttach = false

    private val tabSelectStack = Stack<TabSelectCommand>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += connectivity
                .performOnBackgroundOutOnMain()
                .subscribeSimple(onNext = {
                    isInternetConnected = it
                    if (it) {
                        viewState.showNoConnectionMessage(false)
                        if (eventData.isDataFromLocalStorage) loadData()
                    }
                })

        onMyScheduleTabSelected()
    }

    override fun attachView(view: EventTabsContract.View?) {
        super.attachView(view)
        if (isFirstAttach) isFirstAttach = false
        else if (!isInternetConnected) {
            viewState.apply {
                showAboutTab(userEvent.eventId)
                //showMyScheduleTab()
                showNoConnectionMessage(false)
            }
        }
    }

    private fun loadData() {
        compositeDisposable += eventData.load(userEvent.eventId)
                .performOnBackgroundOutOnMain()
                .subscribeSimple { }
    }

    override fun onMyScheduleTabSelected() {
        viewState.apply {
            showNoConnectionMessage(false)
        }

        selectTab(TabSelectCommand.MySchedule)
    }

    override fun onScheduleTabSelected() {
        viewState.apply {
            showNoConnectionMessage(false)
        }

        selectTab(TabSelectCommand.Schedule)
    }

    override fun onAboutSelected() {
        viewState.apply {
            showNoConnectionMessage(!isInternetConnected)
        }

        selectTab(TabSelectCommand.AboutEvent)
    }

    override fun onMapTabsSelected() {
        viewState.apply {
            showNoConnectionMessage(!isInternetConnected)
        }

        selectTab(TabSelectCommand.Map)
    }

    override fun onToListSelected() {
        compositeDisposable += eventRepository.deleteAllCalendarEvents(EventCalendarBody.CALENDAR_EVENT)
                .andThen(userRepository.getUserShortNew().ignoreElement().onErrorComplete())
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onError = {
                            Log.ERROR
                        },
                        onComplete = {
                            eventData.clear()
                            viewState.showEventList()
                        },
                        onNoInternetConnectionException = {
                            viewState.showNoConnectionMessage(true)
                        })

        /*compositeDisposable += eventRepository.setDefaultEvent("0")
                .andThen(userRepository.getUserShort().ignoreElement().onErrorComplete())
                .withCheckInternetConnectivity()
                .performOnBackgroundOutOnMain()
                .withLoadingDialog(viewState)
                .subscribeSimple(
                        onComplete = {
                            eventData.clear()
                            viewState.showEventList()
                        },
                        onNoInternetConnectionException = {
                            viewState.showNoConnectionMessage(true)
                        })*/
    }

    private fun selectTab(command: TabSelectCommand) {
        tabSelectStack.remove(command)
        tabSelectStack.push(command)
        viewState.apply {
            when (command) {
                is TabSelectCommand.MySchedule -> showMyScheduleTab()
                is TabSelectCommand.Schedule -> showScheduleTab()
                is TabSelectCommand.AboutEvent -> showAboutTab(userEvent.eventId)
                is TabSelectCommand.Map -> {
                    val eventInfo = userEvent.eventInfo
                    showMapTab(eventInfo.event.name?: "",
                            eventInfo.event.createMapInfo(),
                            /*eventInfo.places.toTypedArray()*/arrayOf()
                    )
                }
            }
            setBackClickHandlerEnabled(tabSelectStack.size > 1)
        }
    }

    override fun onHandleBackCLick() {
        if (tabSelectStack.size > 1) {
            tabSelectStack.pop()
            selectTab(tabSelectStack.peek())
        }
    }

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuAccountClick() = viewState.showAccount()

    override fun onMenuNotificationsClick() = viewState.showNotifications()

    override fun onDestroy() {
        super.onDestroy()
        eventData.clear()
    }

    private sealed class TabSelectCommand {
        object MySchedule : TabSelectCommand()
        object Schedule : TabSelectCommand()
        object AboutEvent : TabSelectCommand()
        object Map : TabSelectCommand()
    }
}
