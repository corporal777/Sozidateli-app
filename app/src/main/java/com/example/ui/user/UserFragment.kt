package com.example.ui.user

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_speaker.*
import javax.inject.Inject
import javax.inject.Provider

class UserFragment : BaseFragment(), UserContract.View {

    @InjectPresenter
    lateinit var presenter: UserPresenter

    @Inject
    lateinit var presenterProvider: Provider<UserPresenter>

    @ProvidePresenter
    fun providePresenter(): UserPresenter = presenterProvider.get().apply {
        val args = UserFragmentArgs.fromBundle(arguments!!)
        userId = args.userId.toInt()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnWriteMsg.setOnClickListener { presenter.onWriteMsgClick() }
        btnAddToFavorite.visibility = View.GONE

        btnUnban.setOnClickListener { presenter.onUnbanClick() }
    }

    override fun setUser(user: User) {
        Picasso.get().load(user.user_avatar.let { if (it.isNullOrEmpty()) null else it })
                .placeholder(R.drawable.avatar_placeholder)
                .transform(CropCircleTransformation())
                .into(ivAvatar)
        tvName.text = user.fullName
        //tvInfo.text = user.
        //tvDescription.text = speaker.description
    }

    override fun openChat(userName: String, userAvatar: String?, chatId: String) {
        findNavController().navigate(UserFragmentDirections.userToChat(userName, chatId).apply {
            setUserAvatar(userAvatar)
        })
    }

    override fun layout() = R.layout.fragment_speaker
}
