package com.example.ui.speaker

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View
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

class SpeakerFragment : BaseFragment(), SpeakerContract.View {

    @InjectPresenter
    lateinit var presenter: SpeakerPresenter

    @Inject
    lateinit var presenterProvider: Provider<SpeakerPresenter>

    @ProvidePresenter
    fun providePresenter(): SpeakerPresenter = presenterProvider.get().apply {
        val args = SpeakerFragmentArgs.fromBundle(arguments!!)
        user = args.user
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnWriteMsg.setOnClickListener { presenter.onWriteMsgClick() }
        btnAddToFavorite.setOnClickListener { presenter.onAddFavoriteClick() }
    }

    override fun setUser(user: User) {
        if(!user.user_avatar.isNullOrEmpty()) Picasso.get().load(user.user_avatar).transform(CropCircleTransformation()).into(ivAvatar)
        tvName.text = user.user_name
        tvInfo.text = user.user_notes
        tvDescription.text = user.user_description

        val btnFavoriteBackground: Int
        val btnFavoriteTextColor: Int
        val btnFavoriteText: String
        //TODO: need subscribed
        /*if (user.subscribed) {
            btnFavoriteBackground = R.drawable.background_corners_border
            btnFavoriteTextColor = ContextCompat.getColor(context!!, R.color.colorAccent)
            btnFavoriteText = getString(R.string.remove_from_favorites)
        } else {
            btnFavoriteBackground = R.drawable.background_corners
            btnFavoriteTextColor = Color.WHITE
            btnFavoriteText = getString(R.string.add_to_favorites)
        }

        btnAddToFavorite.apply {
            setBackgroundResource(btnFavoriteBackground)
            setTextColor(btnFavoriteTextColor)
            text = btnFavoriteText
        }*/
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_speaker
}
