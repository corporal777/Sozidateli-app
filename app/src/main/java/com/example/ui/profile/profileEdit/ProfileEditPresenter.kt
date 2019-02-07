package com.example.ui.profile.profileEdit

import com.arellomobile.mvp.InjectViewState
import com.example.data.AppData
import com.example.data.models.ProfileField
import com.example.data.models.user.User
import com.example.holders.ProfileExpandFieldItem
import com.example.holders.ProfileFieldItem
import com.example.repository.ChatRepository
import com.example.ui.base.BasePresenter
import com.example.ui.profile.profileFull.ProfileFullContract
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import javax.inject.Inject

@InjectViewState
class ProfileEditPresenter
@Inject constructor(private val appData: AppData
) : BasePresenter<ProfileEditContract.View>(), ProfileEditContract.Presenter {


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        appData.addOnUserChangeListener(object:AppData.OnUserChangeListener{
            override fun onUserChange(user: User?) {
                user?.let {
                    viewState.setUser(it)
                }
            }
        })
    }

    override fun attachView(view: ProfileEditContract.View?) {
        super.attachView(view)
        viewState.setUser(appData.getUser())
    }

    override fun onSaveClick(groupAdapter: GroupAdapter<ViewHolder>) {
        val arrayField = mutableListOf<ProfileField>()
        for (i in 0 until groupAdapter.itemCount) {
            val item = groupAdapter.getItem(i)

            item.let {
                when (it) {
                    is ProfileFieldItem -> {
                        arrayField.add(it.getField())
                    }
                    is ProfileExpandFieldItem -> {
                        it.getFieldItems().forEach { profileItem ->
                            arrayField.add(profileItem.getField())
                        }
                    }
                    else -> {
                    }
                }
            }
        }

        arrayField.forEach {
            try {
                val field = appData.getUser()::class.java.getDeclaredField(it.nameField)
                field.isAccessible = true
                field.set(appData.getUser(), it.data)
            } catch (e: NoSuchFieldException) {
                e.printStackTrace()
            }
        }

        viewState.navigateUp()
    }
}
