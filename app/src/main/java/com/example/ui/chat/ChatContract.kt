package com.example.ui.chat

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserChatMessage
import com.example.ui.base.takePhoto.TakePhotoContract
import com.example.util.chat.QueryList

interface ChatContract {
    interface View : TakePhotoContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setQuery(queryList: QueryList<UserChatMessage>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun notifyItemInserted(position: Int)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun notifyItemChanged(position: Int)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun notifyItemRemoved(position: Int)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun notifyItemMoved(oldPosition: Int, newPosition: Int)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun clearMessageInput()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun scrollToBottomPosition()

        @StateStrategyType(SkipStrategy::class)
        fun openImageFullScreen(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun cancelNotificationByChatId(chatId: String)

        @StateStrategyType(SkipStrategy::class)
        fun getPhotoMessageText(onTextFound: (String) -> Unit)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showCantSendHolder(isShow:Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showAvatar(url:String?)

    }

    interface Presenter : TakePhotoContract.Presenter {
        fun onSendTextMessageClick(message: String)
        fun onChatScrollChange(isBottomPosition: Boolean)
        fun onChatMessageOnScreen(message: UserChatMessage)
        fun onImageClick(url: String)
    }
}
