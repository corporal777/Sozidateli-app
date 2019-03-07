package com.example.ui.profile.profileEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.ProfileField
import com.example.data.models.ProfileFieldExpand
import com.example.data.models.Type
import com.example.data.models.user.User
import com.example.holders.BrownButtonItem
import com.example.holders.ProfileHeaderItem
import com.example.holders.profile.*
import com.example.ui.base.takePhoto.TakePhotoFragment
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.dialog_password_recovery.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import javax.inject.Inject
import javax.inject.Provider
import android.content.Intent
import android.app.Activity.RESULT_OK
import android.os.Parcelable
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.util.*
import com.example.util.photohelper.RealPathUtil


class ProfileEditFragment : TakePhotoFragment<ProfileEditContract.View, ProfileEditPresenter>(), ProfileEditContract.View {

    @InjectPresenter
    override lateinit var presenter: ProfileEditPresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfileEditPresenter>

    @ProvidePresenter
    fun providePresenter(): ProfileEditPresenter = presenterProvider.get()


    private var adapter = GroupAdapter<ViewHolder>()

    private var linearLayoutManager = LinearLayoutManager(context)

    private var recyclerState: Parcelable? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fieldRecyclerView.layoutManager = linearLayoutManager
        fieldRecyclerView.adapter = adapter
    }


    override fun setUser(user: User) {

        val listField = mutableListOf<Item>()

        listField.add(ProfileHeaderItem(user.fullName, user.user_avatar, user.user_id, true, View.OnClickListener {
            presenter.onTakePhotoRequest()
        }, user.user_avatar_uri))

        listField.add(ProfileEmailItem(ProfileField("user_email", getString(R.string.email), user.user_email)))
        listField.add(ProfileSwitchItem(ProfileField( "user_email_show", null, user.user_email_show)))
        listField.add(ProfileFieldPasswordChangeItem(presenter))
        listField.add(ProfilePhoneItem(ProfileField( "user_phone", getString(R.string.profile_phone), user.user_phone)))
        listField.add(ProfileSwitchItem(ProfileField("user_phone_show", null, user.user_phone_show)))
        listField.add(ProfileDatetItem(ProfileField( "user_birthday", getString(R.string.profile_birthday), user.user_birthday)))
        listField.add(ProfileSwitchItem(ProfileField( "user_birthday_show", null, user.user_birthday_show)))
        listField.add(ProfileTextItem(ProfileField("user_address_city", getString(R.string.profile_city), user.user_address_city)))

        listField.add(MarginItem(resources.getDimensionPixelSize(R.dimen.profile_margin_between_field)))

        val socialNetworks = createFieldExpand("social_links", mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.TEXT, "value", getString(R.string.profile_sn_label))
        ), user.social_links)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_sn), socialNetworks, false))

        val educationExpand = createFieldExpand("education", mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.DATE, "begin", getString(R.string.profile_date_start_educate)),
                ProfileField(Type.DATE, "end", getString(R.string.profile_date_end_educate)),
                ProfileField(Type.TEXT, "organization", getString(R.string.profile_work_organization)),
                ProfileField(Type.TEXT, "specialty", getString(R.string.profile_educate_speciality))
        ), user.education)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_institution), educationExpand, false))

        val workExpand = createFieldExpand("work", mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.DATE, "begin", getString(R.string.profile_date_start)),
                ProfileField(Type.DATE, "end", getString(R.string.profile_date_end)),
                ProfileField(Type.TEXT, "organization", getString(R.string.profile_work_organization)),
                ProfileField(Type.TEXT, "position", getString(R.string.profile_work_position)),
                ProfileField(Type.TEXT, "description", getString(R.string.profile_work_description))
        ), user.work)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_work_experience), workExpand, false))

        val socialProject = createFieldExpand("social_projects", mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.DATE, "begin", getString(R.string.profile_date_start)),
                ProfileField(Type.DATE, "end", getString(R.string.profile_date_end)),
                ProfileField(Type.TEXT, "name", getString(R.string.profile_social_project_name)),
                ProfileField(Type.TEXT, "role", getString(R.string.profile_social_project_role)),
                ProfileField(Type.TEXT, "description", getString(R.string.profile_social_project_description))
        ), user.social_projects)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_social_project), socialProject, false))

        val attachedFiles = createFieldExpand(FIELD_ATTACH_RECOMMENDATION_FILE, mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.SUPPORT, "type", null),
                ProfileField(Type.TEXT, "name", getString(R.string.profile_attached_file_name)),
                ProfileField(Type.TEXT, "desc", getString(R.string.profile_attached_file_desc)),
                ProfileField(Type.TEXT, "url", getString(R.string.profile_attached_file_url))
        ), user.attached_recomendation_files)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_attached_file), attachedFiles, true, presenter))

        listField.add(BrownButtonItem(getString(R.string.save), View.OnClickListener {
            val fieldList = mutableListOf<ProfileField>()
            val expandFieldList = mutableListOf<ProfileFieldExpand>()

            for (i in 0 until adapter.itemCount) {
                val item = adapter.getItem(i)
                when (item) {
                    is ProfileBaseFieldItem -> {
                        fieldList.add(item.field)
                    }
                    is ProfileExpandFieldItem -> {
                        item.let {
                            expandFieldList.add(item.getFieldExpand())
                        }
                    }
                }
            }

            presenter.onSaveClick(fieldList, expandFieldList)
        }))

        adapter.update(listField)

    }

    override fun updateExpandFieldByName(name: String, array:ArrayList<*>?) {
        for (i in 0 until adapter.itemCount) {
            val item = adapter.getItem(i)
            if(item is ProfileExpandFieldItem){
                val attachedFiles =  createFieldExpand(name,(item as ProfileExpandFieldItem).getFieldExpand().defaultFields,array)
                (item as ProfileExpandFieldItem).updateExpandField(attachedFiles)
            }
        }
    }

    override fun showPdfSelector() {
        val intent = Intent()
        intent.type = "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), REQUEST_CODE_SELECT_PDF)
    }

    override fun showChangePasswordDialog() {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_password_recovery, null, false)
        val alert = AlertDialog.Builder(context!!)
                .setTitle(R.string.recovery_set_password_title)
                .setView(view)
                .create()

        var password = ""
        var passwordConfirm = ""
        var oldPassword = ""

        view.etOldPassword.visibility = View.VISIBLE
        view.etPassword.setHint(R.string.profile_new_password)
        view.etConfirmPassword.setHint(R.string.profile_confirm_new_password)

        validPasswords(password, passwordConfirm, oldPassword, view.tvPasswordStrong1, view.btnSave)

        view.etOldPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            oldPassword = charSequence.toString()
            validPasswords(password, passwordConfirm, oldPassword, view.tvPasswordStrong1, view.btnSave)
        })

        view.etPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            password = charSequence.toString()
            validPasswords(password, passwordConfirm, oldPassword, view.tvPasswordStrong1, view.btnSave)
        })

        view.etConfirmPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            passwordConfirm = charSequence.toString()
            validPasswords(password, passwordConfirm, oldPassword, view.tvPasswordStrong1, view.btnSave)
        })

        view.btnSave.setOnClickListener {
            alert.dismiss()
            presenter.onChangePasswordClick(oldPassword, password)
        }

        alert.show()
    }

    private fun validPasswords(password: String, passwordConfirm: String, oldPassword: String, textView: TextView, button: Button) {
        val validPassword = password == passwordConfirm && AuthUtil.isValidPassword(password) && oldPassword.isNotEmpty()
        AuthUtil.colorTextPasswordChecker(textView, validPassword)
        AuthUtil.enableButton(button, validPassword)
    }

    private fun createFieldExpand(nameField: String, defaultList: MutableList<ProfileField>, array: ArrayList<*>?): ProfileFieldExpand {
        val fieldExpand = ProfileFieldExpand(nameField, mutableListOf(), defaultList)

        array?.let {
            for (item in it) {
                fieldExpand.listOfField.add(Utils.getListFieldValueByMapDefault(item, fieldExpand.defaultFields))
            }
        }
        return fieldExpand
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, result: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_SELECT_PDF) {
                result?.let {
                    it.data?.let { uri ->
                        presenter.onPdfSelected(RealPathUtil.getPath(context, uri))
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
       recyclerState = linearLayoutManager.onSaveInstanceState()
    }

    override fun onResume() {
        super.onResume()
        recyclerState?.let {
            linearLayoutManager.onRestoreInstanceState(it)
        }
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_profile_edit
}
