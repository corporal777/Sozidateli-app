package com.example.di

import android.app.Application
import com.example.App
import com.example.ui.views.accountView.AccountView
import com.example.ui.views.chatView.ChatView
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

}
