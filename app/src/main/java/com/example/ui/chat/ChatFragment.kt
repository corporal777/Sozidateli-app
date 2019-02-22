package com.example.ui.chat

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.ChatAdapter
import com.example.adapters.ViewHolder
import com.example.data.models.UserChatMessage
import com.example.ui.base.takePhoto.TakePhotoFragment
import com.example.ui.chat.fullScreenDialogImage.FullScreenImageDialogFragment
import com.firebase.ui.firestore.SnapshotParser
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.item_chat_message_incoming.view.*
import javax.inject.Inject
import javax.inject.Provider


class ChatFragment : TakePhotoFragment<ChatContract.View, ChatPresenter>(), ChatContract.View {

    @InjectPresenter
    override lateinit var presenter: ChatPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatPresenter>

    @ProvidePresenter
    fun providePresenter(): ChatPresenter = presenterProvider.get().apply {
        val args = ChatFragmentArgs.fromBundle(arguments!!)
        chatId = args.chatId
        userId = args.userId
        this@ChatFragment.chatId = args.chatId
    }

    lateinit var chatId: String

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSend.setOnClickListener { presenter.onSendTextMessageClick("${etMessage.text}") }
        btnAttach.setOnClickListener { presenter.onTakePhotoRequest() }

        val layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = false
            reverseLayout = true
        }
        rvChat.apply {
            this.layoutManager = layoutManager
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    val isLastPosition = if (newState == SCROLL_STATE_IDLE) {
                        layoutManager.findLastCompletelyVisibleItemPosition() == adapter?.itemCount?.minus(1)
                    } else false

                    presenter.onChatScrollChange(isLastPosition)
                }
            })
        }
    }

    override fun iniChatAdapter(query: Query, parser: SnapshotParser<UserChatMessage>) {
        val config = PagedList.Config.Builder()
                .setInitialLoadSizeHint(20)
                .setPageSize(20)
                .setEnablePlaceholders(false)
                .build()

        val options = FirestorePagingOptions.Builder<UserChatMessage>()
                .setLifecycleOwner(this)
                .setQuery(query, config, parser)
                .build()

        chatAdapter = object : ChatAdapter(options) {
//            override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
//                super.onChildChanged(type, snapshot, newIndex, oldIndex)
//                if (type == ChangeEventType.ADDED) presenter.onNewMessage(parser.parseSnapshot(snapshot))
//            }



            override fun onBindViewHolder(holder: ViewHolder, position: Int, model: UserChatMessage) {
                super.onBindViewHolder(holder, position, model)
                presenter.onChatMessageOnScreen(model)

                holder.itemView.ivImage.setOnClickListener {
                    model.message.image?.let { url ->
                        presenter.onImageClick(url)
                    }
                }
            }
        }

        rvChat.adapter = chatAdapter
    }

    private fun scrollToPosition(position: Int, smooth: Boolean) {
        if (position < 0) return
        if (smooth) rvChat.smoothScrollToPosition(position)
        else rvChat.layoutManager?.scrollToPosition(position)
    }

    override fun openImageFullScreen(url: String) {
        val dialog = FullScreenImageDialogFragment.newInstance(url)
        val ft = childFragmentManager.beginTransaction()
        dialog.show(ft, FullScreenImageDialogFragment.TAG)
    }

    override fun cancelNotificationByChatId(chatId: String) {
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(chatId.toInt())
    }

    override fun scrollToLastPosition() = scrollToPosition(chatAdapter.itemCount - 1, true)

    override fun clearMessageInput() = etMessage.text.clear()

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_chat
}
