package com.example.ui.gallery.test

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.databinding.FragmentTestBlurBinding
import com.example.ui.base.BaseFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import javax.inject.Inject
import javax.inject.Provider

class TestBlurFragment : BaseFragment<FragmentTestBlurBinding>(), TestBlurContract.View  {

    override fun layout(): Int = R.layout.fragment_test_blur

    @InjectPresenter(type = PresenterType.WEAK)
    lateinit var presenter: TestBlurPresenter

    @Inject
    lateinit var presenterProvider: Provider<TestBlurPresenter>

    @ProvidePresenter(type = PresenterType.WEAK)
    fun providePresenter(): TestBlurPresenter = presenterProvider.get()

    private val groupAdapter by lazy { GroupAdapter<GroupieViewHolder>() }

    private var mBitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            ivContent.setOnClickListener { v ->
                val screenWidth = mainContent.getWidth();
                val screenHeight = mainContent.getHeight();

                val autoTransition: Transition = AutoTransition()
                autoTransition.setDuration(500)

                // With this overload you can control actual transition animation

                // With this overload you can control actual transition animation
                TransitionManager.beginDelayedTransition(mainContent, autoTransition)
                // After `beginDelayedTransition()` function perform changes to the layout
                // Transitions framework will detect those changes and perform appropriate animations
                // After `beginDelayedTransition()` function perform changes to the layout
                // Transitions framework will detect those changes and perform appropriate animations
                ivContent.layoutParams.width = screenWidth
                ivContent.layoutParams.height = screenHeight
                ivContent.requestLayout()
                ivContent.invalidate()
            }
        }
    }

    override fun setImages(images: List<Uri>) {
    }

    private fun setBackgroundOnView(view: View, bitmap: Bitmap?) {
    }


}