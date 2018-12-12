package com.example.ui.mySchedule

import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseNestedNavigationFragment
import javax.inject.Inject
import javax.inject.Provider

class MyScheduleFragment : BaseNestedNavigationFragment(), MyScheduleContract.View {

    @InjectPresenter
    lateinit var presenter: MySchedulePresenter

    @Inject
    lateinit var presenterProvider: Provider<MySchedulePresenter>

    @ProvidePresenter
    fun providePresenter(): MySchedulePresenter = presenterProvider.get()

    private fun findNestedNavController(): NavController = Navigation.findNavController(view!!.findViewById(R.id.tabsNavHostFragment))

    override fun layout() = R.layout.fragment_my_schedule
}
