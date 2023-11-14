package com.example.ui.chat

import com.example.data.models.ChatMessage
import com.example.data.models.Message
import com.example.ui.base.BaseContract
import com.example.util.AddToEndSingleByTagStateStrategy
import com.example.util.pagination.PaginationListGroupAdapter
import moxy.viewstate.strategy.StateStrategyType
import moxy.viewstate.strategy.alias.AddToEndSingle
import moxy.viewstate.strategy.alias.OneExecution
import moxy.viewstate.strategy.alias.Skip

interface ChatContract {
    interface View : BaseContract.View {
        @OneExecution
        fun showEmptyChatPlaceholder()

        @OneExecution
        fun setChatPlaceholder()

        @AddToEndSingle
        fun updateMessages(showAnim : Boolean, messages: List<ChatMessage>)

        @AddToEndSingle
        fun setUserNameAvatar(url: String, name : String)

        @OneExecution
        fun scrollListToPosition(position: Int, smooth : Boolean)

        @OneExecution
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

        @OneExecution
        fun focusOnInput(showKeyboard: Boolean)

        @OneExecution
        fun showChatBlockConfirmation()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input actions")
        fun showSendGroup()

        @StateStrategyType(AddToEndSingleByTagStateStrategy::class, tag = "input actions")
        fun showAttachGroup()

        @OneExecution
        fun clearMessageInput()

        @OneExecution
        fun cancelNotificationByChatId(chatId: String)

        @Skip
        fun showUser(userId: String)

        @Skip
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