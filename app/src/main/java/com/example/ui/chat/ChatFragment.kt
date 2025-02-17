package com.example.ui.chat

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.core.view.doOnNextLayout
import androidx.navigation.ActivityNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.Slide
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import com.example.app.R
import com.example.app.databinding.FragmentChatBinding
import com.example.data.models.ChatMessage
import com.example.data.models.Message
import com.example.extensions.findItemBy
import com.example.extensions.updateItem
import com.example.holders.ChatAcceptItem
import com.example.holders.ChatDateItem
import com.example.holders.ChatMessageImageItem
import com.example.holders.ChatMessageTextItem
import com.example.holders.ChatUnreadLabelItem
import com.example.holders.PlaceholderItem
import com.example.ui.base.BaseVBFragment
import com.example.ui.event.about.AboutEventFragmentArgs
import com.example.ui.event.list.recommendations.items.NoEventItem
import com.example.ui.image.ImageViewActivityArgs
import com.example.ui.user.UserFragmentArgs
import com.example.ui.views.dialogs.DefaultAlertDialog
import com.example.util.SimpleTextWatcher
import com.example.util.pagination.PaginationListGroupAdapter
import com.example.util.setCircleAvatar
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import moxy.presenter.ProvidePresenterTag
import javax.inject.Inject
import javax.inject.Provider

class ChatFragment : BaseVBFragment<FragmentChatBinding>(), ChatContract.View {

    private var animCounter = 0

    @Inject
    lateinit var presenterProvider: Provider<ChatPresenter>

    @InjectPresenter
    lateinit var presenter: ChatPresenter

    @ProvidePresenterTag(presenterClass = ChatPresenter::class)
    fun provideRepositoryPresenterTag(): String? {
        return chatId
    }

    @ProvidePresenter
    fun providePresenter(): ChatPresenter = presenterProvider.get().apply {
        val presenter = this
        requireArguments().let { ChatFragmentArgs.fromBundle(it) }.apply {
            presenter.chatId = chatId
            presenter.userAvatar = userAvatar ?: ""
            presenter.userName = name
        }
    }


    val chatId: String?
        get() = arguments?.let { ChatFragmentArgs.fromBundle(it).chatId }

    private val imageClickListener = { url: String, imageView: ImageView ->
        val opt = ActivityOptionsCompat.makeSceneTransitionAnimation(
            requireActivity(),
            Pair(imageView, imageView.transitionName)
        )

        findNavController().navigate(
            R.id.image_view_activity,
            ImageViewActivityArgs.Builder(url, null, null, imageView.transitionName).build()
                .toBundle(),
            null,
            ActivityNavigatorExtras(opt)
        )
    }

    private val chatAdapter by lazy {
        PaginationListGroupAdapter<GroupieViewHolder>().apply {
            add(chatSection)
            setOnItemTakeCallback(object : PaginationListGroupAdapter.OnItemTakeCallback {
                override fun onItemTake(position: Int) {
                    presenter.onItemTake(position)
                }
            })
        }
    }

