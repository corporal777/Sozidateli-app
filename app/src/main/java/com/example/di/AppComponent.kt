package com.example.di

import android.app.Application
import com.example.App
import com.example.ui.event.my.schedule.calendar.CalendarBottomSheet
import com.example.ui.support.newQuestion.SupportQuestionBottomSheet
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.example.ui.views.filters.chat.ChatFiltersBottomSheetDialog
import com.example.ui.views.filters.event.EventFiltersBottomSheetDialog
import com.example.ui.views.filters.event.my.MyEventsFiltersBottomSheetDialog
import com.example.ui.views.filters.organization.OrgFiltersBottomSheetDialog
import com.example.ui.views.filters.user.UserFiltersBottomSheetDialog
import com.example.ui.views.notifications.NotificationsView
import com.example.ui.views.suggestFieldView.address.DaDataAutoCompleteTextView
import com.example.ui.views.suggestFieldView.format.EventFormatBottomSheet
import com.example.ui.views.suggestFieldView.organization.EventOrgBottomSheet
import com.example.ui.views.suggestFieldView.region.SearchRegionBottomSheet
import com.example.ui.views.suggestFieldView.settlement.SearchSettlementBottomSheet
import com.example.ui.views.suggestFieldView.town.SearchTownBottomSheet
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidInjectionModule::class,
    AppModule::class,
    ActivityModule::class,
    ServiceBuildersModule::class
])
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun inject(examApp: App)

    fun inject(view: ChatView)

    fun inject(view: AccountView)

    fun inject(view: DaDataAutoCompleteTextView)

    fun inject(view: EventFormatBottomSheet)

    fun inject(view: EventOrgBottomSheet)

    fun inject(view: SearchRegionBottomSheet)

    fun inject(view: SearchSettlementBottomSheet)

    fun inject(view: SearchTownBottomSheet)

    fun inject(view: NotificationsView)

    fun inject(dialog : CalendarBottomSheet)

    fun inject(dialog : SupportQuestionBottomSheet)

    fun inject(dialog : MyEventsFiltersBottomSheetDialog)

    fun inject(dialog : EventFiltersBottomSheetDialog)

    fun inject(dialog : OrgFiltersBottomSheetDialog)

    fun inject(dialog : UserFiltersBottomSheetDialog)

    fun inject(dialog : ChatFiltersBottomSheetDialog)
}
