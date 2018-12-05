package com.example.di

import com.example.ui.auth.login.LoginFragment
import com.example.ui.auth.loginEmail.LoginEmailFragment
import com.example.ui.auth.register.RegisterFragment
import com.example.ui.auth.welcome.WelcomeFragment
import com.example.ui.chat.ChatFragment
import com.example.ui.chatList.ChatListFragment
import com.example.ui.eventsTabs.EventsTabsFragment
import com.example.ui.myEvents.MyEventsFragment
import com.example.ui.recommendations.RecommendationsFragment
import com.example.ui.subscriptions.SubscriptionsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeChatListFragment(): ChatListFragment

    @ContributesAndroidInjector
    abstract fun contributeFirsFragment(): ChatFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginEmailFragment(): LoginEmailFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector
    abstract fun contributeWelcomeFragment(): WelcomeFragment

    abstract fun contributeChatFragment(): ChatFragment

    @ContributesAndroidInjector
    abstract fun contributeEventsTabsFragment(): EventsTabsFragment

    @ContributesAndroidInjector
    abstract fun contributeRecommendationsFragment(): RecommendationsFragment

    @ContributesAndroidInjector
    abstract fun contributeSubscriptionsFragment(): SubscriptionsFragment

    @ContributesAndroidInjector
    abstract fun contributeMyEventsFragment(): MyEventsFragment
}
