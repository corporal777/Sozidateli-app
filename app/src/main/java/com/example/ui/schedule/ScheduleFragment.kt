package com.example.ui.schedule

import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseNestedNavigationFragment
import javax.inject.Inject
import javax.inject.Provider

class ScheduleFragment : BaseNestedNavigationFragment(), ScheduleContract.View {

    @InjectPresenter
    lateinit var presenter: SchedulePresenter

    @Inject
    lateinit var presenterProvider: Provider<SchedulePresenter>

    @ProvidePresenter
    fun providePresenter(): SchedulePresenter = presenterProvider.get()

    private fun findNestedNavController(): NavController = Navigation.findNavController(view!!.findViewById(R.id.tabsNavHostFragment))

    override fun layout() = R.layout.fragment_schedule
}
