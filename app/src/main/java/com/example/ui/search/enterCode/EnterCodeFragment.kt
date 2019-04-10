package com.example.ui.search.enterCode

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.navigation.fragment.findNavController
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.ui.base.BaseFragment
import com.example.util.ARG_EVENT
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_enter_code.*
import javax.inject.Inject
import javax.inject.Provider

class EnterCodeFragment : BaseFragment(), EnterCodeContract.View {

    @InjectPresenter
    lateinit var presenter: EnterCodePresenter

    @Inject
    lateinit var presenterProvider: Provider<EnterCodePresenter>

    @ProvidePresenter
    fun providePresenter(): EnterCodePresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSearch.setOnClickListener { presenter.onSearchClick(etCode.text.toString()) }
    }

    override fun showEvent(event: Event) {
        findNavController().navigate(R.id.enter_code_to_event,bundleOf(
                ARG_EVENT to event
        ))
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_enter_code
}
