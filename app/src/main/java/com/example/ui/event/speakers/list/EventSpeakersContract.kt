package com.example.ui.event.speakers.list

import com.example.data.models.MemberModel
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface EventSpeakersContract {
    interface View : BaseContract.View {
        @AddToEndSingle
        fun setData(data: List<MemberModel?>)

        @OneExecution
        fun showSpeaker(eventId: String, speakerId: Int)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSpeakerClick(id: Int)
        fun onRefreshRequest()
    }
}