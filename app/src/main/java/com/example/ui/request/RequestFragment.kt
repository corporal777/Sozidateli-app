package com.example.ui.request

import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.holders.RequestInputItem
import com.example.ui.base.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_request.*
import javax.inject.Inject
import javax.inject.Provider

class RequestFragment : BaseFragment(), RequestContract.View {

    @InjectPresenter
    lateinit var presenter: RequestPresenter

    @Inject
    lateinit var presenterProvider: Provider<RequestPresenter>

    @ProvidePresenter
    fun providePresenter(): RequestPresenter = presenterProvider.get()

    private val adapter by lazy { GroupAdapter<ViewHolder>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply { adapter = this@RequestFragment.adapter }
        btnClose.setOnClickListener { presenter.onCloseClick() }
        btnSendRequest.setOnClickListener { presenter.onCloseClick() }

        adapter.update(listOf(
                RequestInputItem("Имя"),
                RequestInputItem("Email"),
                RequestInputItem("Дата рождения"),
                RequestInputItem("Место рождения"),
                RequestInputItem("Номер паспорта"),
                RequestInputItem("Какой сектор представляете")
        ))
    }

    override fun enableActionButton(enable: Boolean) {
        btnSendRequest.apply {
            isEnabled = enable

            val background: Int
            val textColor: Int
            if (enable) {
                background = R.drawable.background_corners
                textColor = Color.WHITE
            } else {
                background = R.drawable.background_edittext_login
                textColor = Color.DKGRAY
            }

            setBackgroundResource(background)
            setTextColor(textColor)
        }
    }

    override fun layout() = R.layout.fragment_request
}
