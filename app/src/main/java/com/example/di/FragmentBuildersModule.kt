package com.example.di

import com.example.ui.about.AboutFragment
import com.example.ui.aboutEvent.AboutEventFragment
import com.example.ui.aboutForum.AboutForumFragment
import com.example.ui.auth.login.LoginFragment
import com.example.ui.auth.loginEmail.LoginEmailFragment
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragment
import com.example.ui.auth.register.RegisterFragment
import com.example.ui.auth.welcome.WelcomeFragment
import com.example.ui.chat.ChatFragment
import com.example.ui.chatList.ChatListFragment
import com.example.ui.contactsSearch.ContactsSearchFragment
import com.example.ui.documents.DocumentsListFragment
import com.example.ui.event.schedule.complete.EventCompleteScheduleFragment
import com.example.ui.event.schedule.my.EventMyScheduleFragment
import com.example.ui.eventTabs.EventTabsFragment
import com.example.ui.eventsTabs.EventListFragment
import com.example.ui.image.ImageViewFragment
import com.example.ui.mapTabs.MapTabsFragment
import com.example.ui.mapTabs.buildingScheme.BuildingSchemeFragment
import com.example.ui.mapTabs.map.MapFragment
import com.example.ui.event.list.my.MyEventsFragment
import com.example.ui.news.NewsFragment
import com.example.ui.newsList.NewsListFragment
import com.example.ui.notifications.NotificationsFragment
import com.example.ui.organizations.favorites.FavoriteOrganizationsFragment
import com.example.ui.organizations.subscribe.SubscribeOrganizationsFragment
import com.example.ui.partner.PartnerFragment
import com.example.ui.profile.ProfileFragment
import com.example.ui.profile.favoritesTab.FavoriteFragment
import com.example.ui.profile.profileEdit.ProfileEditFragment
import com.example.ui.profile.profileFull.ProfileFullFragment
import com.example.ui.profile.settingChat.SettingChatFragment
import com.example.ui.search.qr.QrScannerFragment
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.ui.request.RequestFragment
import com.example.ui.search.SearchFragment
import com.example.ui.search.enterCode.EnterCodeFragment
import com.example.ui.search.searchType.SearchTypeFragment
import com.example.ui.speaker.SpeakerFragment
import com.example.ui.speakers.event.EventSpeakersFragment
import com.example.ui.speakers.favorite.FavoriteSpeakersFragment
import com.example.ui.splash.SplashFragment
import com.example.ui.subevent.SubeventFragment
import com.example.ui.subevent.users.SubeventUserListFragment
import com.example.ui.user.UserFragment
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
    abstract fun contributeEventsTabsFragment(): EventListFragment

    @ContributesAndroidInjector
    abstract fun contributeRecommendationsFragment(): RecommendationsFragment

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
    abstract fun contributeFavoriteSpeakersFragment(): FavoriteSpeakersFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteOrganizationsFragment(): FavoriteOrganizationsFragment

    @ContributesAndroidInjector
    abstract fun contributeSubscribeOrganizationsFragment(): SubscribeOrganizationsFragment

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
    abstract fun contributeEventMyScheduleFragment(): EventMyScheduleFragment

    @ContributesAndroidInjector
    abstract fun contributeEventCompleteScheduleFragment(): EventCompleteScheduleFragment

    @ContributesAndroidInjector
    abstract fun contributeMapTabsFragment(): MapTabsFragment

    @ContributesAndroidInjector
    abstract fun contributeMapFragment(): MapFragment

    @ContributesAndroidInjector
    abstract fun contributeBuildingSchemeFragment(): BuildingSchemeFragment

    @ContributesAndroidInjector
    abstract fun contributeImageViewFragment(): ImageViewFragment

    @ContributesAndroidInjector
    abstract fun contributeSpeakerFragment(): SpeakerFragment

    @ContributesAndroidInjector
    abstract fun contributeContactsSearchFragment(): ContactsSearchFragment

    @ContributesAndroidInjector
    abstract fun contributeSettingChatFragment(): SettingChatFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationsFragment(): NotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeQrScannerFragment(): QrScannerFragment

    @ContributesAndroidInjector
    abstract fun contributeEnterCodeFragmentFragment(): EnterCodeFragment

    @ContributesAndroidInjector
    abstract fun contributeSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributeRecoveryFragment(): RecoveryPasswordFragment

    @ContributesAndroidInjector
    abstract fun contributePartnerFragment(): PartnerFragment

    @ContributesAndroidInjector
    abstract fun contributeEventSpeakersFragment(): EventSpeakersFragment

    @ContributesAndroidInjector
    abstract fun contributeSubeventFragment(): SubeventFragment

    @ContributesAndroidInjector
    abstract fun contributeUserListFragment(): SubeventUserListFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): UserFragment
}
