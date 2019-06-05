package com.example.ui.event.list.recommendations

import com.example.ui.event.list.EventListContract

interface RecommendationsContract {
    interface View : EventListContract.View

    interface Presenter : EventListContract.Presenter
}
