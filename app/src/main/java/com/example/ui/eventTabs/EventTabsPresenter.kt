package com.example.ui.eventTabs

import com.arellomobile.mvp.InjectViewState
import com.example.data.UserEventData
import com.example.ui.base.BasePresenter
import java.util.*
import javax.inject.Inject

@InjectViewState
class EventTabsPresenter
@Inject constructor(
        private val eventData: UserEventData
) : BasePresenter<EventTabsContract.View>(), EventTabsContract.Presenter {

    private val tabSelectStack = Stack<TabSelectCommand>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        viewState.apply {
            initialNavigationSetup()
            setLabel(eventData.event!!.name ?: "")
        }

        selectTab(TabSelectCommand.MySchedule)
    }

    override fun onMyScheduleTabSelected() = selectTab(TabSelectCommand.MySchedule)

    override fun onScheduleTabSelected() = selectTab(TabSelectCommand.Schedule)

    override fun onAboutSelected() = selectTab(TabSelectCommand.AboutEvent)

    override fun onMapTabsSelected() = selectTab(TabSelectCommand.Map)

    override fun onClickBackWhenCurrentNavigationOnTop() {
        if (tabSelectStack.size > 1) {
            tabSelectStack.pop()
            tabSelectStack.peek().execute(viewState)
        } else {
            viewState.finish()
        }
    }

    private fun selectTab(command: TabSelectCommand) {
        if (tabSelectStack.isNotEmpty() && command == tabSelectStack.peek()) {
            viewState.setCurrentDestinationOnStart()
        } else {
            tabSelectStack.remove(command)
            tabSelectStack.push(command.apply { execute(viewState) })
        }
    }

    override fun onToListSelected() {
        viewState.showEventList()
    }

    override fun onMenuChatClick() = viewState.showChat()

    override fun onMenuSearchClick() = viewState.showSearch()

    override fun onMenuAccountClick() = viewState.showAccount()

    override fun onDestroy() {
        super.onDestroy()
        eventData.clear()
    }

    private sealed class TabSelectCommand {
        abstract fun execute(view: EventTabsContract.View?)

        object MySchedule : TabSelectCommand() {
            override fun execute(view: EventTabsContract.View?) {
                view?.showMyScheduleTab()
            }
        }

        object Schedule : TabSelectCommand() {
            override fun execute(view: EventTabsContract.View?) {
                view?.showScheduleTab()
            }
        }

        object AboutEvent : TabSelectCommand() {
            override fun execute(view: EventTabsContract.View?) {
                view?.showAboutTab()
            }
        }

        object Map : TabSelectCommand() {
            override fun execute(view: EventTabsContract.View?) {
                view?.showMapTab()
            }
        }
    }
}
