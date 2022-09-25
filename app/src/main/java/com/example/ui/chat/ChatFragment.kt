package com.example.ui.chat

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.text.style.ImageSpan
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.doOnNextLayout
import androidx.core.view.isVisible
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
import com.example.databinding.FragmentChatBinding
import com.example.extensions.dp
import com.example.holders.*
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragmentNew
import com.example.ui.event.about.old.AboutEventFragmentArgs
import com.example.ui.event.about.redesign.AboutEventFragmentNew.Companion.ABOUT_FROM_OTHER
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.views.CustomProgressView
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.example.util.PositionOffsetScrollListener
import com.example.util.SimpleTextWatcher
import com.example.util.StayBottomOnLayoutChangeUtil
import com.example.util.pagination.PaginationScrollListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import kotlinx.android.synthetic.main.layout_chat_action_confirmation.*
import kotlinx.android.synthetic.main.layout_chat_action_text.*

import setCircleImage
import javax.inject.Inject
import javax.inject.Provider

class ChatFragment : BaseFragmentNew<FragmentChatBinding>(), ChatContract.View, ToolbarFragment {

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
            ImageViewActivityArgs.Builder(url, null, null, imageView.transitionName).build()
                .toBundle(),
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
        mBinding.apply {
            btnSend.setOnClickListener { presenter.onSendTextMessageClick(etMessage.text.toString()) }
            btnAttachGallery.setOnClickListener { presenter.onTakePhotoFromGalleryRequest() }
            btnAttachPhoto.setOnClickListener { presenter.onTakePhotoFromCameraRequest() }

            rvChat.apply {
                adapter = chatAdapter
                (itemAnimator as SimpleItemAnimator).apply {
                    supportsChangeAnimations = false
                    changeDuration = 0
                }
                //itemAnimator = null

                addOnScrollListener(PaginationScrollListener(10,
                    {
                        if (adapter?.itemCount != 0) {
                            val id = if (chatAdapter.getItem(
                                    (adapter?.itemCount ?: 1) - 2
                                ) is ChatUnreadLabelItem
                            ) {
                                (chatAdapter.getItem(
                                    (adapter?.itemCount ?: 1) - 3
                                ) as ChatMessageItem).message.message._id.toInt()
                            } else {
                                (chatAdapter.getItem(
                                    (adapter?.itemCount ?: 1) - 2
                                ) as ChatMessageItem).message.message._id.toInt()
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
                    }, { scroll ->
                        Log.e("SCROLL", scroll.toString())
                    }
                ))
                addOnScrollListener(PositionOffsetScrollListener { position, offset ->
                    presenter.onScrollChange(position, offset)
                })
                bottomScroller.setupWithRecyclerView(this)

                doOnNextLayout { startPostponedEnterTransition() }
            }

            etMessage.apply {
                addTextChangedListener(SimpleTextWatcher().setAfterTextChangeRunnable {
                    presenter.onMessageInput(
                        it.toString()
                    )
                })
            }
        }

        mBinding.tvUserName.setOnClickListener {
            presenter.onUserClick()
        }
        mBinding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun clearMessageInput() = mBinding.etMessage.text.clear()

    override fun showChatInput(animate: Boolean) {
        if (animate) {
            val transition = AutoTransition().apply {
                addListener(object : TransitionListenerAdapter() {
                    override fun onTransitionEnd(transition: Transition) {
                        presenter.onInputShowAnimationFinish()
                    }
                })
            }
            TransitionManager.beginDelayedTransition(mBinding.root, transition)
        }

        mBinding.inputContainer.visibility = View.VISIBLE
        mBinding.actionContainer.visibility = View.GONE
    }

    override fun showYouBanUser() {
        showActionView(R.layout.layout_chat_action_text, true) {
            textActionContainer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.chat_action_baned_by_you_background
                )
            )
            tvActionText.text = getString(R.string.chat_banned_by_you)
        }
    }

