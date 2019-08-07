package com.example.ui.profile

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.annotation.StyleRes
import androidx.appcompat.view.ContextThemeWrapper
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.doOnNextLayout
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.user.User
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.BadgeDrawable
import com.example.ui.views.addBadge
import kotlinx.android.synthetic.main.fragment_profile.*
import setCircleImage
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment : BaseFragment(), ProfileContract.View, ToolbarFragment {
    override val title: CharSequence
        get() = getString(R.string.profile_title)

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>

    @ProvidePresenter
    fun providePresenter(): ProfilePresenter = presenterProvider.get()

    private lateinit var notificationBadge: BadgeDrawable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        containerUser.setOnClickListener { presenter.onProfileClick() }
        containerNotification.setOnClickListener { presenter.onNotificationClick() }
        tvFavorite.setOnClickListener { presenter.onFavoritesClick() }
        tvEvents.setOnClickListener { presenter.onEventsClick() }
        tvBanned.setOnClickListener { presenter.onBannedClick() }
        tvSupport.setOnClickListener { presenter.onSupportClick() }
        tvRate.setOnClickListener { presenter.onRateClick() }
        tvAboutApplication.setOnClickListener { presenter.onAboutApplicationClick() }
        tvLogout.setOnClickListener { presenter.onLogoutClick() }
    }

    override fun setUser(user: User) {
        ivAvatar.setCircleImage(user.user_avatar, R.drawable.avatar_placeholder)
        tvUserName.text = user.fullName
    }

    override fun highlightNotifications(notificationCount: Int) {
        ivNotificationIcon.apply {
            if (!::notificationBadge.isInitialized) notificationBadge = BadgeDrawable(notificationCount)

            doOnNextLayout {
                addBadge(notificationBadge) { badgeWidth, badgeHeight, anchorRect ->
                    val badgeCenterX = anchorRect.right
                    val badgeCenterY = anchorRect.top + anchorRect.height() / 3

                    anchorRect.set(
                            badgeCenterX - badgeWidth / 2,
                            badgeCenterY - badgeHeight / 2,
                            badgeCenterX + badgeWidth / 2,
                            badgeCenterY + badgeHeight / 2
                    )
                }
            }
        }

        changeNotificationItem(R.string.profile_notifications_has_new, R.color.profile_notification_data_has_new, R.style.ViewBackgroundAccent)
    }

    override fun hideLastNotification() {
        if (::notificationBadge.isInitialized) notificationBadge.apply {
            number = 0
            invalidateSelf()
        }

        changeNotificationItem(R.string.notifications_label, R.color.profile_notification_data_empty, R.style.ViewBackgroundGray)
    }

    private fun changeNotificationItem(@StringRes messageRes: Int, @ColorRes dataColorRes: Int, @StyleRes backgroundStyle: Int) {
        val dataColor = ContextCompat.getColor(requireContext(), dataColorRes)
        tvLastNotificationMessage.apply {
            text = getString(messageRes)
            setTextColor(dataColor)
        }

        ivNotificationIcon.apply {
            imageTintList = ColorStateList.valueOf(dataColor)
        }

        ivNotificationArrow.apply {
            imageTintList = ColorStateList.valueOf(dataColor)
        }

        containerNotification.background = getNotificationButtonBackground(backgroundStyle)
    }

    private fun getNotificationButtonBackground(@StyleRes style: Int): Drawable? {
        return ResourcesCompat.getDrawable(
                resources,
                R.drawable.background_highlight,
                ContextThemeWrapper(requireContext(), style).theme
        )
    }

    override fun showProfile() {
        findNavController().navigate(ProfileFragmentDirections.profileToFullProfile())
    }

    override fun showFavorites() {
        findNavController().navigate(ProfileFragmentDirections.profileToFavorite())
    }

    override fun showEvents() {
        findNavController().navigate(ProfileFragmentDirections.profileToMyEvents())
    }

    override fun showAboutApp() {
        findNavController().navigate(ProfileFragmentDirections.profileToAbout())
    }

    override fun showBanned() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToBannedFragment())
    }

    override fun showNotifications() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToNotificationsFragment())
    }

    override fun layout() = R.layout.fragment_profile
}
