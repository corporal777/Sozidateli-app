package com.example.ui.chat

import android.widget.ImageView
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.Message
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ChatContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showChatInput(animate: Boolean)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showChatConfirm(userName: String?)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showYouBanUser()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showYouBanned()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showWaitForInviteAccept()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input actions")
        fun showSendGroup()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input actions")
        fun showAttachGroup()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun focusOnInput(showKeyboard: Boolean)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun updateMessages(messages: List<ChatMessage>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun clearMessageInput()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun scrollToBottomPosition(smooth: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun scrollToMessagesUnreadItem(position: Int)

        @StateStrategyType(SkipStrategy::class)
        fun openImageFullScreen(url: String, imageView: ImageView)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun cancelNotificationByChatId(chatId: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setUserAvatar(url: String)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setTitle(title: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun removeChatMessage(message: ChatMessage)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun checkScrollPosition()

        @StateStrategyType(SkipStrategy::class)
        fun scrollToPositionWithOffset(position: Int, offset: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChatBlockConfirmation()

        @StateStrategyType(SkipStrategy::class)
        fun showUser(uid: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showEvent(event: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onSendTextMessageClick(message: String)
        fun onChatScrollChange(isBottomPosition: Boolean)
        fun onScrollChange(position: Int, offset: Int)
        fun onChatMessageOnScreen(message: Message)
        fun onImageClick(url: String, imageView: ImageView)
        fun onTakePhotoFromCameraRequest()
        fun onTakePhotoFromGalleryRequest()
        fun onLoadPreviousMessagesRequest(messageId: Int?)
        fun onLoadNextMessagesRequest(messageId: Int?)

        fun onAcceptChatClick()
        fun onBlockChatClick()
        fun onBlockChatConfirm()

        fun onInputShowAnimationFinish()
        fun onMessageInput(message: String)

        fun onUserClick()
    }
}
