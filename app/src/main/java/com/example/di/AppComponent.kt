package com.example.di

import android.app.Application
import com.example.App
import com.example.ui.event.my.schedule.calendar.CalendarBottomSheet
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
import com.example.ui.views.notifications.NotificationsView
import com.example.ui.views.suggestFieldView.DaDataAutoCompleteTextView
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

    fun inject(view: NotificationsView)

    fun inject(dialog : CalendarBottomSheet)
}
