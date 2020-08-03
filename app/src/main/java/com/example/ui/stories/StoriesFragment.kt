package com.example.ui.stories

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.DoNotCheckConnectionFragment
import com.example.ui.base.BaseFragment
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.android.synthetic.main.fragment_stories.*
import javax.inject.Inject
import javax.inject.Provider

class StoriesFragment : BaseFragment(), StoriesContract.View, DoNotCheckConnectionFragment {

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
            stories.resume()
            stories.reverse()
        }
    }

    private val onTouchListener = View.OnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                stories.pause()
                return@OnTouchListener false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                stories.resume()
                return@OnTouchListener limit < now - pressTime
            }
        }
        false
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
        stories.apply {
            setStoriesCount(4)
            setStoryDuration(3000L)
            setStoriesListener(object : StoriesProgressView.StoriesListener {
                override fun onComplete() {
                    backPressedCallback.isEnabled = false
                    findNavController().navigateUp()
                }

                override fun onPrev() {
                    val position = position - 1
                    if (position >= 0) setStory(position)
                }

                override fun onNext() {
                    val position = position + 1
                    if (position < 4) setStory(position)
                }
            })
            startStories()
        }

        reverse.apply {
            setOnClickListener {
                stories.reverse()
            }
            setOnTouchListener(onTouchListener)
        }

        skip.apply {
            setOnClickListener {
                stories.skip()
            }
            setOnTouchListener(onTouchListener)
        }

        setStory(0)
    }

    private fun setStory(position: Int) {
        this.position = position
        ivBackgroundImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), when (position) {
            0 -> R.drawable.st_1_bg
            1 -> R.drawable.st_2_bg
            2 -> R.drawable.st_3_bg
            3 -> R.drawable.st_4_bg
            else -> throw IllegalStateException("No file for position: $position")
        }))

        ivForegroundImage.setImageDrawable(ContextCompat.getDrawable(requireContext(), when (position) {
            0 -> R.drawable.st_1_fg
            1 -> R.drawable.st_2_fg
            2 -> R.drawable.st_3_fg
            3 -> R.drawable.st_4_fg
            else -> throw IllegalStateException("No file for position: $position")
        }))
    }

    override fun layout() = R.layout.fragment_stories
}