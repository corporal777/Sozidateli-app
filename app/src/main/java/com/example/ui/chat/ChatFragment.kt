package com.example.ui.chat

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.doOnNextLayout
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.*
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.arellomobile.mvp.presenter.ProvidePresenterTag
import com.example.R
import com.example.data.models.ChatMessage
import com.example.data.models.Message.MessageType
import com.example.extensions.dp
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.event.about.old.AboutEventFragmentArgs
import com.example.ui.event.about.redesign.AboutEventFragmentNew.Companion.ABOUT_FROM_OTHER
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.views.toolbar.ToolbarButton
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.PositionOffsetScrollListener
import com.example.util.SimpleTextWatcher
import com.example.util.StayBottomOnLayoutChangeUtil
import com.example.util.pagination.PaginationScrollListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.layout_chat_action_confirmation.view.*
import kotlinx.android.synthetic.main.layout_chat_action_text.view.*
import setCircleImage
import javax.inject.Inject
import javax.inject.Provider

class ChatFragment : BaseFragment(), ChatContract.View, ToolbarFragment {

    @Inject
    lateinit var presenterProvider: Provider<ChatPresenter>

    @InjectPresenter(type = PresenterType.WEAK)
    lateinit var presenter: ChatPresenter

    @ProvidePresenterTag(presenterClass = ChatPresenter::class, type = PresenterType.WEAK)
    fun provideRepositoryPresenterTag(): String? {
        return chatId
    }

    @ProvidePresenter(type = PresenterType.WEAK)
    fun providePresenter(): ChatPresenter = presenterProvider.get().apply {
        val presenter = this
        requireArguments().let { ChatFragmentArgs.fromBundle(it) }.apply {
            presenter.chatId = chatId
            presenter.userAvatar = userAvatar
        }
    }

    override val title = ""

    val chatId: String?
        get() = arguments?.let { ChatFragmentArgs.fromBundle(it).chatId }

    private val chatAdapter = GroupAdapter<GroupieViewHolder>()

    private val imageClickListener = { url: String, imageView: ImageView ->
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                requireActivity(),
                Pair(imageView, imageView.transitionName)
        )

