package com.example.ui.mySchedule

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Subevent
import com.example.holders.*
import com.example.ui.base.BaseNestedNavigationFragment
import com.example.util.TYPE_SCHEDULE_MY
import com.example.util.Utils
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_my_schedule.*
import javax.inject.Inject
import javax.inject.Provider

class MyScheduleFragment : BaseNestedNavigationFragment(), MyScheduleContract.View {

    @InjectPresenter
    lateinit var presenter: MySchedulePresenter

    @Inject
    lateinit var presenterProvider: Provider<MySchedulePresenter>

    @ProvidePresenter
    fun providePresenter(): MySchedulePresenter = presenterProvider.get().apply {
        isMySchedule = arguments!!.getString("type") == TYPE_SCHEDULE_MY
    }

    private fun findNestedNavController(): NavController = Navigation.findNavController(view!!.findViewById(R.id.tabsNavHostFragment))

    private val section = Section()
    private val groupAdapter = GroupAdapter<ViewHolder>().apply {
        add(section)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun setTagsAndDays(tags: List<String>?,dateStart:Long,dateEnd:Long) {
        section.setHeader(HeaderSchedule(tags,dateStart,dateEnd,presenter))
    }

    override fun updateSubevents(date: Long, subevents: List<Subevent>,isMySchedule:Boolean) {
        val list = mutableListOf<Item>()
        list.add(TitleSubEventsDayItem(date))
        subevents.forEach {
            list.add(SubEventItem(it,isMySchedule,presenter))
        }
        section.update(list)
    }

    override fun openSubevent(subevent: Subevent,isMySchedule: Boolean) {
        findParentNavigation().navigate(R.id.subevent_fragment,bundleOf(
                "subevent" to subevent
        ))
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_my_schedule
}