    private val chatSection by lazy { Section() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            rvChat.apply {
                adapter = chatAdapter
                (itemAnimator as SimpleItemAnimator).apply {
                    supportsChangeAnimations = false
                    changeDuration = 0
                }
                doOnNextLayout { startPostponedEnterTransition() }
            }
            etMessage.apply {
                addTextChangedListener(SimpleTextWatcher().setAfterTextChangeRunnable {
                    presenter.onMessageInput(it.toString())
                })
            }
            btnSend.setOnClickListener { presenter.onSendTextMessageClick(etMessage.text.toString()) }
            btnAttachGallery.setOnClickListener { presenter.onTakePhotoFromGalleryRequest() }
            btnAttachPhoto.setOnClickListener { presenter.onTakePhotoFromCameraRequest() }
            ivAvatar.setOnClickListener { presenter.onUserClick() }
            ivBack.setOnClickListener { findNavController().navigateUp() }
        }
    }

    override fun setChatPlaceholder() {
        chatSection.updateItem(PlaceholderItem(PlaceholderItem.Type.CHAT))
    }

    override fun showEmptyChatPlaceholder() {
        chatSection.updateItem(NoEventItem(getString(R.string.messages_not_found)))
    }

    override fun updateMessages(showAnim: Boolean, messages: List<ChatMessage>) {
        chatSection.update(messages.map {
            if (animCounter < 8) animCounter++

            when (it) {
                is ChatMessage.NewMessages -> {
                    ChatUnreadLabelItem(it.count)
                }
                is ChatMessage.Personal -> {
                    val item = when (it.message.type) {
                        Message.MessageType.IMAGE -> ChatMessageImageItem(it, imageClickListener)
                        else -> ChatMessageTextItem(it)
                    }

                    item.apply {
                        onBindListener = { presenter.onChatMessageOnScreen(message.message) }
                    }
                }
                is ChatMessage.Date -> ChatDateItem(it.date)
                is ChatMessage.Accept -> ChatAcceptItem {
                    it.message.let { message ->
                        presenter.onChatMessageOnScreen(message)
                    }
                }
            }
        })
    }

    override fun removeUnreadMessageLabel() {
        val unreadLabelItem = chatSection.findItemBy<ChatUnreadLabelItem> { true }
        if (unreadLabelItem != null) {
            chatSection.remove(unreadLabelItem)
        }
        val acceptLabelItem = chatSection.findItemBy<ChatAcceptItem> { true }
        if (acceptLabelItem != null) {
            chatSection.remove(acceptLabelItem)
        }
    }


    override fun scrollListToPosition(position: Int, smooth: Boolean) {
        if (smooth) mBinding.rvChat.smoothScrollToPosition(position)
        else mBinding.rvChat.scrollToPosition(0)
    }

    override fun setUserNameAvatar(url: String, name: String) {
        mBinding.ivAvatar.apply {
            setCircleAvatar(url)
        }
        mBinding.tvUserName.text = name
    }

    override fun showUser(userId: String) {
        findNavController().navigate(
            R.id.user_fragment,
            UserFragmentArgs.Builder(userId).build().toBundle()
        )
    }

    override fun showEvent(event: String) {
        findNavController().navigate(
            R.id.about_event_fragment,
            AboutEventFragmentArgs.Builder(event).build().toBundle()
        )
    }

    override fun showChatInput(animate: Boolean) {
        mBinding.inputContainer.visibility = View.VISIBLE
        mBinding.actionContainer.visibility = View.GONE
    }

    override fun showChatConfirm(userName: String?) {
        showActionView(R.layout.layout_chat_action_confirmation, true) {
            findViewById<TextView>(R.id.tvNeedConfirm).text = userName
                ?.takeIf { it.isNotBlank() }
                ?.let { getString(R.string.chat_need_confirm_user_name, it) }
                ?: getString(R.string.chat_need_confirm)

            findViewById<TextView>(R.id.btnConfirm).setOnClickListener { presenter.onAcceptChatClick() }
            findViewById<TextView>(R.id.btnBlock).setOnClickListener { presenter.onBlockChatClick() }
        }
    }

    override fun showYouBanUser() {
        showActionView(R.layout.layout_chat_action_text, true) {
            findViewById<TextView>(R.id.textActionContainer).setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.chat_action_baned_by_you_background
                )
            )
            findViewById<TextView>(R.id.tvActionText).text = getString(R.string.chat_banned_by_you)
        }
    }

    override fun showYouBanned() {
        showActionView(R.layout.layout_chat_action_text, true) {
            findViewById<TextView>(R.id.textActionContainer).setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.chat_action_you_baned_background
                )
            )
            findViewById<TextView>(R.id.tvActionText).text = getString(R.string.chat_you_banned)
        }
    }

    override fun showWaitForInviteAccept() {
        showActionView(R.layout.layout_chat_action_text, true) {
            findViewById<TextView>(R.id.textActionContainer).setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.chat_action_wait_for_accept_background
                )
            )
            findViewById<TextView>(R.id.tvActionText).text = getString(R.string.chat_wait_accept)
        }
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
        DefaultAlertDialog(
            context = requireContext(),
            title = null,
            message = getString(R.string.user_ban_confirmation_title),
            positiveText = getString(R.string.yes),
            negativeText = getString(R.string.no),
        ).setSelectCallback { presenter.onBlockChatConfirm() }
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

    override fun clearMessageInput() = mBinding.etMessage.text.clear()
    override fun cancelNotificationByChatId(chatId: String) {
        val notificationManager =
            activity?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(chatId.hashCode())
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

    override fun binding() = FragmentChatBinding::class.java
    override fun layout(): Int = R.layout.fragment_chat
}