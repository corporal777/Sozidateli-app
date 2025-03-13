package com.example.ui.stories

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import com.example.app.R
import com.example.app.databinding.FragmentStoriesBinding
import com.example.extensions.textColor
import com.example.interfaces.BackgroundImageFragment
import com.example.interfaces.DoNotCheckConnectionFragment
import com.example.ui.base.BaseVBFragment
import com.example.ui.main.MainActivity
import com.example.ui.views.stories.StoriesProgressView
import com.example.util.getColor
import com.example.util.getDrawable
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider
import kotlin.math.roundToInt

class StoriesFragment : BaseVBFragment<FragmentStoriesBinding>(), StoriesContract.View,
    DoNotCheckConnectionFragment, BackgroundImageFragment {

    @InjectPresenter
    lateinit var presenter: StoriesPresenter

    @Inject
    lateinit var presenterProvider: Provider<StoriesPresenter>

    @ProvidePresenter
    fun providePresenter(): StoriesPresenter = presenterProvider.get()

    private var position = 0
    private var pressTime = 0L
    private var limit = 500L

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            mBinding.stories.resume()
            mBinding.stories.reverse()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private val onTouchListener = View.OnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                mBinding.stories.pause()
                return@OnTouchListener false
            }

            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                mBinding.stories.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    private val storiesListener = object : StoriesProgressView.StoriesListener {
        override fun onComplete() {
            backPressedCallback.isEnabled = false
            presenter.onStoriesComplete()
        }

        override fun onPrev() {
            val position = position - 1
            if (position >= 0) setStory(position)
        }

        override fun onNext() {
            val position = position + 1
            if (position < 4) setStory(position)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            backPressedCallback
        )
        mBinding.apply {
            stories.apply {
                setStoriesCount(4)
                setStoryDuration(3000L)
                setStoriesListener(storiesListener)
                startStories()
            }
            reverse.apply {
                setOnClickListener { stories.reverse() }
                setOnTouchListener(onTouchListener)
            }
            skip.apply {
                setOnClickListener { stories.skip() }
                setOnTouchListener(onTouchListener)
            }
            btnContinue.setOnClickListener { stories.skip() }
            btnClose.setOnClickListener { presenter.onStoriesComplete() }
            flingLayout.apply {
                positionChangeListener = { _, _, dragRangeRate ->
                    if (dragRangeRate <= 0f) mBinding.stories.resume()
                    else mBinding.stories.pause()
                    setBackgroundColor(
                        Color.argb(
                            (255 * (1.0F - dragRangeRate)).roundToInt(),
                            0,
                            0,
                            0
                        )
                    )
                }
                dismissListener = { presenter.onStoriesComplete() }
            }
        }
        setStory(0)
    }

    private fun setStory(position: Int) {
        this.position = position
        var imageBackground: Int = R.drawable.st_1_bg
        var imageForeground: Int = R.drawable.st_1_fg
        when (position) {
            0 -> {
                imageBackground = R.drawable.st_1_bg
                imageForeground = R.drawable.st_1_fg
            }

            1 -> {
                imageBackground = R.drawable.st_2_bg
                imageForeground = R.drawable.st_2_fg
            }

            2 -> {
                imageBackground = R.drawable.st_3_bg
                imageForeground = R.drawable.st_3_fg
            }

            3 -> {
                imageBackground = R.drawable.st_4_bg
                imageForeground = R.drawable.st_4_fg
            }
        }
        if (position == 0 || position == 3) {
            mBinding.btnContinue.apply {
                background = getDrawable(R.drawable.btn_background_corners_brown)
                textColor = R.color.black
            }
            mBinding.btnClose.setImageResource(R.drawable.ic_close_camera)
        } else {
            mBinding.btnContinue.apply {
                background = getDrawable(R.drawable.btn_background_corners_white)
                textColor = R.color.white
            }
            mBinding.btnClose.setImageResource(R.drawable.ic_close_light)
        }
        mBinding.ivBackgroundImage.apply { setImageDrawable(getDrawable(imageBackground)) }
        mBinding.ivForegroundImage.apply { setImageDrawable(getDrawable(imageForeground)) }
    }

    override fun hideStories() {
        checkIfFragmentAttached {
            (requireActivity() as MainActivity).presenter.onStoriesComplete()
        }
    }

    override fun binding() = FragmentStoriesBinding::class.java
    override fun layout() = R.layout.fragment_stories
    override fun getFragmentBackgroundDrawable() =
        mBinding.root.getDrawable(R.drawable.background_auth_very_small)

    override val isLightStatus = false
}