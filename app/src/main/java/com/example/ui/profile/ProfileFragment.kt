package com.example.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.user.User
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile.*
import javax.inject.Inject
import javax.inject.Provider

class ProfileFragment : BaseFragment(), ProfileContract.View {

    @InjectPresenter
    lateinit var presenter: ProfilePresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfilePresenter>

    @ProvidePresenter
    fun providePresenter(): ProfilePresenter = presenterProvider.get()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        flMyEvents.setOnClickListener { presenter.clickMyEvents() }
        flAbout.setOnClickListener { presenter.clickAboutApp() }
        tvAboutStatus.setOnClickListener { presenter.clickAboutStatus() }
        clProfile.setOnClickListener { presenter.clickFullProfile() }
        flFavorite.setOnClickListener { presenter.clickFavorite() }
        flChatSetting.setOnClickListener { presenter.clickChatSetting() }
        notification.setOnClickListener { presenter.onNotificationClick() }
    }

    override fun showAboutStatus() {
        findNavController().navigate(ProfileFragmentDirections.profileToAbout())
    }

    override fun showFullProfile() {
        findNavController().navigate(ProfileFragmentDirections.profileToFullProfile())
    }

    override fun showFavorite() {
        findNavController().navigate(ProfileFragmentDirections.profileToFavorite())
    }

    override fun showMyEvents() {
        findNavController().navigate(ProfileFragmentDirections.profileToMyEvents())
    }

    override fun showTabEvents() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showCurrentEvent(event: Event) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showAboutApp() {
        findNavController().navigate(ProfileFragmentDirections.profileToAbout())
    }

    override fun showChatSetting() {
//        findNavController().navigate(ProfileFragmentDirections.profileToSetting())
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", requireContext().packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun setUser(user: User) {
        tvName.text = user.fullName
        //TODO: need status
        //tvStatus.text = String.format(getString(R.string.profile_status), user.status)
        if (!user.user_avatar.isNullOrEmpty()) Picasso.get().load(user.user_avatar).transform(CropCircleTransformation()).into(ivAvatar)
        val visibleCurrentEvent = if (user.default_event == null) View.GONE else View.VISIBLE
        llCurrentEvent.visibility = visibleCurrentEvent

        user.default_event?.let {
            tvEventName.text = it.name
            tvOrganizationName.text = it.organizationName
            tvEventDate.text = it.start
        }
    }

    override fun showLastNotification(text: String, notificationCount: Int) {
        notification.visibility = View.VISIBLE
        tvLastNotificationText.setHtml(text)
        tvNotificationCount.text = notificationCount.toString()
    }

    override fun hideLastNotification() {
        notification.visibility = View.GONE
    }

    override fun showNotifications() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToNotificationsFragment())
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_profile
}
