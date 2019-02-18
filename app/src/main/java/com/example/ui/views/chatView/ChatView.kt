package com.example.ui.views.chatView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.App
import com.example.R
import com.example.repository.DummyRepository
import com.example.repository.DummyRepositoryImpl
import kotlinx.android.synthetic.main.image_with_badge.view.*
import javax.inject.Inject
import javax.inject.Provider

class ChatView : FrameLayout, ChatViewContract.View {


    companion object {
        private const val CHAT_VIEW_TAG = "chat_view_tag"
    }

    @InjectPresenter(type = PresenterType.WEAK, tag = CHAT_VIEW_TAG)
    lateinit var presenter: ChatViewPresenter

    /*@Inject
    lateinit var presenterProvider: Provider<ChatViewPresenter>

    @ProvidePresenter(type = PresenterType.WEAK, tag = CHAT_VIEW_TAG)
    fun providePresenter(): ChatViewPresenter = presenterProvider.get()*/

    private lateinit var view: View

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }


    private fun init() {
        view = LayoutInflater.from(context).inflate(R.layout.image_with_badge, this, true)
        presenter = ChatViewPresenter(DummyRepositoryImpl())
        presenter.attachView(this)
    }

    override fun setChatCount(count: Int) {
        var result = count
        val visibility: Int
        if (count > 99) result = 99
        if (count == 0) {
            visibility = View.GONE
        } else {
            visibility = View.VISIBLE
        }
        view.tvBadge.visibility = visibility
        view.tvBadge.text = result.toString()
    }

    override fun showLoadingDialog() {

    }

    override fun hideLoadingDialog() {
    }

    override fun hideKeyboard() {

    }

    override fun hideKeyboard(view: View?) {
    }

    override fun showKeyboard() {
    }

    override fun showToast(message: Int) {}
    override fun showToast(message: String) {}
    override fun navigateUp() {}

}