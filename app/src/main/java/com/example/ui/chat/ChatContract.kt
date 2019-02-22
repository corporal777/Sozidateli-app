package com.example.ui.chat

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.UserChatMessage
import com.example.ui.base.takePhoto.TakePhotoContract
import com.firebase.ui.firestore.SnapshotParser
import com.google.firebase.firestore.Query

interface ChatContract {
    interface View : TakePhotoContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun iniChatAdapter(query: Query, parser: SnapshotParser<UserChatMessage>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun clearMessageInput()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun scrollToLastPosition()

        @StateStrategyType(SkipStrategy::class)
        fun openImageFullScreen(url: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun cancelNotificationByChatId(chatId: String)
    }

    interface Presenter : TakePhotoContract.Presenter {
        fun onSendTextMessageClick(message: String)
        fun onNewMessage(message: UserChatMessage)
        fun onChatScrollChange(isLastPosition: Boolean)
        fun onChatMessageOnScreen(message: UserChatMessage)
        fun onImageClick(url: String)
    }
}