    override fun showYouBanned() {
        showActionView(R.layout.layout_chat_action_text, true) {
            textActionContainer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.chat_action_you_baned_background
                )
            )
            tvActionText.text = getString(R.string.chat_you_banned)
        }
    }

    override fun showWaitForInviteAccept() {
        showActionView(R.layout.layout_chat_action_text, true) {
            textActionContainer.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.chat_action_wait_for_accept_background
                )
            )
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

    private fun showActionView(
        @LayoutRes layout: Int,
        animate: Boolean,
        viewApply: View.() -> Unit
    ) {
        mBinding.actionContainer.removeAllViews()
        layoutInflater.inflate(layout, mBinding.actionContainer).apply(viewApply)

        if (animate) {
            TransitionManager.beginDelayedTransition(mBinding.root, Slide(Gravity.BOTTOM))
        }
        mBinding.actionContainer.visibility = View.VISIBLE
        mBinding.inputContainer.visibility = View.GONE
    }

    override fun showChatBlockConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.user_ban_confirmation_title)
            .setPositiveButton(R.string.ok) { _, _ -> presenter.onBlockChatConfirm() }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    override fun focusOnInput(showKeyboard: Boolean) {
        mBinding.etMessage.apply {
            post {
                showSoftInputOnFocus = showKeyboard
                requestFocus()
                showSoftInputOnFocus = true
                if (showKeyboard) showKeyboard(this)
            }
        }
    }

    override fun showSendGroup() {
        if (mBinding.sendGroup.visibility == View.VISIBLE && mBinding.attachGroup.visibility == View.GONE) return
        TransitionManager.beginDelayedTransition(
            mBinding.inputContainer,
            getInputActionTransition()
        )
        mBinding.sendGroup.visibility = View.VISIBLE
        mBinding.attachGroup.visibility = View.GONE
    }

    override fun showAttachGroup() {
        if (mBinding.attachGroup.visibility == View.VISIBLE && mBinding.sendGroup.visibility == View.GONE) return
        TransitionManager.beginDelayedTransition(
            mBinding.inputContainer,
            getInputActionTransition()
        )
        mBinding.attachGroup.visibility = View.VISIBLE
        mBinding.sendGroup.visibility = View.GONE
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

                    item.apply {
                        onBindListener = { presenter.onChatMessageOnScreen(message.message) }
                    }
                }
                is ChatMessage.NewMessages -> {
                    ChatUnreadLabelItem(it.count)
                }
                is ChatMessage.Date -> ChatDateItem(it.date)
                is ChatMessage.Accept -> ChatAcceptItem {
                    it.message.let { message ->
                        presenter.onChatMessageOnScreen(
                            message
                        )
                    }
                }
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
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(chatId.hashCode())
    }

    override fun scrollToBottomPosition(smooth: Boolean) = scrollToPosition(0, smooth)

    override fun scrollToMessagesUnreadItem(position: Int) {
        val height = mBinding.rvChat.height
        scrollToPositionWithOffset(position, height - height / 4)
    }

    private fun scrollToPosition(pos: Int, smooth: Boolean) {
        if (pos < 0) return
        if (smooth) mBinding.rvChat?.smoothScrollToPosition(pos)
        else mBinding.rvChat?.layoutManager?.scrollToPosition(pos)
    }

    override fun scrollToPositionWithOffset(position: Int, offset: Int) {
        bottomScroller.isEnabled = false
        (mBinding.rvChat.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
            position,
            offset
        )
        bottomScroller.isEnabled = true
    }

    override fun checkScrollPosition() {
        presenter.onChatScrollChange(isChatScrolledToBottom())
    }

    private fun isChatScrolledToBottom(): Boolean {
        return (mBinding.rvChat.layoutManager as LinearLayoutManager).let {
            if (it.reverseLayout) it.findFirstCompletelyVisibleItemPosition() == 0
            else it.findLastCompletelyVisibleItemPosition() == mBinding.rvChat.adapter?.itemCount?.minus(
                1
            )
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
        findNavController().navigate(
            R.id.about_event_fragment,
            AboutEventFragmentArgs.Builder(event, ABOUT_FROM_OTHER).build().toBundle()
        )
    }

    override fun showProgressLoadingDisplay() {
        val mProgressView = CustomProgressView(requireContext())
        mProgressView.setSize(35.dp)
        mProgressView.setProgressColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.main_brown_color_new
            )
        )
        mBinding.loadingContainer.isVisible = true
        mBinding.progressViewContainer.addView(mProgressView, 0)
    }

    override fun hideProgressLoadingDisplay() {
        mBinding.loadingContainer.isVisible = false
        mBinding.progressViewContainer.removeAllViews()
    }

    override fun setUserAvatar(url: String) {
        mBinding.ivAvatar.apply {
            setCircleImage(url)
            isEnabled = false
            isClickable = false
        }
    }

    override fun setTitle(title: String) {
        mBinding.tvUserName.text = title
    }


    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
    }

    override fun hideKeyboard() {
        super.hideKeyboard(mBinding.etMessage)
    }

    override fun layout() = R.layout.fragment_chat
}
