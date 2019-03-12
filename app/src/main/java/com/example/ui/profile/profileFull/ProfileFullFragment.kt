package com.example.ui.profile.profileFull

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.user.User
import com.example.holders.InfoProfileExpandFieldItem
import com.example.holders.InfoProfileFieldItem
import com.example.holders.ProfileHeaderItem
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.example.util.Utils
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
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

    private var adapter = GroupAdapter<ViewHolder>()

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

        val phoneUtill = PhoneNumberUtil.createInstance(context)


        val listField = mutableListOf<Item>()

        listField.add(ProfileHeaderItem(user.fullName, user.user_avatar, user.user_id))

        user.user_email?.let {
            listField.add(InfoProfileFieldItem(getString(R.string.email), arrayListOf(it)))
        }

        user.user_phone?.let {
            val phone = phoneUtill.parse(it, "RU")
            listField.add(InfoProfileFieldItem(getString(R.string.profile_phone), arrayListOf(phoneUtill.format(phone, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL))))
        }

        user.user_phone_work?.let {
            val phone = phoneUtill.parse(it, "RU")
            listField.add(InfoProfileFieldItem(getString(R.string.profile_phone_work), arrayListOf(phoneUtill.format(phone, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL))))
        }

        user.user_birthday?.let {
            listField.add(InfoProfileFieldItem(getString(R.string.profile_birthday), arrayListOf(it)))
        }

        user.user_gender?.let {
            listField.add(InfoProfileFieldItem(getString(R.string.profile_gender), arrayListOf(it.capitalize())))
        }

        user.user_address_country?.let {
            listField.add(InfoProfileFieldItem(getString(R.string.profile_country), arrayListOf(it)))
        }

        user.user_address_city?.let {
            listField.add(InfoProfileFieldItem(getString(R.string.profile_city), arrayListOf(it)))
        }

        user.social_links?.let {
            listField.add(InfoProfileFieldItem(getString(R.string.profile_sn), it))
        }

        user.academic_degree?.let {
            listField.add(InfoProfileFieldItem(getString(R.string.profile_education), it))
        }

        user.education?.let {
            val list = mutableListOf<HashMap<String, String?>>()
            for (item in it) {
                list.add(hashMapOf(
                        "" to Utils.getDatesInterval(item.begin, item.end),
                        getString(R.string.profile_educate_speciality) to item.specialty,
                        getString(R.string.profile_educate_name) to item.organization
                ))
            }
            addToList(listField, list, getString(R.string.profile_institution))
        }

        user.work?.let {
            val list = mutableListOf<HashMap<String, String?>>()

            for (item in it) {
                list.add(hashMapOf(
                        "" to Utils.getDatesInterval(item.begin, item.end),
                        getString(R.string.profile_work_organization) to item.organization,
                        getString(R.string.profile_work_position) to item.position,
                        getString(R.string.profile_work_description) to item.description
                ))
            }
            addToList(listField, list, getString(R.string.profile_work_experience))
        }

        user.social_projects?.let {
            val list = mutableListOf<HashMap<String, String?>>()
            for (item in it) {
                list.add(hashMapOf(
                        "" to Utils.getDatesInterval(item.begin, item.end),
                        getString(R.string.profile_social_project_name) to item.name,
                        getString(R.string.profile_work_position) to item.role,
                        getString(R.string.profile_work_description) to item.description
                ))
            }
            addToList(listField, list, getString(R.string.profile_social_project))
        }

        user.attached_recomendation_files?.let {
            val list = mutableListOf<HashMap<String, String?>>()
            for (item in it) {
                list.add(hashMapOf(
                        getString(R.string.profile_attached_file_name) to item.name,
                        getString(R.string.profile_attached_file_desc) to item.desc,
                        getString(R.string.profile_attached_file_url) to item.url
                ))
            }
            addToList(listField, list, getString(R.string.profile_attached_file))
        }

        adapter.clear()
        adapter.addAll(listField)

        fieldRecyclerView.apply {
            this.adapter = this@ProfileFullFragment.adapter
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
        }
    }

    private fun addToList(mainList: MutableList<Item>, child: MutableList<HashMap<String, String?>>, label: String) {
        if (!child.isEmpty()) {
            mainList.add(InfoProfileExpandFieldItem(label, child))
        }
    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_profile_full
}
