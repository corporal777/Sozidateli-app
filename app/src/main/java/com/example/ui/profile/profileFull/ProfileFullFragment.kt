package com.example.ui.profile.profileFull

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.example.util.Utils
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile_full, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> presenter.onEditClick()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun showEditProfile() {
        findNavController().navigate(ProfileFullFragmentDirections.profileToEdit())
    }

    override fun setUser(user: User) {
        if (!user.user_avatar.isNullOrEmpty()) Picasso.get().load(user.user_avatar).transform(CropCircleTransformation()).into(ivAvatar)

        tvName.text = user.user_name
        tvId.text = user.user_id.toString()

        /*setVisibleField(llEmail, user.email)
        user.email?.let {
            tvEmail.text = it
        }*/

        setVisibleField(llPhone, user.user_phone)
        user.user_phone?.let {
            tvPhone.text = it
        }

        setVisibleField(llBirthday, user.user_birthday)
        tvBirthday.text = user.user_birthday

        setVisibleField(llCity, user.user_address_city)
        user.user_address_city?.let {
            tvCity.text = it
        }

        /*setVisibleField(llSN, user.sn)
        user.sn?.let {
            tvSn.text = it
        }*/

        /*setVisibleField(llEducation, user.educations)
        user.educations?.let {
            tvEducation.text = it
        }*/

        /*eiInstitution.setName(getString(R.string.profile_institution))
        eiInstitution.setDataInfo(hashMapOf(
                "" to Utils.getDatesInterval(user.startEducate,user.endEducate),
                "Специальность" to user.speciality,
                getString(R.string.profile_institution) to user.institution
        ))*/
    }

    private fun setVisibleField(view: View, data: Any?) {
        val visibility = if (data == null) View.GONE else View.VISIBLE
        view.visibility = visibility
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_profile_full
}
