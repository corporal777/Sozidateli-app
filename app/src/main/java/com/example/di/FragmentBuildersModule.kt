package com.example.di

import com.example.ui.about.AboutFragment
import com.example.ui.agreement.UserAgreementFragment
import com.example.ui.auth.authorization.AuthorizationFragment
import com.example.ui.auth.confirm.EmailConfirmFragment
import com.example.ui.auth.login.LoginFragment
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragment
import com.example.ui.auth.register.email.RegisterEmailFragment
import com.example.ui.auth.register.sn.RegisterSnFragment
import com.example.ui.auth.welcome.WelcomeFragment
import com.example.ui.banned.BannedFragment
import com.example.ui.chat.ChatFragment
import com.example.ui.chatList.ChatListTabsFragment
import com.example.ui.chatList.contacts.ChatListFragment
import com.example.ui.chatList.invites.InviteListFragment
import com.example.ui.event.about.AboutEventFragment
import com.example.ui.event.contacts.EventContactsFragment
import com.example.ui.event.favorite.subevent.FavoriteSubeventFragment
import com.example.ui.event.list.favorite.FavoriteEventsFragment
import com.example.ui.event.list.my.MyEventsFragment
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.ui.event.location.EventLocationFragment
import com.example.ui.event.location.buildingScheme.BuildingSchemeFragment
import com.example.ui.event.location.map.MapFragment
import com.example.ui.event.rating.EventRatingFragment
import com.example.ui.event.registration.EventRegistrationFragment
import com.example.ui.event.schedule.complete.EventCompleteScheduleFragment
import com.example.ui.event.schedule.my.EventMyScheduleFragment
import com.example.ui.event.speakers.EventSpeakersFragment
import com.example.ui.eventTabs.EventTabsFragment
import com.example.ui.notification.NotificationFragment
import com.example.ui.notification.center.NotificationsFragment
import com.example.ui.organizations.OrganizationFragment
import com.example.ui.organizations.events.OrganizationEventsFragment
import com.example.ui.organizations.list.OrganizationsFragment
import com.example.ui.organizations.members.OrganizationMembersFragment
import com.example.ui.page.PageFragment
import com.example.ui.partner.PartnerFragment
import com.example.ui.profile.ProfileFragment
import com.example.ui.profile.favoritesTab.FavoriteTabsFragment
import com.example.ui.search.chat.SearchChatFragment
import com.example.ui.search.code.EnterCodeFragment
import com.example.ui.search.event.SearchEventFragment
import com.example.ui.search.organization.SearchOrganizationFragment
import com.example.ui.search.qr.QrScannerFragment
import com.example.ui.search.tabs.SearchTabsFragment
import com.example.ui.search.user.SearchUserFragment
import com.example.ui.splash.SplashFragment
import com.example.ui.status.StatusFragment
import com.example.ui.status.tabs.StatusPagesFragment
import com.example.ui.stories.StoriesFragment
import com.example.ui.subevent.SubeventFragment
import com.example.ui.subevent.users.SubeventUserListFragment
import com.example.ui.tags.TagsFragment
import com.example.ui.user.UserFragment
import com.example.ui.userprofile.edit.UserEditFragment
import com.example.ui.userprofile.passwordconfirm.PasswordConfirmFragment
import com.example.ui.userprofile.phoneconfirm.PhoneConfirmFragment
import com.example.ui.userprofile.UserProfileFragment
import com.example.ui.userprofile.read.contacts.UserProfileContactsFragment
import com.example.ui.userprofile.read.education.UserProfileEducationFragment
import com.example.ui.userprofile.read.experience.UserProfileExperienceFragment
import com.example.ui.userprofile.read.interests.UserProfileInterestsFragment
import com.example.ui.userprofile.read.maindata.UserProfileMainDataFragment
import com.example.ui.users.favorite.FavoriteUsersFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Suppress("unused")
@Module
abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract fun contributeChatListTabsFragment(): ChatListTabsFragment

    @ContributesAndroidInjector
    abstract fun contributeChatListFragment(): ChatListFragment

    @ContributesAndroidInjector
    abstract fun contributeInvitesListFragment(): InviteListFragment

    @ContributesAndroidInjector
    abstract fun contributeFirsFragment(): ChatFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): AuthorizationFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginEmailFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterEmailFragment(): RegisterEmailFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterSnFragment(): RegisterSnFragment

    @ContributesAndroidInjector
    abstract fun contributeWelcomeFragment(): WelcomeFragment

    @ContributesAndroidInjector
    abstract fun contributeRecommendationsFragment(): RecommendationsFragment

    @ContributesAndroidInjector
    abstract fun contributeMyEventsFragment(): MyEventsFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutEventFragment(): AboutEventFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteSpeakersFragment(): FavoriteUsersFragment

    @ContributesAndroidInjector
    abstract fun contributeOrganizationFragment(): OrganizationFragment

    @ContributesAndroidInjector
    abstract fun contributeOrganizationEventsFragment(): OrganizationEventsFragment

    @ContributesAndroidInjector
    abstract fun contributeOrganizationMembersFragment(): OrganizationMembersFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteOrganizationsFragment(): OrganizationsFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteFragment(): FavoriteTabsFragment

    @ContributesAndroidInjector
    abstract fun contributeRequestFragment(): EventRegistrationFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchTabsFragment(): SearchTabsFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchOrganizationsFragment(): SearchOrganizationFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchEventsFragment(): SearchEventFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchUsersFragment(): SearchUserFragment

    @ContributesAndroidInjector
    abstract fun contributeSearchChatFragment(): SearchChatFragment

    @ContributesAndroidInjector
    abstract fun contributeEventTabsFragment(): EventTabsFragment

    @ContributesAndroidInjector
    abstract fun contributeEventMyScheduleFragment(): EventMyScheduleFragment

    @ContributesAndroidInjector
    abstract fun contributeEventCompleteScheduleFragment(): EventCompleteScheduleFragment

    @ContributesAndroidInjector
    abstract fun contributeMapTabsFragment(): EventLocationFragment

    @ContributesAndroidInjector
    abstract fun contributeMapFragment(): MapFragment

    @ContributesAndroidInjector
    abstract fun contributeBuildingSchemeFragment(): BuildingSchemeFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationsFragment(): NotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationFragment(): NotificationFragment

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

    @ContributesAndroidInjector
    abstract fun contributeUserProfileFragment(): UserProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeUserProfileMainDataFragment(): UserProfileMainDataFragment

    @ContributesAndroidInjector
    abstract fun contributeUserProfileContactsFragment(): UserProfileContactsFragment

    @ContributesAndroidInjector
    abstract fun contributeUserProfileInterestsFragment(): UserProfileInterestsFragment

    @ContributesAndroidInjector
    abstract fun contributeUserProfileEducationFragment(): UserProfileEducationFragment

    @ContributesAndroidInjector
    abstract fun contributeUserProfileExperienceFragment(): UserProfileExperienceFragment

    @ContributesAndroidInjector
    abstract fun contributeUserEditFragment(): UserEditFragment

    @ContributesAndroidInjector
    abstract fun contributeBannedFragment(): BannedFragment

    @ContributesAndroidInjector
    abstract fun contributeEmailConfirmFragment(): EmailConfirmFragment

    @ContributesAndroidInjector
    abstract fun contributeStatusPagesFragment(): StatusPagesFragment

    @ContributesAndroidInjector
    abstract fun contributeStatusFragment(): StatusFragment

    @ContributesAndroidInjector
    abstract fun contributePageFragment(): PageFragment

    @ContributesAndroidInjector
    abstract fun contributeEventContactsFragment(): EventContactsFragment

    @ContributesAndroidInjector
    abstract fun contributeStoriesFragment(): StoriesFragment

    @ContributesAndroidInjector
    abstract fun contributeTagsFragment(): TagsFragment

    @ContributesAndroidInjector
    abstract fun contributeUserAgreementFragment(): UserAgreementFragment

    @ContributesAndroidInjector
    abstract fun contributeEventRatingFragment(): EventRatingFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteEventsFragment(): FavoriteEventsFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteSubeventFragment(): FavoriteSubeventFragment

    @ContributesAndroidInjector
    abstract fun contributePasswordConfirmFragment(): PasswordConfirmFragment

    @ContributesAndroidInjector
    abstract fun contributePhoneConfirmFragment(): PhoneConfirmFragment
}
