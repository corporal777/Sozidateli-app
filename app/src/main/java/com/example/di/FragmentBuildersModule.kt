package com.example.di

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
}
