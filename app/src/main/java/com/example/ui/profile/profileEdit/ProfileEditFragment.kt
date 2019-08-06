package com.example.ui.profile.profileEdit

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.ProfileField
import com.example.data.models.ProfileFieldExpand
import com.example.data.models.Type
import com.example.data.models.user.User
import com.example.holders.ActionButtonItem
import com.example.holders.ProfileHeaderItem
import com.example.holders.profile.*
import com.example.ui.base.takePhoto.TakePhotoFragment
import com.example.ui.views.PasswordHintTextView
import com.example.util.*
import com.vincent.filepicker.Constant
import com.vincent.filepicker.activity.PDFFilePickActivity
import com.vincent.filepicker.filter.entity.NormalFile
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.dialog_password_recovery.view.*
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import javax.inject.Inject
import javax.inject.Provider

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

        val genderData = LinkedHashMap<String, String?>()
        genderData.put(getString(R.string.profile_gender_not_selected), "")
        genderData.put(getString(R.string.profile_gender_male), GENDER_MALE)
        genderData.put(getString(R.string.profile_gender_female), GENDER_FEMALE)

        val listField = mutableListOf<Item>()

        listField.add(ProfileHeaderItem(user.fullName, user.user_avatar, user.user_id, true, View.OnClickListener {
        }, user.user_avatar_uri))

        listField.add(ProfileEmailItem(ProfileField("user_email", getString(R.string.email), user.user_email, true)))
        listField.add(ProfileSwitchItem(ProfileField("user_email_show", null, user.user_email_show)))
        listField.add(ProfileFieldPasswordChangeItem(presenter))
        listField.add(ProfilePhoneItem(ProfileField("user_phone", getString(R.string.profile_phone), user.user_phone)))
        listField.add(ProfileSwitchItem(ProfileField("user_phone_show", null, user.user_phone_show)))
        listField.add(ProfilePhoneItem(ProfileField("user_phone_work", getString(R.string.profile_phone_work), user.user_phone_work)))
        listField.add(ProfileSwitchItem(ProfileField("user_phone_work_show", null, user.user_phone_work_show)))
        listField.add(ProfileDatetItem(ProfileField("user_birthday", getString(R.string.profile_birthday), user.user_birthday), childFragmentManager))
        listField.add(ProfileSwitchItem(ProfileField("user_birthday_show", null, user.user_birthday_show)))
        listField.add(ProfileSelectItem(ProfileField("user_gender", getString(R.string.profile_gender), user.user_gender), genderData))
        listField.add(ProfileCityItem(ProfileField("user_address", getString(R.string.profile_city), user.user_address)))

        listField.add(MarginItem(resources.getDimensionPixelSize(R.dimen.profile_margin_between_field)))

        val socialNetworks = createFieldExpand("social_links", mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.TEXT, "value", getString(R.string.profile_sn_label), true)
        ), user.social_links)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_sn), socialNetworks, false, fragmentManager = childFragmentManager))

        val educationExpand = createFieldExpand("education", mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.DATE, "begin", getString(R.string.profile_date_start_educate), true),
                ProfileField(Type.DATE, "end", getString(R.string.profile_date_end_educate)),
                ProfileField(Type.TEXT, "organization", getString(R.string.profile_work_organization), true),
                ProfileField(Type.TEXT, "specialty", getString(R.string.profile_educate_speciality), true)
        ), user.education)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_institution), educationExpand, false, fragmentManager = childFragmentManager))

        val workExpand = createFieldExpand("work", mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.DATE, "begin", getString(R.string.profile_date_start), true),
                ProfileField(Type.DATE, "end", getString(R.string.profile_date_end)),
                ProfileField(Type.TEXT, "organization", getString(R.string.profile_work_organization), true),
                ProfileField(Type.TEXT, "position", getString(R.string.profile_work_position), true),
                ProfileField(Type.TEXT, "description", getString(R.string.profile_work_description))
        ), user.work)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_work_experience), workExpand, false, fragmentManager = childFragmentManager))

        val socialProject = createFieldExpand("social_projects", mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.DATE, "begin", getString(R.string.profile_date_start), true),
                ProfileField(Type.DATE, "end", getString(R.string.profile_date_end)),
                ProfileField(Type.TEXT, "name", getString(R.string.profile_social_project_name), true),
                ProfileField(Type.TEXT, "role", getString(R.string.profile_social_project_role), true),
                ProfileField(Type.TEXT, "description", getString(R.string.profile_social_project_description))
        ), user.social_projects)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_social_project), socialProject, false, fragmentManager = childFragmentManager))

        val attachedFiles = createFieldExpand(FIELD_ATTACH_RECOMMENDATION_FILE, mutableListOf(
                ProfileField(Type.SUPPORT, "id", null),
                ProfileField(Type.SUPPORT, "type", null),
                ProfileField(Type.TEXT, "name", getString(R.string.profile_attached_file_name), true),
                ProfileField(Type.TEXT, "desc", getString(R.string.profile_attached_file_desc)),
                ProfileField(Type.TEXT, "url", getString(R.string.profile_attached_file_url), true)
        ), user.attached_recomendation_files)

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_attached_file), attachedFiles, true, presenter, fragmentManager = childFragmentManager))

        listField.add(ActionButtonItem(getString(R.string.save), View.OnClickListener {
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

        adapter.clear()

        adapter.update(listField)
    }

    override fun showRequiredError(fieldName: String) {
        showToast(getString(R.string.profile_save_required_error).format(fieldName))
    }

    override fun updateExpandFieldByName(name: String, array: ArrayList<*>?) {
        for (i in 0 until adapter.itemCount) {
            val item = adapter.getItem(i)
            if (item is ProfileExpandFieldItem) {
                if (item.getFieldExpand().nameField == FIELD_ATTACH_RECOMMENDATION_FILE) {
                    val attachedFiles = createFieldExpand(name, (item as ProfileExpandFieldItem).getFieldExpand().defaultFields, array)
                    (item as ProfileExpandFieldItem).updateExpandField(attachedFiles)
                }
            }
        }
    }

    override fun showPdfSelector() {
        val intent = Intent(context, PDFFilePickActivity::class.java)
        startActivityForResult(intent, Constant.REQUEST_CODE_PICK_FILE)
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

        validPasswords(password, passwordConfirm, oldPassword, view.tvPasswordHintLength, view.btnSave)

        view.etOldPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            oldPassword = charSequence.toString()
            validPasswords(password, passwordConfirm, oldPassword, view.tvPasswordHintLength, view.btnSave)
        })

        view.etPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            password = charSequence.toString()
            validPasswords(password, passwordConfirm, oldPassword, view.tvPasswordHintLength, view.btnSave)
        })

        view.etConfirmPassword.addTextChangedListener(SimpleTextWatcher().setOnTextChangeRunnable { charSequence, _, _, _ ->
            passwordConfirm = charSequence.toString()
            validPasswords(password, passwordConfirm, oldPassword, view.tvPasswordHintLength, view.btnSave)
        })

        view.btnSave.setOnClickListener {
            alert.dismiss()
            presenter.onChangePasswordClick(oldPassword, password)
        }

        alert.show()
    }

    private fun validPasswords(password: String, passwordConfirm: String, oldPassword: String, textView: PasswordHintTextView, button: Button) {
        val validPassword = password == passwordConfirm && AuthValidateUtil.isValidPassword(password) && oldPassword.isNotEmpty()
        textView.apply {
            if (validPassword) highlightCorrect()
            else highlightError()
        }
        button.isEnabled = validPassword
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
        super.onActivityResult(requestCode, resultCode, result)
        if (resultCode == RESULT_OK) {
            if (requestCode == Constant.REQUEST_CODE_PICK_FILE) {
                val file = result?.getParcelableExtra<NormalFile>(Constant.RESULT_PICK_FILE)
                file?.let {
                    presenter.onPdfSelected(it.path)
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

    override fun layout() = R.layout.fragment_profile_edit
}
