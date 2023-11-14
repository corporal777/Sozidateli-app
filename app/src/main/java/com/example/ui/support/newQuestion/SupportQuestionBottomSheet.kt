package com.example.ui.support.newQuestion

import android.content.Context
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.example.App
import com.example.R
import com.example.data.models.SupportFile
import com.example.databinding.BottomSheetSupportQuestionBinding
import com.example.ui.support.sendFile.SupportFilesBottomSheet
import com.example.util.initDropDownAdapter
import com.example.util.initInput
import com.example.util.showCustomTabsBrowser
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import javax.inject.Inject
import javax.inject.Provider

class SupportQuestionBottomSheet(
    context: Context,
    val manager: FragmentManager
) : BottomSheetDialog(context), SupportQuestionContract.View {

    private val mvpDelegate by lazy { MvpDelegate<SupportQuestionBottomSheet>(this) }
    private val mBinding = BottomSheetSupportQuestionBinding.inflate(LayoutInflater.from(context))

    @InjectPresenter(tag = FILES_TAG)
    lateinit var presenter: SupportQuestionPresenter

    @Inject
    lateinit var presenterProvider: Provider<SupportQuestionPresenter>

    @ProvidePresenter(tag = FILES_TAG)
    fun providePresenter(): SupportQuestionPresenter = presenterProvider.get()

    private var onSendClick: (data: String?) -> Unit = {}

    private val groupAdapter = GroupAdapter<GroupieViewHolder>()

    init {
        (context.applicationContext as App).appComponent.inject(this)

        behavior.state = BottomSheetBehavior.STATE_EXPANDED
        behavior.skipCollapsed = true

        setContentView(mBinding.root)
        setCancelable(true)
    }

    override fun setFields(themes: List<String>, isHasConfirmedEmail: Boolean) {
        mBinding.apply {
            filesList.apply {
                isVisible = false
                adapter = groupAdapter
            }
            tvQuestionTheme.apply {
                initDropDownAdapter(themes.toMutableList())
                initInput(presenter.questionTheme) { presenter.onChangeTheme(it.toString()) }
            }
            etEmail.apply {
                tilEmail.isVisible = !isHasConfirmedEmail
                tvEmailTitle.isVisible = !isHasConfirmedEmail
                initInput(presenter.questionEmail) {
                    presenter.onChangeEmail(it.toString())
                }
            }
            etProblem.initInput(presenter.questionProblem) {
                presenter.onChangeProblem(it.toString())
            }
            etDescription.initInput(presenter.questionDescription) {
                presenter.onChangeDescription(it.toString())
            }
            ivFilePrivacy.apply {
                setChecked(presenter.isFileAgree)
                setOnCheckedListener {
                    presenter.onChangePrivacyFileAgree(it)
                }
            }
            ivInfoPrivacy.apply {
                setClickableText(context.getString(R.string.support_info_privacy_text), 52) {
                    val url = "https://файлы.президентскиегранты.рф/42760f96-e759-483f-9611-aa43e70aa343"
                    showCustomTabsBrowser(context, url)
                }
                setChecked(presenter.isInfoAgree)
                setOnCheckedListener {
                    presenter.onChangePrivacyInfoAgree(it)
                }
            }
            tvAddFile.setOnClickListener { showAddFile() }
            btnClose.setOnClickListener { dismiss() }
            btnSend.setOnClickListener { presenter.onSendQuestion() }
        }
    }

    override fun setFiles(files: List<SupportFile>) {
        mBinding.apply {
            filesList.isVisible = !files.isNullOrEmpty()
            clAddFile.isVisible = files.isNullOrEmpty()
        }
        groupAdapter.update(files.map { SupportFileItem(it) { f -> presenter.onRemoveFile(f) } })
        if (files.size in 1..2) groupAdapter.add(SupportAddFileItem { showAddFile() })
    }

    private fun showAddFile() {
        SupportFilesBottomSheet()
            .setFileReadyCallback { presenter.onAddFile(it) }
            .show(manager)
    }


    fun setSendClickCallback(block: (data: String?) -> Unit): SupportQuestionBottomSheet {
        onSendClick = block
        return this
    }

    override fun enableBtnSend(enable: Boolean) = mBinding.btnSend.let { it.isEnabled = enable }
    override fun showCustomLoading() = mBinding.btnSend.showProgressLoading(true)
    override fun hideCustomLoading() = mBinding.btnSend.showProgressLoading(false)
    override fun hideSupportQuestion() = dismiss()


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

    companion object {
        private const val FILES_TAG = "support_question_files_tag"
    }
}