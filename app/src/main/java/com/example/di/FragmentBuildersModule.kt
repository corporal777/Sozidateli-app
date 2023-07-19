package com.example.di

import com.example.ui.about.AboutFragment
import com.example.ui.accountChange.AccountAuthFragment
import com.example.ui.accountChange.ChangeAccountFragment
import com.example.ui.agreement.UserAgreementFragment
import com.example.ui.auth.authorization.AuthorizationFragment
import com.example.ui.auth.confirm.EmailConfirmFragment
import com.example.ui.auth.login.LoginFragment
import com.example.ui.auth.recoveryPassword.RecoveryPasswordFragment
import com.example.ui.auth.register.email.finish.FinishRegisterFragment
import com.example.ui.auth.register.email.newbuild.RegisterEmailNewFragment
import com.example.ui.auth.register.invite.InviteRegisterFragment
import com.example.ui.auth.register.sn.RegisterSnFragment
import com.example.ui.auth.welcome.WelcomeFragment
import com.example.ui.banned.BannedFragment
import com.example.ui.chat.ChatFragment
import com.example.ui.chatList.ChatListTabsFragment
import com.example.ui.chatList.contacts.ChatListFragment
import com.example.ui.chatList.invites.InviteListFragment
import com.example.ui.editeducation.EditEducationFragment
import com.example.ui.editwork.EditWorksFragment
import com.example.ui.event.about.AboutEventFragmentNew
import com.example.ui.event.activities.ActivitiesFragment
import com.example.ui.event.favorite.subevent.FavoriteSubeventFragment
import com.example.ui.event.list.favorite.FavoriteEventsFragment
import com.example.ui.event.list.recommendations.RecommendationsFragment
import com.example.ui.event.location.buildingScheme.BuildingSchemeFragment
import com.example.ui.event.location.buildingScheme.redesign.DestinationSchemeFragment
import com.example.ui.event.location.map.MapFragment
import com.example.ui.event.location.map.redesign.MapFragmentNew
import com.example.ui.event.my.MyEventsFragment
import com.example.ui.event.my.schedule.MyScheduleEventsFragment
import com.example.ui.event.rating.EventRatingFragment
import com.example.ui.event.registration.EventRegistrationFragment
import com.example.ui.event.speakers.list.EventSpeakersFragment
import com.example.ui.event.speakers.member.UserSpeakerFragment
import com.example.ui.main.inApp.InAppNotificationFragment
import com.example.ui.notification.NotificationFragment
import com.example.ui.notification.center.NotificationsFragment
import com.example.ui.notification.center.redesign.NotificationsListFragment
import com.example.ui.notification.center.redesign.invites.InviteNotificationsBottomSheet
import com.example.ui.notification.center.redesign.types.evaluate.EvaluateNotificationsFragment
import com.example.ui.notification.center.redesign.types.event.EventNotificationsFragment
import com.example.ui.notification.center.redesign.types.organizator.OrganizerNotificationsFragment
import com.example.ui.notification.center.redesign.types.projects.ProjectNotificationsFragment
import com.example.ui.notification.center.redesign.types.projects.active.ActiveInvitesFragment
import com.example.ui.notification.center.redesign.types.projects.active.ActiveInvitesPresenter
import com.example.ui.notification.center.redesign.types.projects.archive.ArchiveInvitesFragment
import com.example.ui.notification.center.redesign.types.system.SystemNotificationsFragment
import com.example.ui.organizations.detail.OrganizationFragment
import com.example.ui.organizations.events.OrganizationEventsFragment
import com.example.ui.organizations.favorite.FavoriteOrganizationsFragment
import com.example.ui.organizations.members.OrganizationMembersFragment
import com.example.ui.page.PageFragment
import com.example.ui.partner.PartnerFragment
import com.example.ui.profile.ProfileFragment
import com.example.ui.profile.data.ProfileDataFragment
import com.example.ui.profile.favoritesTab.FavoriteTabsFragment
import com.example.ui.profile.shortName.ChangeShortNameFragment
import com.example.ui.qrscanner.QrScannerToAuthWebFragment
import com.example.ui.qrscanner.auth.AuthWebsiteFragment
import com.example.ui.search.chat.SearchChatFragment
import com.example.ui.search.code.EnterCodeFragment
import com.example.ui.search.event.SearchEventFragment
import com.example.ui.search.organization.SearchOrganizationFragment
import com.example.ui.search.qr.QrScannerFragment
import com.example.ui.search.tabs.SearchTabsFragment
import com.example.ui.search.user.SearchUserFragment
import com.example.ui.splash.SplashFragment
import com.example.ui.state.UserStateFragment
import com.example.ui.state.base.MainInfoFragment
import com.example.ui.state.maxNew.education.MaxStatusEducationFragment
import com.example.ui.state.maxNew.interests.MaxStatusInterestsFragment
import com.example.ui.state.maxNew.mainInfo.MaxStatusContactsFragment
import com.example.ui.state.maxNew.work.MaxStatusWorkFragment
import com.example.ui.stories.StoriesFragment
import com.example.ui.subevent.SubEventFragment
import com.example.ui.subevent.users.SubeventUserListFragment
import com.example.ui.tags.TagsFragment
import com.example.ui.user.UserFragment
import com.example.ui.userSessions.UserSessionsFragment
import com.example.ui.userprofile.UserProfileFragment
import com.example.ui.userprofile.academicdegree.EditDegreeFragment
import com.example.ui.userprofile.edit.UserEditFragment
import com.example.ui.userprofile.editfile.UserEditFileFragment
import com.example.ui.userprofile.read.contacts.UserProfileContactsFragment
import com.example.ui.userprofile.read.education.UserProfileEducationFragment
import com.example.ui.userprofile.read.experience.UserProfileExperienceFragment
import com.example.ui.userprofile.read.interests.UserProfileInterestsFragment
import com.example.ui.userprofile.read.maindata.UserProfileMainDataFragment
import com.example.ui.userprofile.read.settings.UserProfileSettingsFragment
import com.example.ui.userprofile.read.settings.change_email.ChangeEmailFragment
import com.example.ui.userprofile.read.settings.change_name.ChangeNameFragment
import com.example.ui.userprofile.read.settings.change_password.ChangePasswordFragment
import com.example.ui.userprofile.read.settings.change_phone.ChangePhoneFragment
import com.example.ui.userprofile.read.settings.confirm_phone_email.ConfirmEmailPhoneFragment
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
    abstract fun contributeChatFragment(): ChatFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): AuthorizationFragment

    @ContributesAndroidInjector
    abstract fun contributeAccountAuthFragment(): AccountAuthFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginEmailFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterEmailFragment(): FinishRegisterFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterEmailNewFragment(): RegisterEmailNewFragment

    @ContributesAndroidInjector
    abstract fun contributeRegisterSnFragment(): RegisterSnFragment

    @ContributesAndroidInjector
    abstract fun contributeWelcomeFragment(): WelcomeFragment

    @ContributesAndroidInjector
    abstract fun contributeRecommendationsFragment(): RecommendationsFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    abstract fun contributeAboutEventFragment(): AboutEventFragmentNew

    @ContributesAndroidInjector
    abstract fun contributeFavoriteSpeakersFragment(): FavoriteUsersFragment

    @ContributesAndroidInjector
    abstract fun contributeOrganizationEventsFragment(): OrganizationEventsFragment

    @ContributesAndroidInjector
    abstract fun contributeOrganizationMembersFragment(): OrganizationMembersFragment

    @ContributesAndroidInjector
    abstract fun contributeFavoriteOrganizationsFragment(): FavoriteOrganizationsFragment

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
    abstract fun contributeMapFragment(): MapFragment

    @ContributesAndroidInjector
    abstract fun contributeBuildingSchemeFragment(): BuildingSchemeFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationsFragment(): NotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationsListFragment(): NotificationsListFragment

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
    abstract fun contributeSubeventFragment(): SubEventFragment

    @ContributesAndroidInjector
    abstract fun contributeUserListFragment(): SubeventUserListFragment

    @ContributesAndroidInjector
    abstract fun contributeUserFragment(): UserFragment

    @ContributesAndroidInjector
    abstract fun contributeUserProfileFragment(): UserProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeUserProfileSettingsFragment(): UserProfileSettingsFragment

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
    abstract fun contributePageFragment(): PageFragment

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
    abstract fun contributeInviteRegisterFragment(): InviteRegisterFragment

    @ContributesAndroidInjector
    abstract fun contributeUserEditFileFragment(): UserEditFileFragment

    @ContributesAndroidInjector
    abstract fun contributeEditDegreeFragment(): EditDegreeFragment

    @ContributesAndroidInjector
    abstract fun contributeUserStateFragment(): UserStateFragment

    @ContributesAndroidInjector
    abstract fun contributeMainInfoFragment(): MainInfoFragment

    @ContributesAndroidInjector
    abstract fun contributeActivitiesFragment(): ActivitiesFragment

    @ContributesAndroidInjector
    abstract fun contributeEditWorksFragment(): EditWorksFragment

    @ContributesAndroidInjector
    abstract fun contributeEditEducationFragment(): EditEducationFragment

    @ContributesAndroidInjector
    abstract fun contributeQrScannerAuthWebsiteFragment(): QrScannerToAuthWebFragment

    @ContributesAndroidInjector
    abstract fun contributeAuthWebsiteFragment(): AuthWebsiteFragment

    @ContributesAndroidInjector
    abstract fun contributeUserSpeakerFragment(): UserSpeakerFragment

    @ContributesAndroidInjector
    abstract fun contributeMapFragmentNew(): MapFragmentNew

    @ContributesAndroidInjector
    abstract fun contributeMyEventsFragmentNew(): MyEventsFragment

    @ContributesAndroidInjector
    abstract fun contributeMyScheduleEventsFragmentNew(): MyScheduleEventsFragment

    @ContributesAndroidInjector
    abstract fun contributeUserSessionsFragment(): UserSessionsFragment

    @ContributesAndroidInjector
    abstract fun contributeChangeAccountFragment(): ChangeAccountFragment

    @ContributesAndroidInjector
    abstract fun contributeChangePasswordBottomSheetFragment(): ChangePasswordFragment

    @ContributesAndroidInjector
    abstract fun contributeChangeShortNameFragment(): ChangeShortNameFragment

    @ContributesAndroidInjector
    abstract fun contributeChangeNameFragment(): ChangeNameFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileDataFragment(): ProfileDataFragment

    @ContributesAndroidInjector
    abstract fun contributeChangeEmailBottomSheetFragment(): ChangeEmailFragment

    @ContributesAndroidInjector
    abstract fun contributeChangePhoneBottomSheetFragment(): ChangePhoneFragment

    @ContributesAndroidInjector
    abstract fun contributeConfirmPhoneBottomSheetFragment(): ConfirmEmailPhoneFragment

    @ContributesAndroidInjector
    abstract fun contributeOrganizationFragmentNew(): OrganizationFragment

    @ContributesAndroidInjector
    abstract fun contributeDestinationSchemeFragment(): DestinationSchemeFragment

    @ContributesAndroidInjector
    abstract fun contributeInAppNotificationBottomSheetFragment(): InAppNotificationFragment

    @ContributesAndroidInjector
    abstract fun contributeSystemNotificationsFragment(): SystemNotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeProjectNotificationsFragment(): ProjectNotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeActiveInvitesFragment(): ActiveInvitesFragment

    @ContributesAndroidInjector
    abstract fun contributeArchiveInvitesFragment(): ArchiveInvitesFragment

    @ContributesAndroidInjector
    abstract fun contributeEventNotificationsFragment(): EventNotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeOrganizerNotificationsFragment(): OrganizerNotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeEvaluateNotificationsFragment(): EvaluateNotificationsFragment

    @ContributesAndroidInjector
    abstract fun contributeInvitesNotificationsFragment(): InviteNotificationsBottomSheet

    @ContributesAndroidInjector
    abstract fun contributeMaxStatusMainInfoFragment(): MaxStatusContactsFragment

    @ContributesAndroidInjector
    abstract fun contributeMaxStatusInterestsFragment(): MaxStatusInterestsFragment

    @ContributesAndroidInjector
    abstract fun contributeMaxStatusWorkFragment(): MaxStatusWorkFragment

    @ContributesAndroidInjector
    abstract fun contributeMaxStatusEducationFragment(): MaxStatusEducationFragment
}
