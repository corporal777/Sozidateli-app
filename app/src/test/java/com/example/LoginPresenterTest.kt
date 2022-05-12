package com.example

import androidx.test.platform.app.InstrumentationRegistry
import com.example.data.AppData
import com.example.data.bodies.AuthBody
import com.example.data.bodies.LoginModel
import com.example.data.prefs.AppPrefs
import com.example.repository.AuthRepository
import com.example.repository.UserRepository
import com.example.ui.auth.login.LoginPresenter
import com.example.ui.snAuth.SnAuthManager
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import performOnBackgroundOutOnMain
import withCheckInternetConnectivity


@RunWith(RobolectricTestRunner::class)

class LoginPresenterTest {

//    @Mock
//    val viewState: LoginContract.View? = null

    @Mock
    private lateinit var authRepository: AuthRepository

    @Mock
    private val phoneNumberUtil: PhoneNumberUtil? = null

    @Mock
    private val userRepository: UserRepository? = null

    @Mock
    private lateinit var appPrefs: AppPrefs

    private var appData: AppData? = null


    private val snAuthManager: SnAuthManager? = null

    private lateinit var mPresenter: LoginPresenter

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().context
        MockitoAnnotations.initMocks(this)
        val mAppData = AppData(appPrefs)
        val mSnAuthManager = SnAuthManager(context)
        mPresenter =
            LoginPresenter(
                authRepository,
                phoneNumberUtil!!,
                userRepository!!,
                // appData!!,
                mAppData!!,
                //snAuthManager!!
                mSnAuthManager!!
            )
    }


    @Test
    fun getData() {

        val mLogin = "t_3@houseofapps.ru"
        val mPassword = "Lera1801"
        //mPresenter.onClickLogin(mLogin, mPassword, -1)

        val compositeDisposable = authRepository.authEmailOrPhone(
            AuthBody(
                LoginModel("email", mLogin),
                LoginModel("common", mPassword)
            )
        )
//            .withCheckInternetConnectivity()
//            .performOnBackgroundOutOnMain()
            .subscribe({

            }, {

            })
    }

}