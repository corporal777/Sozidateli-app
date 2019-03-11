package com.example.ui.speaker

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.Speaker
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
        speaker = args.speaker
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnWriteMsg.setOnClickListener { presenter.onWriteMsgClick() }
        btnAddToFavorite.setOnClickListener { presenter.onAddFavoriteClick() }
    }

    override fun setSpeaker(speaker: Speaker) {
        Picasso.get().load(speaker.photo.let { if (it.isNullOrEmpty()) null else it }).placeholder(R.drawable.avatar_placeholder).transform(CropCircleTransformation()).into(ivAvatar)
        tvName.text = speaker.name
        tvInfo.text = speaker.position
        tvDescription.text = speaker.description

        val btnFavoriteBackground: Int
        val btnFavoriteTextColor: Int
        val btnFavoriteText: String

        if (speaker.isInFavorite) {
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
        }
    }

    override fun openChat(userName: String, userId: String, chatId: String) {
        findNavController().navigate(SpeakerFragmentDirections.speakerFragmentToChat(userName, chatId, userId))
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_speaker
}
