package com.example.ui.chat

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.ChatMessage
import com.example.data.models.Message
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter

interface ChatContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun updateMessages(showAnim : Boolean, messages: List<ChatMessage>)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setUserNameAvatar(url: String, name : String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun scrollListToPosition(position: Int, smooth : Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun removeUnreadMessageLabel()

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

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun focusOnInput(showKeyboard: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showChatBlockConfirmation()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input actions")
        fun showSendGroup()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input actions")
        fun showAttachGroup()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun clearMessageInput()

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun cancelNotificationByChatId(chatId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showUser(userId: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEvent(event: String)
    }

    interface Presenter : BaseContract.Presenter, PaginationListGroupAdapter.OnItemTakeCallback {
        fun onChatMessageOnScreen(message: Message)
        fun onAcceptChatClick()
        fun onBlockChatClick()
        fun onBlockChatConfirm()

        fun onMessageInput(message: String)
        fun onSendTextMessageClick(message: String)

        fun onTakePhotoFromCameraRequest()
        fun onTakePhotoFromGalleryRequest()

        fun onUserClick()
    }
}