package com.example.ui.chat

import android.arch.paging.PagedList
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.ChatAdapter
import com.example.data.models.ChatMessage
import com.example.ui.base.BaseFragment
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_first.*
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

    private lateinit var chatAdapter: ChatAdapter

    override fun iniChatAdapter(query: Query) {
        val config = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(10)
                .setPageSize(20)
                .build()

        val options = FirestorePagingOptions.Builder<ChatMessage>()
                .setLifecycleOwner(this)
                .setQuery(query, config, ChatMessage::class.java)
                .build()

        chatAdapter = ChatAdapter(options)

        rvChat.adapter = chatAdapter
    }

    override fun layout() = R.layout.fragment_first
}
