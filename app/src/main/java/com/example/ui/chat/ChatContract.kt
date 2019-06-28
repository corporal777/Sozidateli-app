package com.example.ui.chat

import android.widget.ImageView
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.ui.base.takePhoto.TakePhotoContract

interface ChatContract {
    interface View : TakePhotoContract.View {
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
    }

    interface Presenter : TakePhotoContract.Presenter {
        fun onSendTextMessageClick(message: String)
        fun onChatScrollChange(isBottomPosition: Boolean)
        fun onChatMessageOnScreen(message: ChatMessage.Personal)
        fun onImageClick(url: String, imageView: ImageView)
        fun onLoadPreviousMessagesRequest()
        fun onLoadNextMessagesRequest()
    }
}
