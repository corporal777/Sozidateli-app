package com.example.ui.auth

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import com.example.R
import com.example.TestUtils
import com.example.TestUtils.waitFor
import com.example.ui.main.MainActivity
import org.hamcrest.Matchers.allOf
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun loginTest() {
        TestUtils.doScreenShot("Auth-Account")

        AuthorizationScreenTest().authorizationWithLogin()

        TestUtils.doScreenShot("Login-Screen")

        onView(isRoot()).perform(waitFor(2000))

        val editTextLogin = onView(TestUtils.findCustomEditText(R.id.etLogin))

        editTextLogin.perform(replaceText("podxvat777@gmail.com"))
        editTextLogin.perform(pressImeActionButton())

        TestUtils.doScreenShot("Email-Typed-Screen")
        onView(isRoot()).perform(waitFor(2000))

        val editTextPassword = onView(TestUtils.findCustomEditText(R.id.etPassword))
        editTextPassword.perform(replaceText("Qwerty123"))
        editTextPassword.perform(pressImeActionButton())

        TestUtils.doScreenShot("Password-Typed-Screen")
        onView(isRoot()).perform(waitFor(2000))

        val btnLogin = onView(
            allOf(withId(R.id.btn_login), isDisplayed())
        )
        btnLogin.perform(click())
        TestUtils.doScreenShot("Login-Pressed-Screen")

        onView(isRoot()).perform(waitFor(5000))

        TestUtils.doScreenShot("Welcome-Screen")
    }
}