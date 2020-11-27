package com.example.ui.userprofile.editfile

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.RecommendationFile
import com.example.ui.base.BaseFragment
import com.example.util.FileUtils
import kotlinx.android.synthetic.main.fragment_edit_file_name.*
import onTextChanged
import java.io.File
import javax.inject.Inject
import javax.inject.Provider


class UserEditFileFragment : BaseFragment(), UserEditFileContract.View {

    var file: RecommendationFile? = null

    override fun layout(): Int = R.layout.fragment_edit_file_name

    @InjectPresenter
    lateinit var presenter: UserEditFilePresenter

    @Inject
    lateinit var presenterProvider: Provider<UserEditFilePresenter>

    @ProvidePresenter
    fun providePresenter(): UserEditFilePresenter = presenterProvider.get().apply {
        file = UserEditFileFragmentArgs.fromBundle(requireArguments()).file
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        etFirstName.setText(file?.name)
        etFirstName.onTextChanged {
            tvFileName.text = it.toString()
        }
        tvFileName.apply {
            text = file?.name
            isClickable = false
            setOnClickListener {
                val uri = Uri.parse(file?.url)
                try {
                    startActivity(Intent(Intent.ACTION_VIEW, uri))
                } catch (e: ActivityNotFoundException) {
                    showRequestErrorMessage()
                }
            }
        }

        ivClose.setOnClickListener {
            super.navigateUp()
        }
        ibSave.setOnClickListener {
            val fileName = etFirstName.text.toString()
            if (fileName.isEmpty()) {
                tilFirstName.error = context?.getString(R.string.profile_edit_empty_field_error)
            } else {
                file?.name = fileName
                parentFragmentManager.setFragmentResult(FILE_EDIT_CODE, bundleOf(FILE_PATH to file))
                super.navigateUp()
            }
        }
    }

    companion object {
        const val FILE_EDIT_CODE = "1488"
        const val FILE_PATH = "file_path"
    }
}