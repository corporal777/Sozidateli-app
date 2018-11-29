package com.example.ui.chat

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.ChatAdapter
import com.example.data.models.UserChatMessage
import com.example.ui.base.BaseFragment
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
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
        userId = args.userId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSend.setOnClickListener { presenter.onSendTextMessageClick("${etMessage.text}") }

        rvChat.apply {
            (layoutManager as LinearLayoutManager).stackFromEnd = true
        }
    }

    override fun iniChatAdapter(query: Query, parser: SnapshotParser<UserChatMessage>) {
        val options = FirestoreRecyclerOptions.Builder<UserChatMessage>()
                .setLifecycleOwner(this)
                .setQuery(query, parser)
                .build()

        rvChat.adapter = ChatAdapter(options)
    }

    override fun clearMessageInput() {
        etMessage.text.clear()
    }

    override fun layout() = R.layout.fragment_first
}
