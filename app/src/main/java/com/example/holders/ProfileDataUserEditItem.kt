package com.example.holders

import android.graphics.Bitmap
import android.graphics.PorterDuff
import com.example.R
import com.example.data.models.Optional
import com.example.data.models.user.User
import com.example.util.USER_MIDDLE_NAME_EMPTY
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.kotlinandroidextensions.Item
import com.xwray.groupie.kotlinandroidextensions.ViewHolder
import kotlinx.android.synthetic.main.item_profile_data_user_edit.*
import onTextChanged

class ProfileDataUserEditItem(
        id: Long,
        private val avatar: Bitmap?,
        private val name: String?,
        private val surname: String?,
        private val middleName: String?,
        private val removePhotoClickListener: () -> Unit,
        private val changePhotoClickListener: () -> Unit,
        private val saveClickListener: (data: Map<String, Any?>) -> Unit,
        private val cancelClickListener: () -> Unit,
        private val onDisabledInputInfoClickListener: () -> Unit
) : Item(id) {

    private var mAvatar = avatar
    private var mName = name
    private var mSurname = surname
    private var mMiddleName = middleName

    private var mNoMiddleNameChecked = middleName == USER_MIDDLE_NAME_EMPTY

    private val isCanChangeName = middleName.isNullOrEmpty()

    private lateinit var emptyInputError: String

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.apply {
            setAvatar(this, mAvatar)

            emptyInputError = etSurname.context.getString(R.string.profile_edit_empty_field_error)

            tilSurname.initInput(surname) { mSurname = it.toString() }
            tilName.initInput(mName) { mName = it.toString() }
            tilMiddleName.initInput(mMiddleName) { mMiddleName = it.toString() }

            scNoMiddleName.apply {
                isChecked = mNoMiddleNameChecked
                isEnabled = isCanChangeName
                if (isCanChangeName) {
                    setOnCheckedChangeListener { _, isChecked ->
                        mNoMiddleNameChecked = isChecked
                        etMiddleName.apply {
                            tilMiddleName.isEnabled = !isChecked
                            if (!isEnabled) tilMiddleName.error = null
                        }
                    }
                }
            }

            btnAvatarRemove.apply {
                isEnabled = avatar != null
                setOnClickListener { removePhotoClickListener() }
            }

            btnAvatarChange.apply {
                setOnClickListener { changePhotoClickListener() }
            }

            btnSave.setOnClickListener { if (checkDataComplete(viewHolder)) saveClickListener(getDataToSave()) }
            btnRevoke.setOnClickListener { cancelClickListener() }
        }
    }

    override fun bind(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.size > 0) setAvatar(holder, (payloads.getOrNull(0) as Optional<*>).value as? Bitmap)
        else super.bind(holder, position, payloads)
    }

    private fun setAvatar(viewHolder: ViewHolder, avatar: Bitmap?) {
        this.mAvatar = avatar
        viewHolder.ivAvatar.apply {
            if (avatar != null) setImageBitmap(avatar)
            else setImageResource(R.drawable.avatar_placeholder)
        }
    }

    private fun TextInputLayout.initInput(text: String?, onTextChanged: (text: CharSequence?) -> Unit) {
        editText?.setText(text)
        error = null
        isEnabled = true
        if (isCanChangeName) {
            editText?.isEnabled = true
            editText?.onTextChanged {
                if (it?.isNotEmpty() == true) error = null
                onTextChanged(it)
            }
            setEndIconDrawable(0)
        } else {
            editText?.isEnabled = false
            setEndIconDrawable(R.drawable.ic_information)
            setEndIconTintMode(PorterDuff.Mode.MULTIPLY)
            setEndIconOnClickListener { onDisabledInputInfoClickListener() }
        }
    }

    private fun checkDataComplete(viewHolder: ViewHolder): Boolean {
        var hasError = false
        viewHolder.apply {
            if (mSurname.isNullOrEmpty()) {
                tilSurname.error = emptyInputError
                hasError = true
            }
            if (mName.isNullOrEmpty()) {
                tilName.error = emptyInputError
                hasError = true
            }
            if (!mNoMiddleNameChecked && mMiddleName.isNullOrEmpty()) {
                tilMiddleName.error = emptyInputError
                hasError = true
            }
        }

        return !hasError
    }

    private fun getDataToSave(): Map<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (name != mName) put(User.FIELD_USER_NAME, mName)
            if (surname != mSurname) put(User.FIELD_USER_LAST_NAME, mSurname)
            val middleName = if (mNoMiddleNameChecked) USER_MIDDLE_NAME_EMPTY else mMiddleName
            if (this@ProfileDataUserEditItem.middleName != middleName) put(User.FIELD_USER_MIDDLE_NAME, middleName)
            if (avatar != mAvatar) put(User.FIELD_USER_AVATAR, mAvatar)
        }
    }

    override fun getLayout() = R.layout.item_profile_data_user_edit
}