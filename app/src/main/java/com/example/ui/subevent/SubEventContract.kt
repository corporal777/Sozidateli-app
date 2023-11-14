package com.example.ui.subevent

import com.example.data.models.EventActivityModel
import com.example.data.models.MemberModel
import com.example.ui.base.BaseContract
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution

interface SubEventContract {
    interface View : BaseContract.View {

        @AddToEndSingle
        fun setSubEventPlaceholder()

        @AddToEndSingle
        fun setData(isApproved : Boolean, subEvent: EventActivityModel)

        @AddToEndSingle
        fun setSpeakers(speakers: List<MemberModel>)

        @OneExecution
        fun showSpeakerProfile(speaker: Int)

        @OneExecution
        fun updateSubEvent(subEvent: EventActivityModel)

        @OneExecution
        fun showEventErrorMessageDialog(withResult : Boolean, id : String, message : String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSpeakerClick(speaker: Int)
//        fun onSpeakerChangeSubscriptionClick(speaker: MemberModel)
//        fun onSubeventChangeSubscriptionClick(subevent: EventActivityModel)

        fun onAddToScheduleClick(subEvent: EventActivityModel)
        fun onRemoveFromScheduleClick(subEvent: EventActivityModel)    }
}
