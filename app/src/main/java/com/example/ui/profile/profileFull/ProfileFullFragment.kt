package com.example.ui.profile.profileFull

import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.User
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_profile_full.*
import javax.inject.Inject
import javax.inject.Provider

class ProfileFullFragment : BaseFragment(), ProfileFullContract.View {

    @InjectPresenter
    lateinit var presenter: ProfileFullPresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfileFullPresenter>

    @ProvidePresenter
    fun providePresenter(): ProfileFullPresenter = presenterProvider.get()

    override fun setUser(user: User) {
        if (!user.image.isNullOrEmpty()) Picasso.get().load(user.image).transform(CropCircleTransformation()).into(ivAvatar)

        tvName.text = user.name
        tvId.text = user.id.toString()

        setVisibleField(llEmail, user.email)
        user.email?.let {
            tvEmail.text = it
        }

        setVisibleField(llPhone, user.phone)
        user.phone?.let {
            tvPhone.text = it
        }

        setVisibleField(llBirthday, user.birthday)
        tvBirthday.text = user.birthday.toString()

        setVisibleField(llCity, user.city)
        user.city?.let {
            tvCity.text = it
        }

        setVisibleField(llSN, user.sn)
        user.sn?.let {
            tvSn.text = it
        }

        setVisibleField(llEducation, user.educations)
        user.educations?.let {
            tvEducation.text = it
        }

        eiInstitution.setName(getString(R.string.profile_institution))
        eiInstitution.setDataInfo(hashMapOf(
                "" to "2013 - н.в.",
                "Специальность" to "Технолог",
                "Учебное заведение" to "Коледж имени ПТУ"
        ))
    }

    private fun setVisibleField(view: View, data: Any?) {
        val visibility = if (data == null) View.GONE else View.VISIBLE
        view.visibility = visibility
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_profile_full
}
