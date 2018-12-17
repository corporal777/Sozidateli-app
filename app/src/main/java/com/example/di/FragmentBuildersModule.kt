package com.example.di

import com.example.ui.about.AboutFragment
import com.example.ui.aboutEvent.AboutEventFragment
import com.example.ui.aboutForum.AboutForumFragment
import com.example.ui.auth.login.LoginFragment
import com.example.ui.auth.loginEmail.LoginEmailFragment
import com.example.ui.auth.register.RegisterFragment
import com.example.ui.auth.welcome.WelcomeFragment
import com.example.ui.buildingScheme.BuildingSchemeFragment
import com.example.ui.chat.ChatFragment
import com.example.ui.chatList.ChatListFragment
import com.example.ui.contactsSearch.ContactsSearchFragment
import com.example.ui.documents.DocumentsListFragment
import com.example.ui.eventTabs.EventTabsFragment
import com.example.ui.eventsTabs.EventsTabsFragment
import com.example.ui.image.ImageViewFragment
import com.example.ui.map.MapFragment
import com.example.ui.mapTabs.MapTabsFragment
import com.example.ui.myEvents.MyEventsFragment
import com.example.ui.mySchedule.MyScheduleFragment
import com.example.ui.speaker.SpeakerFragment
import com.example.ui.mySchedule.subevent.SubeventFragment
import com.example.ui.mySchedule.usersList.UserListFragment
import com.example.ui.news.NewsFragment
import com.example.ui.newsList.NewsListFragment
import com.example.ui.profile.ProfileFragment
import com.example.ui.profile.favoritesTab.FavoriteFragment
import com.example.ui.profile.profileEdit.ProfileEditFragment
import com.example.ui.profile.profileFull.ProfileFullFragment
import com.example.ui.profile.settingChat.SettingChatFragment
import com.example.ui.recommendations.RecommendationsFragment
import com.example.ui.request.RequestFragment
import com.example.ui.search.SearchFragment
import com.example.ui.search.searchType.SearchTypeFragment
import com.example.ui.speakers.SpeakersFragment
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

    @ContributesAndroidInjector
    abstract fun contributeEventsTabsFragment(): EventsTabsFragment

    @ContributesAndroidInjector
    abstract fun contributeRecommendationsFragment(): RecommendationsFragment

    @ContributesAndroidInjector
    abstract fun contributeSubscriptionsFragment(): SubscriptionsFragment

    @ContributesAndroidInjector
    abstract fun contributeMyEventsFragment(): MyEventsFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFullFragment(): ProfileFullFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutEventFragment(): AboutEventFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutForumFragment(): AboutForumFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsListFragment(): NewsListFragment

    @ContributesAndroidInjector
    abstract fun contributeNewsFragment(): NewsFragment

    @ContributesAndroidInjector
    abstract fun contributeDocumentsListFragment(): DocumentsListFragment

    @ContributesAndroidInjector
    abstract fun contributeSpeakersFragment(): SpeakersFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteFragment(): FavoriteFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileEditFragment(): ProfileEditFragment

    @ContributesAndroidInjector
    abstract fun contributeRequestFragment(): RequestFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchFragment(): SearchFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchTypeFragment(): SearchTypeFragment

    @ContributesAndroidInjector
    abstract fun contributeEventTabsFragment(): EventTabsFragment

    @ContributesAndroidInjector
    abstract fun contributeMyScheduleFragment(): MyScheduleFragment

    @ContributesAndroidInjector
    abstract fun contributeMapTabsFragment(): MapTabsFragment

    @ContributesAndroidInjector
    abstract fun contributeMapFragment(): MapFragment

    @ContributesAndroidInjector
    abstract fun contributeBuildingSchemeFragment(): BuildingSchemeFragment

    @ContributesAndroidInjector
    abstract fun contributeImageViewFragment(): ImageViewFragment

    @ContributesAndroidInjector
    abstract fun contributeSubeventFragment(): SubeventFragment

    @ContributesAndroidInjector
    abstract fun contributeSpeakerFragment(): SpeakerFragment

    @ContributesAndroidInjector
    abstract fun contributeContactsSearchFragment(): ContactsSearchFragment

    @ContributesAndroidInjector
    abstract fun contributeUserListFragment(): UserListFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingChatFragment(): SettingChatFragment
}
