package com.example.ui.views.chatView

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.App
import com.example.R
import kotlinx.android.synthetic.main.image_with_badge.view.*
import javax.inject.Inject
import javax.inject.Provider

class ChatView : FrameLayout, ChatViewContract.View {

    companion object {
        private const val CHAT_VIEW_TAG = "chat_view_tag"
    }

    private val mvpDelegate by lazy { MvpDelegate(this) }

    @InjectPresenter(type = PresenterType.WEAK, tag = CHAT_VIEW_TAG)
    lateinit var presenter: ChatViewPresenter

    @Inject
    lateinit var presenterProvider: Provider<ChatViewPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = CHAT_VIEW_TAG)
    fun providePresenter(): ChatViewPresenter = presenterProvider.get()

    private var view: View = LayoutInflater.from(context).inflate(R.layout.image_with_badge, this, true).apply {
        ivImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_chat))
    }

    private val badgeBackgroundMessages by lazy {
        ContextCompat.getDrawable(context, R.drawable.background_badge_attention_high)!!
    }

    private val badgeBackgroundRequests by lazy {
        ContextCompat.getDrawable(context, R.drawable.background_badge_attention_low)!!
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        (context.applicationContext as App).appComponent.inject(this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        mvpDelegate.onCreate()
        mvpDelegate.onAttach()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mvpDelegate.onSaveInstanceState()
        mvpDelegate.onDetach()
        mvpDelegate.onDestroyView()
        mvpDelegate.onDestroy()
    }

    override fun setMessagesCount(count: String) {
        showBadge(count, badgeBackgroundMessages)
    }

    override fun setRequestsCount(count: String) {
        showBadge(count, badgeBackgroundRequests)
    }

    private fun showBadge(count: String, backgroundDrawable: Drawable) {
        view.tvBadge.apply {
            text = count
            background = backgroundDrawable
            visibility = View.VISIBLE
        }
    }

    override fun hideCounter() {
        view.tvBadge.visibility = View.GONE
    }
}