        findNavController().navigate(
                R.id.image_view_activity,
                ImageViewActivityArgs.Builder(url, null, null, imageView.transitionName).build().toBundle(),
                null,
                ActivityNavigatorExtras(options)
        )
    }

    private val bottomScroller by lazy { StayBottomOnLayoutChangeUtil() }

    private lateinit var toolbarContentActionBar: ToolbarContentActionBar

    val imageSpan by lazy {
        ContextCompat.getDrawable(requireContext(), R.drawable.ic_chat_user_expand)?.let {
            it.setBounds(0, 0, 12.dp, 12.dp)
            ImageSpan(it, ImageSpan.ALIGN_BASELINE)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
        setHasOptionsMenu(true)

        btnSend.setOnClickListener { presenter.onSendTextMessageClick(etMessage.text.toString()) }
        btnAttachGallery.setOnClickListener { presenter.onTakePhotoFromGalleryRequest() }
        btnAttachPhoto.setOnClickListener { presenter.onTakePhotoFromCameraRequest() }

        rvChat.apply {
            adapter = chatAdapter
            (itemAnimator as SimpleItemAnimator).apply {
                supportsChangeAnimations = false
                changeDuration = 0
            }
            itemAnimator = null

            addOnScrollListener(PaginationScrollListener(10,
                    {
                        if (adapter?.itemCount != 0) {
                            val id = if (chatAdapter.getItem((adapter?.itemCount ?: 1) - 2) is ChatUnreadLabelItem) {
                                (chatAdapter.getItem((adapter?.itemCount ?: 1) - 3) as ChatMessageItem).message.message._id.toInt()
                            } else {
                                (chatAdapter.getItem((adapter?.itemCount ?: 1) - 2) as ChatMessageItem).message.message._id.toInt()
                            }
                            presenter.onLoadNextMessagesRequest(id)
                        }
                        //presenter.onLoadPreviousMessagesRequest()
                    },
                    {
                        if (adapter?.itemCount != 0) {
                            presenter.onLoadPreviousMessagesRequest((chatAdapter.getItem(0) as ChatMessageItem).message.message._id.toInt())
                        }
                        //presenter.onLoadNextMessagesRequest()
                    }
            ))
            addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                presenter.onScrollChange(position, offset)
            })
            bottomScroller.setupWithRecyclerView(this)

            doOnNextLayout { startPostponedEnterTransition() }
        }

        etMessage.addTextChangedListener(SimpleTextWatcher().setAfterTextChangeRunnable { presenter.onMessageInput(it.toString()) })
    }

    override fun clearMessageInput() = etMessage.text.clear()

    override fun showChatInput(animate: Boolean) {
        if (animate) {
            val transition = AutoTransition().apply {
                addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        presenter.onInputShowAnimationFinish()
                    }
                })
            }
            TransitionManager.beginDelayedTransition(root, transition)
        }

        inputContainer.visibility = View.VISIBLE
        actionContainer.visibility = View.GONE
    }

    override fun showYouBanUser() {
        showActionView(R.layout.layout_chat_action_text, true) {
            textActionContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chat_action_baned_by_you_background))
            tvActionText.text = getString(R.string.chat_banned_by_you)
        }
    }

    override fun showYouBanned() {
        showActionView(R.layout.layout_chat_action_text, true) {
            textActionContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chat_action_you_baned_background))
            tvActionText.text = getString(R.string.chat_you_banned)
        }
    }

    override fun showWaitForInviteAccept() {
        showActionView(R.layout.layout_chat_action_text, true) {
            textActionContainer.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.chat_action_wait_for_accept_background))
            tvActionText.text = getString(R.string.chat_wait_accept)
        }
    }

    override fun showChatConfirm(userName: String?) {
        showActionView(R.layout.layout_chat_action_confirmation, true) {
            tvNeedConfirm.text = userName
                    ?.takeIf { it.isNotBlank() }
                    ?.let { getString(R.string.chat_need_confirm_user_name, it) }
                    ?: getString(R.string.chat_need_confirm)

            btnConfirm.setOnClickListener { presenter.onAcceptChatClick() }
            btnBlock.setOnClickListener { presenter.onBlockChatClick() }
        }
    }

    private fun showActionView(@LayoutRes layout: Int, animate: Boolean, viewApply: View.() -> Unit) {
        actionContainer.removeAllViews()
        layoutInflater.inflate(layout, actionContainer).apply(viewApply)

        if (animate) {
            TransitionManager.beginDelayedTransition(root, Slide(Gravity.BOTTOM))
        }
        actionContainer.visibility = View.VISIBLE
        inputContainer.visibility = View.GONE
    }

    override fun showChatBlockConfirmation() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.user_ban_confirmation_title)
                .setPositiveButton(R.string.ok) { _, _ -> presenter.onBlockChatConfirm() }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun focusOnInput(showKeyboard: Boolean) {
        etMessage.apply {
            post {
                showSoftInputOnFocus = showKeyboard
                requestFocus()
                showSoftInputOnFocus = true
                if (showKeyboard) showKeyboard(this)
            }
        }
    }

    override fun showSendGroup() {
        if (sendGroup.visibility == View.VISIBLE && attachGroup.visibility == View.GONE) return
        TransitionManager.beginDelayedTransition(inputContainer, getInputActionTransition())
        sendGroup.visibility = View.VISIBLE
        attachGroup.visibility = View.GONE
    }

    override fun showAttachGroup() {
        if (attachGroup.visibility == View.VISIBLE && sendGroup.visibility == View.GONE) return
        TransitionManager.beginDelayedTransition(inputContainer, getInputActionTransition())
        attachGroup.visibility = View.VISIBLE
        sendGroup.visibility = View.GONE
    }

    private fun getInputActionTransition(): Transition {
        return TransitionSet().apply {
            ordering = TransitionSet.ORDERING_SEQUENTIAL
            addTransition(Fade(Fade.OUT))
            addTransition(ChangeBounds())
            addTransition(Fade(Fade.IN))
            duration = 100
        }
    }

    override fun updateMessages(messages: List<ChatMessage>) {
        chatAdapter.update(messages.map {
            when (it) {
                is ChatMessage.Personal -> {
                    val item = when (it.message.type) {
                        MessageType.IMAGE -> ChatMessageImageItem(it, imageClickListener)
                        else -> ChatMessageTextItem(it)
                    }

                    item.apply { onBindListener = { presenter.onChatMessageOnScreen(message.message) } }
                }
                is ChatMessage.NewMessages -> ChatUnreadLabelItem(it.count)
                is ChatMessage.Date -> ChatDateItem(it.date)
                is ChatMessage.Accept -> ChatAcceptItem { it.message.let { message -> presenter.onChatMessageOnScreen(message) } }
            }
        })
    }

    override fun removeChatMessage(message: ChatMessage) {
        for (i in 0 until chatAdapter.itemCount) {
            val item = chatAdapter.getItem(i)
            if (checkItemIsSameMessage(message, item)) {
                chatAdapter.remove(item)
                break
            }
        }
    }

    private fun checkItemIsSameMessage(message: ChatMessage, item: Item<*>): Boolean {
        return when (item) {
            is ChatMessageItem -> item.message == message
            is ChatUnreadLabelItem -> message is ChatMessage.NewMessages
            else -> false
        }
    }

    override fun cancelNotificationByChatId(chatId: String) {
        val notificationManager = activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(chatId.hashCode())
    }

    override fun scrollToBottomPosition(smooth: Boolean) = scrollToPosition(0, smooth)

    override fun scrollToMessagesUnreadItem(position: Int) {
        val height = rvChat.height
        scrollToPositionWithOffset(position, height - height / 4)
    }

    private fun scrollToPosition(position: Int, smooth: Boolean) {
        if (position < 0) return
        if (smooth) rvChat?.smoothScrollToPosition(position)
        else rvChat?.layoutManager?.scrollToPosition(position)
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        bottomScroller.isEnabled = false
        (rvChat.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, offset)
        bottomScroller.isEnabled = true
    }

    override fun checkScrollPosition() {
        presenter.onChatScrollChange(isChatScrolledToBottom())
    }

    private fun isChatScrolledToBottom(): Boolean {
        return (rvChat.layoutManager as LinearLayoutManager).let {
            if (it.reverseLayout) it.findFirstCompletelyVisibleItemPosition() == 0
            else it.findLastCompletelyVisibleItemPosition() == rvChat.adapter?.itemCount?.minus(1)
        }
    }

    override fun openImageFullScreen(url: String, imageView: ImageView) {
        val transitionName = imageView.transitionName
        findNavController().navigate(
                R.id.image_view_activity,
                ImageViewActivityArgs.Builder(url, null, null, transitionName).build().toBundle(),
                null,
                FragmentNavigatorExtras(imageView to transitionName)
        )
    }

    override fun showUser(uid: Int) {
        findNavController().navigate(ChatFragmentDirections.actionChatFragmentToUserFragment(uid.toString()))
    }

    override fun showEvent(event: String) {
        findNavController().navigate(R.id.about_event_fragment, AboutEventFragmentArgs.Builder(event, ABOUT_FROM_OTHER).build().toBundle())
    }

    override fun setUserAvatar(url: String) {
        toolbarContentActionBar.removeAllRightViews()
        ToolbarButton(requireContext()).apply {
            setCircleImage(url)
            isEnabled = false
            isClickable = false
            toolbarContentActionBar.addRightView(this)
        }
    }

    override fun setTitle(title: String) {
        toolbarContentActionBar.apply {
            getTitleView { setTitle(ellipsizeTitle(this, title)) }
        }
    }

    private fun ellipsizeTitle(titleView: TextView, title: CharSequence): CharSequence {
        return titleView.let {
            val titleSpannable = SpannableStringBuilder(title).apply {
                if (imageSpan != null) append("  ").setSpan(imageSpan, length - 1, length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            }
            TextUtils.ellipsize(titleSpannable, it.paint, (it.width - it.paddingRight - it.paddingLeft).toFloat(), TextUtils.TruncateAt.END)
        }
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
        toolbarContentActionBar.apply {
            setOnToolbarClickListener { presenter.onUserClick() }
        }
    }

    override fun hideKeyboard() {
        super.hideKeyboard(etMessage)
    }

    override fun onDetach() {
        super.onDetach()
        toolbarContentActionBar.apply { setOnToolbarClickListener(null) }
    }

    override fun layout() = R.layout.fragment_chat
}
