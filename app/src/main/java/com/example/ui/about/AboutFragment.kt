package com.example.ui.about

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.BuildConfig
import com.example.R
import com.example.extensions.dp
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.util.RoundedCornersTransformation
import kotlinx.android.synthetic.main.fragment_about.*
import javax.inject.Inject
import javax.inject.Provider

class AboutFragment : BaseFragment(), AboutContract.View, ToolbarFragment {

    override val title: CharSequence
        get() = getString(R.string.profile_about_app)

    @InjectPresenter
    lateinit var presenter: AboutPresenter

    @Inject
    lateinit var presenterProvider: Provider<AboutPresenter>

    @ProvidePresenter
    fun providePresenter(): AboutPresenter = presenterProvider.get()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvAppVersion.text = getString(R.string.about_version).format(BuildConfig.VERSION_NAME)

        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_about_app)
        val iconBitmap = iconDrawable?.toBitmap()
        ivIcon.setImageBitmap(RoundedCornersTransformation(32.dp, 24.dp).transform(iconBitmap))
    }

    override fun layout() = R.layout.fragment_about
}
