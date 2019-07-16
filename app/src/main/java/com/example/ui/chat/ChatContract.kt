package com.example.ui.chat

import android.widget.ImageView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.ui.base.takePhoto.TakePhotoContract
import com.example.util.AddToEndSingleByTagStateStrategy

interface ChatContract {
    interface View : TakePhotoContract.View {
        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showChatInput(animate: Boolean)

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showChatConfirm()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showYouBanUser()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showYouBanned()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input controls")
        fun showWaitForInviteAccetp()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input actions")
        fun showSendGroup()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input actions")
        fun showAttachGroup()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun focusOnInput(showKeyboard: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
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

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showCantSendHolder(isShow: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAvatar(url: String?)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun removeChatMessage(message: ChatMessage)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun checkScrollPosition()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChatBlockConfirmation()
    }

    interface Presenter : TakePhotoContract.Presenter {
        fun onSendTextMessageClick(message: String)
        fun onChatScrollChange(isBottomPosition: Boolean)
        fun onChatMessageOnScreen(message: ChatMessage.Personal)
        fun onImageClick(url: String, imageView: ImageView)
        fun onLoadPreviousMessagesRequest()
        fun onLoadNextMessagesRequest()

        fun onAcceptChatClick()
        fun onBlockChatClick()
        fun onBlockChatConfirm()

        fun onInputShowAnimationFinish()
        fun onMessageInput(message: String)
    }
}
