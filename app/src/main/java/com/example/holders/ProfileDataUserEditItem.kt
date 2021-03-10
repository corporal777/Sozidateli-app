package com.example.holders

import android.graphics.Bitmap
import android.graphics.PorterDuff
import com.example.R
import com.example.data.models.Optional
import com.example.data.models.user.User
import com.example.util.USER_DATA_EMPTY
import com.google.android.material.textfield.TextInputLayout
import com.xwray.groupie.kotlinandroidextensions.GroupieViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.item_profile_data_user_edit.*
import onTextChanged

class ProfileDataUserEditItem(
        private val avatar: Bitmap?,
        private val name: String?,
        private val surname: String?,
        private val middleName: String?,
        private val userNameEdited: Boolean,
        private val removePhotoClickListener: () -> Unit,
        private val changePhotoClickListener: () -> Unit,
        private val onDisabledInputInfoClickListener: () -> Unit
) : Item() {

    private var mAvatar = avatar
    private var mName = name
    private var mSurname = surname
    private var mMiddleName = middleName

    private var mNoMiddleNameChecked = middleName == USER_DATA_EMPTY

    private val isCanChangeName = userNameEdited//middleName.isNullOrEmpty()

    private lateinit var emptyInputError: String

    private lateinit var viewHolder: GroupieViewHolder

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        this.viewHolder = viewHolder
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
        }
    }

    override fun bind(holder: GroupieViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.size > 0) setAvatar(holder, (payloads.getOrNull(0) as Optional<*>).value as? Bitmap)
        else super.bind(holder, position, payloads)
    }

    private fun setAvatar(viewHolder: GroupieViewHolder, avatar: Bitmap?) {
        this.mAvatar = avatar
        viewHolder.ivAvatar.apply {
            clipToOutline = true
            if (avatar != null) setImageBitmap(avatar)
            else setImageResource(R.drawable.avatar_placeholder_rectangle)
        }

        viewHolder.btnAvatarRemove.apply {
            isEnabled = avatar != null
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

    fun checkDataComplete(): Boolean {
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

    fun getDataToSave(): MutableMap<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (name != mName) put(User.FIELD_USER_NAME, mName)
            if (surname != mSurname) put(User.FIELD_USER_LAST_NAME, mSurname)
            val middleName = if (mNoMiddleNameChecked) USER_DATA_EMPTY else mMiddleName
            if (this@ProfileDataUserEditItem.middleName != middleName) put(User.FIELD_USER_MIDDLE_NAME, middleName)
            if (avatar != mAvatar) put(User.FIELD_USER_AVATAR, mAvatar ?: "")
        }
    }

    override fun getLayout() = R.layout.item_profile_data_user_edit
}