package com.example.ui.chat

import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.ui.base.BaseFragment
import javax.inject.Inject
import javax.inject.Provider

class ChatFragment : BaseFragment(), ChatContract.View {

    @InjectPresenter
    lateinit var presenter: ChatPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatPresenter = presenterProvider.get().apply {
        val args = ChatFragmentArgs.fromBundle(arguments)
        chatId = args.chatId
    }

    override fun layout() = R.layout.fragment_first
}
