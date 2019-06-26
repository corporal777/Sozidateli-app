package com.example.ui.chat

import afterOnGlobalLayout
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE
import androidx.recyclerview.widget.SimpleItemAnimator
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.ChatMessage
import com.example.holders.ChatMessageImageItem
import com.example.holders.ChatMessageItem
import com.example.holders.ChatMessageTextItem
import com.example.holders.ChatUnreadLabel
import com.example.ui.base.takePhoto.TakePhotoFragment
import com.example.ui.image.ImageViewFragment
import com.example.util.pagination.PaginationScrollListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.user_chat_avatar.view.*
import ru.houseofapps.chat.models.Message
import setCircleImageWithPlaceholder
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
    }

    private val chatAdapter = GroupAdapter<ViewHolder>()

    private val scrollToBottomListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            val isBottomPosition = if (newState == SCROLL_STATE_IDLE) {
                layoutManager.let {
                    if (it.reverseLayout) it.findFirstCompletelyVisibleItemPosition() == 0
                    else it.findLastCompletelyVisibleItemPosition() == recyclerView.adapter?.itemCount?.minus(1)
                }
            } else false

            presenter.onChatScrollChange(isBottomPosition)
        }
    }

    val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context).apply {
            stackFromEnd = false
            reverseLayout = true
        }
    }

    private val imageClickListener = { url: String, imageView: ImageView ->
        presenter.onImageClick(url, imageView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        setHasOptionsMenu(true)
        btnSend.setOnClickListener { presenter.onSendTextMessageClick("${etMessage.text}") }
        btnAttach.setOnClickListener { presenter.onTakePhotoRequest() }
        flCantSendHolder.setOnTouchListener { _, _ -> return@setOnTouchListener true }

        rvChat.apply {
            this.layoutManager = this@ChatFragment.layoutManager
            adapter = chatAdapter

            addOnScrollListener(PaginationScrollListener(10,
                    { presenter.onLoadPreviousMessagesRequest() },
                    { presenter.onLoadNextMessagesRequest() }
            ))

            (itemAnimator as SimpleItemAnimator).apply {
                supportsChangeAnimations = false
                changeDuration = 0
            }
            itemAnimator = null

            afterOnGlobalLayout { startPostponedEnterTransition() }
        }
    }

    private fun scrollToPosition(position: Int, smooth: Boolean) {
        if (position < 0) return
        if (smooth) rvChat?.smoothScrollToPosition(position)
        else rvChat?.layoutManager?.scrollToPosition(position)
    }

    override fun updateMessages(messages: List<ChatMessage>) {
        chatAdapter.update(messages.map {
            when (it) {
                is ChatMessage.Personal -> {
                    val item = when (it.message.type) {
                        Message.Type.IMAGE -> ChatMessageImageItem(it, imageClickListener)
                        else -> ChatMessageTextItem(it)
                    }

                    item.apply { onBindListener = { presenter.onChatMessageOnScreen(message) } }
                }
                is ChatMessage.Service -> getItemForChatServiceMessage(it)
            }
        })
    }

    override fun enableBottomScrollListener() {
        rvChat.addOnScrollListener(scrollToBottomListener)
    }

    override fun removeChatMessage(message: ChatMessage) {
        for (i in 0 until chatAdapter.itemCount) {
            val item = chatAdapter.getItem(i)
            val isSameMessage = when (message) {
                is ChatMessage.Personal -> checkItemIsSamePersonalMessage(message, item)
                is ChatMessage.Service -> checkItemIsSameServiceMessage(message, item)
            }

            if (isSameMessage) {
                chatAdapter.remove(item)
                break
            }
        }
    }

    private fun checkItemIsSamePersonalMessage(message: ChatMessage.Personal, item: Item<*>): Boolean {
        return when (item) {
            is ChatMessageItem -> item.message == message
            else -> false
        }
    }

    private fun checkItemIsSameServiceMessage(message: ChatMessage.Service, item: Item<*>): Boolean {
        return when (item) {
            is ChatUnreadLabel -> message.type == ChatMessage.Service.Type.NEW_MESSAGES
            else -> false
        }
    }

    private fun getItemForChatServiceMessage(message: ChatMessage.Service): Item<*> {
        return when (message.type) {
            ChatMessage.Service.Type.NEW_MESSAGES -> ChatUnreadLabel()
        }
    }

    override fun openImageFullScreen(url: String, imageView: ImageView) {
        val transitionName = imageView.transitionName
        findNavController().navigate(
                R.id.image_view_fragment,
                bundleOf(
                        ImageViewFragment.ARG_IMAGE_URL to url,
                        ImageViewFragment.ARG_TRANSITION_NAME to transitionName
                ),
                null,
                FragmentNavigatorExtras(imageView to transitionName)
        )
    }

    override fun cancelNotificationByChatId(chatId: String) {
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(chatId.hashCode())
    }

    override fun showCantSendHolder(isShow: Boolean) {
        flCantSendHolder.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun scrollToBottomPosition() = scrollToPosition(0, true)

    override fun scrollToMessagesUnreadItem(position: Int) {
        layoutManager.scrollToPositionWithOffset(position, 0)
    }

    override fun clearMessageInput() = etMessage.text.clear()

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_chat, menu)
        // val avatarMenu = menu.findItem(R.event_id.avatar)
    }

    override fun showAvatar(url: String?) {
        val avatarView = activity?.findViewById<View>(R.id.avatar)
        avatarView?.ivAvatar?.setCircleImageWithPlaceholder(url, R.drawable.avatar_placeholder)
    }

    override fun isShowToolbar() = true
    override fun layout() = R.layout.fragment_chat
}
