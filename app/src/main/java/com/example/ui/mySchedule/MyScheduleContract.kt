package com.example.ui.mySchedule

import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.Subevent
import com.example.holders.SubEventItem
import com.example.ui.base.BaseContract

interface MyScheduleContract {
    interface View : BaseContract.View {
        @StateStrategyType(SkipStrategy::class)
        fun setTagsAndDays(tags:List<String>?,dateStart:Long,dateEnd:Long)

        @StateStrategyType(SkipStrategy::class)
        fun updateSubevents(date:Long,subevents:List<Subevent>,isMySchedule:Boolean)
    }

    interface Presenter : BaseContract.Presenter {
        fun selectDate(date:Long)
        fun changeSelectedTags(tags:ArrayList<String>)
        fun addToSchedule(subevent: Subevent)
        fun removeFromeSchedule(subevent: Subevent)
    }
}
