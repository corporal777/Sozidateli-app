package com.example.ui.organizations.events

import com.example.ui.event.list.EventListContract

interface OrganizationEventsContract {
    interface View : EventListContract.View

    interface Presenter : EventListContract.Presenter
}
