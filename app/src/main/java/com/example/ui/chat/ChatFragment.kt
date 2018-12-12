package com.example.ui.chat

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.ChatAdapter
import com.example.data.models.UserChatMessage
import com.example.ui.base.BaseFragment
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_chat.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
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

        chatAdapter = object : ChatAdapter(options) {
            override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
                super.onChildChanged(type, snapshot, newIndex, oldIndex)
                if (type == ChangeEventType.ADDED) presenter.onNewMessage()
            }
        }

        rvChat.adapter = chatAdapter
    }

    private fun scrollToPosition(position: Int, smooth: Boolean) {
        if (position < 0) return
        if (smooth) rvChat.smoothScrollToPosition(position)
        else rvChat.layoutManager?.scrollToPosition(position)
    }

    override fun scrollToLastPosition() = scrollToLastPosition(true)

    private fun scrollToLastPosition(smooth: Boolean) = scrollToPosition(chatAdapter.itemCount - 1, smooth)

    override fun clearMessageInput() {
        etMessage.text.clear()
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_chat
}
