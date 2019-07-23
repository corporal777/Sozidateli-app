package com.example.ui.chat

import afterOnGlobalLayout
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.*
import bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.ChatMessage
import com.example.holders.*
import com.example.ui.base.takePhoto.TakePhotoFragment
import com.example.ui.image.ImageViewFragment
import com.example.util.SimpleTextWatcher
import com.example.util.pagination.PaginationScrollListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.layout_chat_action_confirmation.view.*
import kotlinx.android.synthetic.main.layout_chat_action_text.view.*
import ru.houseofapps.chat.models.Message
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
    }

    private val chatAdapter = GroupAdapter<ViewHolder>()

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
        flCantSendHolder.setOnTouchListener { _, _ -> return@setOnTouchListener true }

        btnSend.setOnClickListener { presenter.onSendTextMessageClick(etMessage.text.toString()) }
        btnAttachGallery.setOnClickListener { presenter.onTakePhotoFromGalleryRequest() }
        btnAttachPhoto.setOnClickListener { presenter.onTakePhotoFromCameraRequest() }

        rvChat.apply {
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

        etMessage.addTextChangedListener(SimpleTextWatcher().setAfterTextChangeRunnable { presenter.onMessageInput(it.toString()) })
    }

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

    override fun showChatConfirm() {
        showActionView(R.layout.layout_chat_action_confirmation, true) {
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

    override fun focusOnInput(showKeyboard: Boolean) {
        etMessage.apply {
            showSoftInputOnFocus = showKeyboard
            requestFocus()
            showSoftInputOnFocus = true
            if (showKeyboard) showKeyboard(this)
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
            is ChatUnreadLabelItem -> message.type == ChatMessage.Service.Type.NEW_MESSAGES
            else -> false
        }
    }

    private fun getItemForChatServiceMessage(message: ChatMessage.Service): Item<*> {
        return when (message.type) {
            ChatMessage.Service.Type.NEW_MESSAGES -> ChatUnreadLabelItem()
            ChatMessage.Service.Type.ACCEPT -> ChatAcceptItem()
            ChatMessage.Service.Type.NO_TYPE -> ChatEmptyItem()
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

    override fun checkScrollPosition() {
        presenter.onChatScrollChange(isChatScrolledToBottom())
    }

    override fun showChatBlockConfirmation() {
        AlertDialog.Builder(requireContext())
                .setTitle(R.string.user_ban_confirmation_title)
                .setMessage(R.string.user_ban_confirmation_message)
                .setPositiveButton(R.string.ok) { _, _ -> presenter.onBlockChatConfirm() }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun scrollToBottomPosition(smooth: Boolean) = scrollToPosition(0, smooth)

    override fun scrollToMessagesUnreadItem(position: Int) {
        val height = rvChat.height
        (rvChat.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(position, height - height / 4)
    }

    override fun clearMessageInput() = etMessage.text.clear()

    override fun setUserAvatar(avatar: Bitmap) {
        setToolbarLogo(avatar)
    }

    override fun setUserAvatarPlaceholder() {
        setToolbarLogo((ContextCompat.getDrawable(requireContext(), R.drawable.ic_launcher) as BitmapDrawable).bitmap)
    }

    private fun setToolbarLogo(logo: Bitmap) {
//        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
//            val size = resources.getDimensionPixelSize(R.dimen.event_schedule_sub_event_divider_height)
//            setIcon(Bitmap.createScaledBitmap(logo, size, size, true).toDrawable(resources).apply {
//                setBounds(0, 0, size, size)
//            })
//            setDisplayUseLogoEnabled(true)
//        }
    }

    override fun removeUserAvatar() {
        (requireActivity() as AppCompatActivity).supportActionBar?.apply {
            setLogo(null)
            setDisplayUseLogoEnabled(false)
        }
    }

    private fun isChatScrolledToBottom(): Boolean {
        return (rvChat.layoutManager as LinearLayoutManager).let {
            if (it.reverseLayout) it.findFirstCompletelyVisibleItemPosition() == 0
            else it.findLastCompletelyVisibleItemPosition() == rvChat.adapter?.itemCount?.minus(1)
        }
    }

    override fun layout() = R.layout.fragment_chat
}
