package com.example.di

import com.example.App
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.example.ui.views.notifications.NotificationsView
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@EntryPoint
interface AppComponent {

    fun inject(examApp: App)

    fun inject(view: ChatView)

    fun inject(view: AccountView)

    //fun inject(view: DaDataAutoCompleteTextView)

//    fun inject(view: EventFormatBottomSheet)
//
//    fun inject(view: EventOrgBottomSheet)
//
//    fun inject(view: SearchRegionBottomSheet)
//
//    fun inject(view: SearchSettlementBottomSheet)
//
//    fun inject(view: SearchTownBottomSheet)

    fun inject(view: NotificationsView)
    
//    fun inject(dialog : MyEventsFiltersBottomSheetDialog)
//
//    fun inject(dialog : EventFiltersBottomSheetDialog)
//
//    fun inject(dialog : OrgFiltersBottomSheetDialog)
//
//    fun inject(dialog : UserFiltersBottomSheetDialog)
//
//    fun inject(dialog : ChatFiltersBottomSheetDialog)
}
