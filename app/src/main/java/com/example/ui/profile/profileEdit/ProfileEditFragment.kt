package com.example.ui.profile.profileEdit

import android.os.Bundle
import android.view.View
import com.arellomobile.mvp.presenter.InjectPresenter
import com.arellomobile.mvp.presenter.ProvidePresenter
import com.example.R
import com.example.data.models.ProfileField
import com.example.data.models.Type
import com.example.data.models.user.User
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
        fieldRecyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        fieldRecyclerView.adapter = adapter
    }


    override fun setUser(user: User) {

        if (!user.user_avatar.isNullOrEmpty()) Picasso.get().load(user.user_avatar).transform(CropCircleTransformation()).into(ivAvatar)

        tvName.text = user.user_name
        tvId.text = user.user_id.toString()

        val listField = mutableListOf<Item>()

        listField.add(ProfileFieldItem(ProfileField("user_email",Type.EMAIL,getString(R.string.email),true,user.user_email)))
        listField.add(ProfileFieldItem(ProfileField("user_password",Type.PASSWORD,getString(R.string.auth_hint_password),false,null)))
        listField.add(ProfileFieldItem(ProfileField("password_one_more",Type.PASSWORD,"Повторите пароль",false,null)))
        listField.add(ProfileFieldItem(ProfileField("user_phone",Type.PHONE,getString(R.string.profile_phone),true,user.user_phone)))
        listField.add(ProfileFieldItem(ProfileField("user_birthday",Type.DATE,getString(R.string.profile_birthday),true,user.user_birthday)))
        listField.add(ProfileFieldItem(ProfileField("user_address_country",Type.TEXT,getString(R.string.profile_country),false,user.user_address_country)))
        listField.add(ProfileFieldItem(ProfileField("user_address_city",Type.TEXT,getString(R.string.profile_city),false,user.user_address_city)))
        listField.add(ProfileFieldItem(ProfileField("social_links",Type.TEXT,getString(R.string.profile_sn),false,user.social_links)))
        //listField.add(ProfileFieldItem(ProfileField("education",Type.TEXT,getString(R.string.profile_education),false,user.education)))

        user.education?.let {
            for(education in it){
                val listFirstExpandField = mutableListOf<ProfileFieldItem>()
                listFirstExpandField.add(ProfileFieldItem(ProfileField("education.begin",Type.DATE,"Дата начала обучение",false,education.begin)))
                listFirstExpandField.add(ProfileFieldItem(ProfileField("education.end",Type.DATE,"Дата окончания",false,education.end)))
                listFirstExpandField.add(ProfileFieldItem(ProfileField("education.specialty",Type.DATE,"Спиальность",false,education.specialty)))
                listFirstExpandField.add(ProfileFieldItem(ProfileField("education.organization",Type.TEXT,getString(R.string.profile_institution),false,education.organization)))

                listField.add(ProfileExpandFieldItem(getString(R.string.profile_institution),listFirstExpandField,true))
            }
        }



        listField.add(BrownButtonItem(getString(R.string.save), View.OnClickListener {
            presenter.onSaveClick(adapter)
        }))

        adapter.update(listField)

    }

    override fun isShowToolbar() = true

    override fun layout() = R.layout.fragment_profile_edit
}
