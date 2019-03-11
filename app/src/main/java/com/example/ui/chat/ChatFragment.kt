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
import com.example.data.models.UserChatMessage
import com.example.holders.ChatMessageImageItem
import com.example.holders.ChatMessageItem
import com.example.holders.ChatMessageTextItem
import com.example.holders.QueryPageListGroup
import com.example.ui.base.takePhoto.TakePhotoFragment
import com.example.ui.image.ImageViewFragment
import com.example.util.SnapshotWrappedItemParser
import com.example.util.chat.QueryList
import com.example.util.chat.QueryPageOptions
import com.example.util.chat.SimpleChangeEventListener
import com.firebase.ui.common.ChangeEventType
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.user_chat_avatar.view.*
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

    private val chatGroup = QueryPageListGroup<ChatMessageItem>()
    private val queryListChangeListener = object : SimpleChangeEventListener() {
        override fun onChildChanged(type: ChangeEventType, snapshot: DocumentSnapshot, newIndex: Int, oldIndex: Int) {
            if (type == ChangeEventType.ADDED) presenter.onNewMessage()
        }
    }
    private var chatQueryList: QueryList<ChatMessageItem>? = null

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

        val layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = false
            reverseLayout = true
        }

        rvChat.apply {
            this.layoutManager = layoutManager
            adapter = GroupAdapter<ViewHolder>().apply {
                add(chatGroup)
            }
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    val isBottomPosition = if (newState == SCROLL_STATE_IDLE) {
                        layoutManager.let {
                            if (it.reverseLayout) it.findFirstCompletelyVisibleItemPosition() == 0
                            else it.findLastCompletelyVisibleItemPosition() == adapter?.itemCount?.minus(1)
                        }
                    } else false

                    presenter.onChatScrollChange(isBottomPosition)
                }
            })

            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false

            afterOnGlobalLayout {
                startPostponedEnterTransition()
            }
        }
    }

    override fun setQuery(query: Query, parser: SnapshotParser<UserChatMessage>, pageSize: Int) {
        val imageClickListener = { url: String, imageView: ImageView ->
            presenter.onImageClick(url, imageView)
        }

        val itemParser = SnapshotWrappedItemParser(parser) {
            when {
                !it.message.image.isNullOrBlank() -> ChatMessageImageItem(it, imageClickListener)
                else -> ChatMessageTextItem(it)
            }.apply {
                onBindListener = { presenter.onChatMessageOnScreen(it) }
            }
        }

        chatQueryList?.removeChangeEventListener(queryListChangeListener)
        chatQueryList = QueryList(QueryPageOptions(
                query,
                itemParser,
                pageSize
        )).apply {
            addChangeEventListener(queryListChangeListener)
            chatGroup.setQueryList(this)
        }
    }

    private fun scrollToPosition(position: Int, smooth: Boolean) {
        if (position < 0) return
        if (smooth) rvChat?.smoothScrollToPosition(position)
        else rvChat?.layoutManager?.scrollToPosition(position)
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
        notificationManager.cancel(chatId.toInt())
    }

    override fun showCantSendHolder(isShow: Boolean) {
        flCantSendHolder.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    override fun getPhotoMessageText(onTextFound: (String) -> Unit) {
        onTextFound(getString(R.string.chat_photo_message_text))
    }

    override fun scrollToBottomPosition() = scrollToPosition(0, true)

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

    override fun onDestroy() {
        super.onDestroy()
        chatQueryList?.removeChangeEventListener(queryListChangeListener)
    }

    override fun isShowToolbar() = true
    override fun layout() = R.layout.fragment_chat
}
