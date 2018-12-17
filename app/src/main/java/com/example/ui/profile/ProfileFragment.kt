package com.example.ui.profile

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Event
import com.example.data.models.User
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
        findNavController().navigate(ProfileFragmentDirections.profileToSetting())
    }

    override fun setUser(user: User) {
        tvName.text = user.name
        tvStatus.text = String.format(getString(R.string.profile_status), user.status)
        if (!user.image.isNullOrEmpty()) Picasso.get().load(user.image).transform(CropCircleTransformation()).into(ivAvatar)
        val visibleCurrentEvent = if (user.currentEvent == null) View.GONE else View.VISIBLE
        llCurrentEvent.visibility = visibleCurrentEvent

        user.currentEvent?.let {
            tvEventName.text = it.name
            tvOrganizationName.text = it.organizationName
            tvEventDate.text = it.startDate.toString()
        }
    }


    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_profile
}
