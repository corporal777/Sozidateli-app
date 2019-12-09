package com.example.ui.stories

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import androidx.navigation.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.interfaces.BackgroundImageFragment
import com.example.interfaces.DoNotCheckConnectionFragment
import com.example.ui.base.BaseFragment
import com.example.util.AuthBackground
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
        stories.apply {
            setStoriesCount(3)
            setStoryDuration(3000L)
            setStoriesListener(object : StoriesProgressView.StoriesListener {
                override fun onComplete() {
                    findNavController().navigateUp()
                }

                override fun onPrev() {
                    val position = position - 1
                    if (position >= 0) setStory(position)
                }

                override fun onNext() {
                    val position = position + 1
                    if (position < 3) setStory(position)
                }
            })
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

        setStory(0)
    }

    private fun setStory(position: Int) {
        this.position = position
        val message = "Story $position"
        tvMessage.text = message
        ivImage.setImageDrawable(AuthBackground.get(resources))
    }

    override fun layout() = R.layout.fragment_stories
}