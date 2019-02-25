package com.example.ui.views.accountView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.PresenterType
import com.example.R
import com.example.repository.DummyRepositoryImpl
import kotlinx.android.synthetic.main.image_with_badge.view.*

class AccountView : FrameLayout, AccountViewContract.View {


    companion object {
        private const val CHAT_VIEW_TAG = "account_view_tag"
    }

    @InjectPresenter(type = PresenterType.WEAK, tag = CHAT_VIEW_TAG)
    lateinit var presenter: AccountViewPresenter

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
        view.ivImage.setImageResource(R.drawable.ic_account)
        presenter = AccountViewPresenter(DummyRepositoryImpl())
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

    override fun hideAllLoadingDialogs() {

    }

    override fun showToast(message: Int) {}
    override fun showToast(message: String) {}
    override fun navigateUp() {}

}