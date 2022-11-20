package com.example.ui.organizations

import android.graphics.Bitmap
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType
import com.example.data.models.*
import com.example.data.models.user.User
import com.example.ui.base.BaseContract

interface OrganizationContract {
    interface View : BaseContract.View {
        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setOrganization(logo: Bitmap?, background: Bitmap?, organization: OrganizationNew/*Organization*//*, events: List<Event>, users: List<OrganizationMember>*/)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEvents(organizationId: String)

        @StateStrategyType(SkipStrategy::class)
        fun changeScrollY(scroll: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: Event)

        @StateStrategyType(AddToEndSingleStrategy::class)
        fun setSubscribed(isSubscribed: Boolean)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUsers(organizationId: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showUser(id: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showCurrentUser(id: String)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun updateUser(user: UserDetail?)

        @StateStrategyType(SkipStrategy::class)
        fun showAboutEvent(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showEventRequest(event: String)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganizationEmails(emails: List<EventPhoneModel/*EmailAffiliation*/>)

        @StateStrategyType(SkipStrategy::class)
        fun showWriteToOrganization(email: EventPhoneModel/*EmailAffiliation*/)

        @StateStrategyType(SkipStrategy::class)
        fun selectEvent()

        @StateStrategyType(SkipStrategy::class)
        fun showSearch(format: Int)

        @StateStrategyType(SkipStrategy::class)
        fun showRegistrationFieldsRequest(fields: List<String>)

        @StateStrategyType(OneExecutionStateStrategy::class)
        fun showEditProfile(id: String)
    }

    interface Presenter : BaseContract.Presenter {
        fun onShowMoreEventsClick()
        fun onGoToEventClick(event: Event)

        fun onShowMoreUsersClick()
        fun onUserClick(user: UserDetail?/*User*/)
        fun onUserActionCLick(user: UserDetail?/*User*/)

        fun onSubscribeClick()
        fun onUnsubscribeClick()

        fun onScrollPositionChange(scroll: Int)
        fun onRefreshRequest()

        fun onActionRegister(event: String)
        fun onActionCancel(event: String, registrationId: String?)
        fun onActionWriteToOrganization(emails: List<EventPhoneModel/*EmailAffiliation*/>)
        fun onActionShowEvent(event: String)
        fun onShowEventClick(event: String)
        fun onShowFilterClick(format: Int)
        fun onWriteToOrganizationEmailChosen(email: EventPhoneModel/*EmailAffiliation*/)
        fun onShowEditProfileClick()
    }
}
