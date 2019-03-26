package com.example.ui.request

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_OPEN_DOCUMENT
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.FieldType
import com.example.data.models.RegisterEventField
import com.example.data.models.RegisterFieldResponse
import com.example.holders.registerEvent.*
import com.example.ui.base.BaseFragment
import com.example.util.REQUEST_CODE_SELECT_PDF
import com.example.util.photohelper.RealPathUtil
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.PDFFilePickActivity
import com.vincent.filepicker.filter.entity.NormalFile
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Section
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.fragment_request.*
import java.io.File
import javax.inject.Inject
import javax.inject.Provider
import android.provider.DocumentsContract
import com.example.holders.ActionButtonItem


class RequestFragment : BaseFragment(), RequestContract.View {

    @InjectPresenter
    lateinit var presenter: RequestPresenter

    @Inject
    lateinit var presenterProvider: Provider<RequestPresenter>

    @ProvidePresenter
    fun providePresenter(): RequestPresenter = presenterProvider.get().apply {
        arguments?.let {
            val arg = RequestFragmentArgs.fromBundle(it)
            event = arg.event
        }
    }

    private val adapter by lazy { GroupAdapter<ViewHolder>().apply { add(section) } }

    private val section = Section()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView.apply { adapter = this@RequestFragment.adapter }
        section.setHeader(RegisterEventHeaderItem(presenter))
        section.setFooter(ActionButtonItem(getString(R.string.go_to_event), View.OnClickListener { presenter.onRegisterClick() }))
    }

    override fun setFields(fieldResponse: RegisterFieldResponse) {
        val listFields = arrayListOf<Item>()
        fieldResponse.fields?.let {

            it.forEach {
                val baseItem: BaseRegisterItem
                when (it.type) {
                    FieldType.STRING.code -> baseItem = RegisterEventStringItem(it, presenter)
                    FieldType.NUMBER.code -> baseItem = RegisterEventNumberItem(it, presenter)
                    FieldType.DATE.code -> baseItem = RegisterEventDateItem(it, false, presenter, fragmentManager!!)
                    FieldType.DATETIME.code -> baseItem = RegisterEventDateItem(it, true, presenter, fragmentManager!!)
                    FieldType.SELECTBOX.code -> baseItem = RegisterEventSelectBoxItem(it, presenter)
                    FieldType.RADIOBOX.code -> baseItem = RegisterEventRadioBoxItem(it, presenter)
                    FieldType.FILE.code -> baseItem = RegisterEventFileItem(it, presenter)
                    else -> baseItem = RegisterEventStringItem(it, presenter)
                }
                listFields.add(baseItem)
            }
        }

        listFields.add(RegisterEventDropDownCategoryItem(fieldResponse.categories, presenter,fieldResponse.selectedCategory))

        section.update(listFields)
    }

    override fun openFileSelector() {
        val intent = Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_OPEN_DOCUMENT)
                .addCategory(Intent.CATEGORY_OPENABLE)

        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_CODE_SELECT_PDF)
    }

    override fun updateFileField(position: Int, path: String) {
        val item = section.getItem(position)
        if (item is RegisterEventFileItem) {
            item.updateFile(File(path))
            item.notifyChanged()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        super.onActivityResult(requestCode, resultCode, result)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_PDF) {
                val uri = result?.data
                try {
                    uri?.let {
                        presenter.onFileSelected(RealPathUtil.getPath(context, uri))
                    }
                } catch (e:Exception){
                    e.printStackTrace()
                    showDialog(getString(R.string.select_file_error))
                }
            }
        }
    }

    override fun enableActionButton(enable: Boolean) {
        /* btnSendRequest.apply {
             isEnabled = enable

             val background: Int
             val textColor: Int
             if (enable) {
                 background = R.drawable.background_corners
                 textColor = Color.WHITE
             } else {
                 background = R.drawable.background_edittext_login
                 textColor = Color.DKGRAY
             }

             setBackgroundResource(background)
             setTextColor(textColor)
         }*/
    }

    override fun layout() = R.layout.fragment_request
}
