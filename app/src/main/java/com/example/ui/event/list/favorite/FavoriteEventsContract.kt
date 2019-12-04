package com.example.ui.event.list.favorite

import com.example.ui.event.list.EventListContract

interface FavoriteEventsContract {
    interface View : EventListContract.View

    interface Presenter : EventListContract.Presenter
}
