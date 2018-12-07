package com.example.ui.profile.profileEdit

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.ProfileField
import com.example.data.models.Type
import com.example.data.models.User
import com.example.holders.BrownButtonItem
import com.example.holders.ProfileExpandFieldItem
import com.example.holders.ProfileFieldItem
import com.example.ui.base.BaseFragment
import com.example.util.CropCircleTransformation
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import com.xwray.groupie.kotlinandroidextensions.Item
import kotlinx.android.synthetic.main.fragment_profile_edit.*
import javax.inject.Inject
import javax.inject.Provider

class ProfileEditFragment : BaseFragment(), ProfileEditContract.View {

    @InjectPresenter
    lateinit var presenter: ProfileEditPresenter

    @Inject
    lateinit var presenterProvider: Provider<ProfileEditPresenter>

    @ProvidePresenter
    fun providePresenter(): ProfileEditPresenter = presenterProvider.get()


    private var adapter = GroupAdapter<ViewHolder>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fieldRecyclerView.layoutManager = LinearLayoutManager(context)
        fieldRecyclerView.adapter = adapter
    }


    override fun setUser(user: User) {

        if (!user.image.isNullOrEmpty()) Picasso.get().load(user.image).transform(CropCircleTransformation()).into(ivAvatar)

        tvName.text = user.name
        tvId.text = user.id

        val listField = mutableListOf<Item>()

        listField.add(ProfileFieldItem(ProfileField("email",Type.EMAIL,getString(R.string.email),true,user.email)))
        listField.add(ProfileFieldItem(ProfileField("password",Type.PASSWORD,getString(R.string.auth_hint_password),false,null)))
        listField.add(ProfileFieldItem(ProfileField("password_one_more",Type.PASSWORD,"Повторите пароль",false,null)))
        listField.add(ProfileFieldItem(ProfileField("phone",Type.PHONE,getString(R.string.profile_phone),true,user.phone)))
        listField.add(ProfileFieldItem(ProfileField("birthday",Type.DATE,getString(R.string.profile_birthday),true,user.birthday)))
        listField.add(ProfileFieldItem(ProfileField("city",Type.TEXT,getString(R.string.profile_city),false,user.city)))
        listField.add(ProfileFieldItem(ProfileField("sn",Type.TEXT,getString(R.string.profile_sn),false,user.sn)))
        listField.add(ProfileFieldItem(ProfileField("education",Type.TEXT,getString(R.string.profile_education),false,user.educations)))

        val listFirstExpandField = mutableListOf<ProfileFieldItem>()
        listFirstExpandField.add(ProfileFieldItem(ProfileField("startEducate",Type.DATE,"Дата начала обучение",false,user.startEducate)))
        listFirstExpandField.add(ProfileFieldItem(ProfileField("endEducate",Type.DATE,"Дата окончания",false,user.endEducate)))
        listFirstExpandField.add(ProfileFieldItem(ProfileField("speciality",Type.DATE,"Спиальность",false,user.speciality)))
        listFirstExpandField.add(ProfileFieldItem(ProfileField("institution",Type.TEXT,getString(R.string.profile_institution),false,user.institution)))

        listField.add(ProfileExpandFieldItem(getString(R.string.profile_institution),listFirstExpandField,true))

        listField.add(BrownButtonItem(getString(R.string.save), View.OnClickListener {
            presenter.onSaveClick(adapter)
        }))

        adapter.update(listField)

    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_profile_edit
}
