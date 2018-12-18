package com.example.ui.about

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject
import javax.inject.Provider

class AboutFragment : BaseFragment(), AboutContract.View {

    @InjectPresenter
    lateinit var presenter: AboutPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutPresenter = presenterProvider.get()


    override fun showText(text: String) {
        tvText.text = text
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_about
}
