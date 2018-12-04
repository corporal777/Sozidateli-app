package com.example.di

import com.example.ui.auth.login.LoginFragment
import com.example.ui.auth.loginEmail.LoginEmailFragment
import com.example.ui.auth.register.RegisterFragment
import com.example.ui.auth.welcome.WelcomeFragment
import com.example.ui.chatList.ChatListFragment
import com.example.ui.chat.ChatFragment
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
}
