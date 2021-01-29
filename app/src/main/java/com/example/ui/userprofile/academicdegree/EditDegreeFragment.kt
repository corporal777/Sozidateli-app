package com.example.ui.userprofile.academicdegree


import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.adapters.NoFilterArrayAdapter
import com.example.data.models.user.User
import com.example.interfaces.ToolbarFragment
import com.example.ui.base.BaseFragment
import com.example.ui.views.toolbar.ToolbarContentActionBar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.fragment_edit_degree.*
import kotlinx.android.synthetic.main.item_profile_data_edit_academic_degree.tilDegreesLevel
import kotlinx.android.synthetic.main.item_profile_data_edit_academic_degree.tilSciencesLevel
import kotlinx.android.synthetic.main.item_profile_data_edit_academic_degree.tvDegreesLevel
import kotlinx.android.synthetic.main.item_profile_data_edit_academic_degree.tvSciencesLevel
import javax.inject.Inject
import javax.inject.Provider

class EditDegreeFragment: BaseFragment(), EditDegreeContract.View, ToolbarFragment {

    override val title: String? = null
    private lateinit var toolbarContentActionBar: ToolbarContentActionBar
    var mDegreesLevel: String? = null
    var mSciencesLevel: String? = null
    var position: Int = 0

    @InjectPresenter
    lateinit var presenter: EditDegreePresenter

    @Inject
    lateinit var presenterProvider: Provider<EditDegreePresenter>

    @ProvidePresenter
    fun providePresenter(): EditDegreePresenter = presenterProvider.get().apply {
        val args = EditDegreeFragmentArgs.fromBundle(requireArguments())
        mDegreesLevel = args.degreesLevel
        mSciencesLevel = args.sciencesLevel
        position = args.position
    }

    override fun layout(): Int = R.layout.fragment_edit_degree

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnSave.setOnClickListener {
            parentFragmentManager.setFragmentResult(DEGREE_EDIT_CODE, bundleOf(DEGREES_LEVEL to mDegreesLevel,
                    SCIENCES_LEVEL to mSciencesLevel, ITEM_POSITION to position))
            super.navigateUp()
        }
    }

    override fun setupToolbarContent(toolbarContentActionBar: ToolbarContentActionBar) {
        super.setupToolbarContent(toolbarContentActionBar)
        this.toolbarContentActionBar = toolbarContentActionBar
    }

    override fun setUserData(user: User) {
        mDegreesLevel = if (mDegreesLevel == null)
            user.available_degrees?.first() else mDegreesLevel
        setupDropDown(tvDegreesLevel, tilDegreesLevel, user.available_degrees?: emptyList(), mDegreesLevel) {
            mDegreesLevel = it
            validateSaveButton()
        }
        setupDropDown(tvSciencesLevel, tilSciencesLevel, user.available_sciences?: emptyList(), mSciencesLevel) {
            mSciencesLevel = it
            validateSaveButton()
        }
        validateSaveButton()
    }

    private fun setupDropDown(textView: AutoCompleteTextView, textInputLayout: TextInputLayout, variants: List<String>, initialVariant: String?, onSelect: (String?) -> Unit) {
        textView.apply {
            setAdapter(NoFilterArrayAdapter(context, android.R.layout.simple_list_item_1, variants.toTypedArray()))
            setText(initialVariant)
            onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
                textInputLayout.error = null
                onSelect(variants.getOrNull(position))
            }
        }
    }

    private fun validateSaveButton() {
        btnSave.isEnabled = mDegreesLevel != null
    }

    override fun setTitle() {
        setTitle(R.string.academic_degree)
    }

    private fun setTitle(@StringRes titleRes: Int) {
        toolbarContentActionBar.setTitle(titleRes)
    }

    companion object {
        const val DEGREE_EDIT_CODE = "228"
        const val DEGREES_LEVEL = "degrees_level"
        const val SCIENCES_LEVEL = "sciences_level"
        const val ITEM_POSITION = "item_position"
    }
}