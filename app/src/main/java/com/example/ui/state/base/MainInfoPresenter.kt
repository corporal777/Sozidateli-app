package com.example.ui.state.base

import android.util.Log
import call
import com.example.data.AppData
import com.example.data.bodies.BirthdayBody
import com.example.data.models.FieldDetails
import com.example.data.models.ImageModel
import com.example.data.models.ToggleStringModel
import com.example.data.models.UserAddress
import com.example.data.models.UserAddressBody
import com.example.data.models.UserDetail
import com.example.extensions.formatToDefaultDate
import com.example.extensions.formatToDefaultServerDate
import com.example.extensions.phoneToServer
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.base.BasePresenter
import com.example.ui.state.UserState
import com.example.util.GENDER_FEMALE
import com.example.util.GENDER_MALE
import com.example.util.PHONE_PERSONAL
import com.example.util.Utils
import com.example.util.rxtakephoto.RxTakePhoto
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.plusAssign
import moxy.InjectViewState
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity
import withCustomLoading
import withProgressBarDialogLoading
import javax.inject.Inject


@InjectViewState
class MainInfoPresenter
@Inject constructor(
    private val appData: AppData,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
) : BasePresenter<MainInfoContract.View>(appData), MainInfoContract.Presenter {

    lateinit var type: UserState
    var screen: Int = 1

    private var userGender: String? = null
    private var isShownUserGender: Boolean? = false
    private var userBirthday: String? = null
    private var isShownUserBirthday: Boolean? = false
    private var userAddressRegion: String? = null
    private var userAddressCity: String? = null
    private var isShownUserAddress: Boolean? = false


    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        compositeDisposable += Maybe.defer { Maybe.just(getUserData()) }
            .performOnBackgroundOutOnMain()
            .subscribeSimple(
                onError = {
                    it.printStackTrace()
                    viewState.navigateUp()
                },
                onSuccess = { viewState.setPersonalData(it) }
            )
    }

    override fun attachView(view: MainInfoContract.View?) {
        super.attachView(view)
        viewState.setUserAvatar(getUserData())
        performDataChange()
    }

    override fun onSaveData() {
        if (!isDataValid()) return
        compositeDisposable += Completable.defer {
            val data = getDataToSave()
            if (data.isEmpty()) Completable.complete()
            else userRepository.updateUserProfile(appData.getId(), data).ignoreElement()
        }
            .andThen(userRepository.checkUserProfileSingle())
            .performOnBackgroundOutOnMain()
            .withCustomLoading(viewState)
            .subscribeSimple(
                onError = { onReceiveError(it) },
                onSuccess = { viewState.goToNext() }
            )
    }

    override fun checkEmailIsUnique(withCheck: Boolean, email: String) {
        compositeDisposable += Completable.defer {
            if (withCheck) userRepository.checkEmailPhone(email, null)
            else Completable.complete()
        }
            .doOnComplete {
                if (getEmail()?.value.isNullOrEmpty())
                    getUserData().email?.value = email
                else getUserData().email?.onConfirmation = email
            }
            .performOnBackgroundOutOnMain()
            .withProgressBarDialogLoading(viewState)
            .subscribeSimple(
                onError = { viewState.showEmailNotUnique(email) },
                onComplete = { viewState.showEmailConfirm(email) }
            )
    }

    override fun onUpdateImage() {
        viewState.setUserAvatar(getUserData())
        performDataChange()
    }

    override fun onChangeBirthday(birthday: String) {
        userBirthday = birthday
        performDataChange()
    }

    override fun onChangeGender(gender: String) {
        userGender = gender
        performDataChange()
    }

    override fun onChangeRegion(region: String) {
        userAddressRegion = region
        performDataChange()
    }

    override fun onChangeCity(city: String) {
        userAddressCity = city
        performDataChange()
    }

    override fun onChangeShowGender(show: Boolean) {
        isShownUserGender = show
    }

    override fun onChangeShowBirthday(show: Boolean) {
        isShownUserBirthday = show
    }

    override fun onChangeShowAddress(show: Boolean) {
        isShownUserAddress = show
    }

    private fun performDataChange() = viewState.enableBtnSave(isDataValid())

    private fun isDataValid(): Boolean {
        return !userGender.isNullOrEmpty()
                && !userBirthday.isNullOrEmpty()
                && !userAddressRegion.isNullOrEmpty()
                && !getUserData().personalPhone?.value.isNullOrEmpty()
                && !getUserData().loadUserNotDefaultImage().isNullOrEmpty()
    }


    fun getEmail() = appData.getUser().email

    private fun getGender(): String? = when (userGender) {
        "Мужской" -> GENDER_MALE
        "Женский" -> GENDER_FEMALE
        else -> null
    }

    fun setGender(gender: String?): String = when (gender) {
        GENDER_MALE -> "Мужской"
        GENDER_FEMALE -> "Женский"
        else -> ""
    }

    private fun getDataToSave(): MutableMap<String, Any?> {
        return mutableMapOf<String, Any?>().apply {
            if (getUserData().gender?.value != getGender() || getUserData().gender?.showInProfile != isShownUserGender) {
                put(
                    UserDetail.USER_GENDER,
                    ToggleStringModel(getGender(), isShownUserGender ?: false)
                )
            }
            if (getUserData().birthday?.value?.formatToDefaultDate() != userBirthday || getUserData().birthday?.isVisible != isShownUserBirthday) {
                val birthday = userBirthday?.formatToDefaultServerDate()
                put(UserDetail.USER_BIRTHDAY, BirthdayBody(birthday, isShownUserBirthday))
            }
            if (getUserData().address?.region != userAddressRegion || getUserData().address?.city != userAddressCity || getUserData().address?.showInProfile != isShownUserAddress) {
                put(
                    UserDetail.USER_ADDRESS, UserAddressBody(
                        getUserData().address?.index,
                        userAddressRegion,
                        userAddressCity,
                        getUserData().address?.fullValue,
                        getUserData().address?.shortAddres,
                        isShownUserAddress ?: false
                    )
                )
            }
        }
    }
}